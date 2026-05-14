package com.imposter.game.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.imposter.game.data.AvatarStore
import com.imposter.game.data.UsedWordsRepository
import com.imposter.game.data.WordCategories
import com.imposter.game.data.WordCategory
import com.imposter.game.model.GamePhase
import com.imposter.game.model.GameSettings
import com.imposter.game.model.ImposterCountMode
import com.imposter.game.model.Player
import com.imposter.game.model.Role
import com.imposter.game.model.RoundPlayer
import com.imposter.game.model.WinResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class GameUiState(
    val phase: GamePhase = GamePhase.LOBBY,
    val players: List<Player> = emptyList(),
    val settings: GameSettings = GameSettings(),
    val roundPlayers: List<RoundPlayer> = emptyList(),
    val currentRevealIndex: Int = 0,
    val secretWord: String = "",
    val category: WordCategory? = null,
    val firstClueGiver: Player? = null,
    val lastWinResult: WinResult? = null,
    val roundNumber: Int = 0,
    val nextStarter: Player? = null,
    val historyJustReset: Boolean = false,
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val usedRepo = UsedWordsRepository(application)

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    val usedWords: StateFlow<Set<String>> = usedRepo.used

    private var nextId: Int = 0

    init {
        addPlayer("Spieler 1")
        addPlayer("Spieler 2")
        addPlayer("Spieler 3")
        addPlayer("Spieler 4")
    }

    fun addPlayer(name: String = "Spieler ${_state.value.players.size + 1}") {
        val players = _state.value.players + Player(
            id = nextId++,
            name = name.ifBlank { "Spieler ${_state.value.players.size + 1}" },
        )
        _state.value = _state.value.copy(players = players)
    }

    fun removePlayer(id: Int) {
        val target = _state.value.players.firstOrNull { it.id == id }
        AvatarStore.delete(target?.avatarPath)
        _state.value = _state.value.copy(players = _state.value.players.filter { it.id != id })
    }

    fun setAvatar(id: Int, path: String?) {
        val current = _state.value.players.firstOrNull { it.id == id }
        if (current?.avatarPath != null && current.avatarPath != path) {
            AvatarStore.delete(current.avatarPath)
        }
        _state.value = _state.value.copy(
            players = _state.value.players.map { if (it.id == id) it.copy(avatarPath = path) else it },
        )
    }

    fun clearAvatar(id: Int) = setAvatar(id, null)

    fun renamePlayer(id: Int, name: String) {
        _state.value = _state.value.copy(
            players = _state.value.players.map { if (it.id == id) it.copy(name = name) else it },
        )
    }

    fun clearPlayers() {
        _state.value = _state.value.copy(players = emptyList())
    }

    fun updateSettings(transform: (GameSettings) -> GameSettings) {
        _state.value = _state.value.copy(settings = transform(_state.value.settings))
    }

    fun reconcileSettings(playerCount: Int) {
        val s = _state.value.settings
        val fixed = s.fixedImposters.coerceIn(0, playerCount)
        val randomMin = s.randomMin.coerceIn(0, playerCount)
        val randomMax = s.randomMax.coerceIn(randomMin, playerCount)
        if (fixed != s.fixedImposters || randomMin != s.randomMin || randomMax != s.randomMax) {
            _state.value = _state.value.copy(
                settings = s.copy(
                    fixedImposters = fixed,
                    randomMin = randomMin,
                    randomMax = randomMax,
                ),
            )
        }
    }

    fun clearUsedWords() {
        usedRepo.clear()
    }

    fun resetSession() {
        _state.value = _state.value.copy(
            players = _state.value.players.map { it.copy(score = 0) },
            phase = GamePhase.LOBBY,
            lastWinResult = null,
            roundNumber = 0,
        )
    }

    fun startRound() {
        val current = _state.value
        val playerCount = current.players.size
        if (playerCount < 3) return

        val settings = current.settings
        val availableCategories = if (settings.selectedCategoryIds.isEmpty()) {
            WordCategories.all
        } else {
            WordCategories.all.filter { it.id in settings.selectedCategoryIds }
        }.ifEmpty { WordCategories.all }

        val used = usedRepo.used.value
        val candidates = availableCategories.flatMap { cat -> cat.words.map { cat to it } }
        var unused = candidates.filter { (_, w) -> w !in used }
        var historyReset = false
        if (unused.isEmpty()) {
            usedRepo.clear()
            unused = candidates
            historyReset = true
        }
        val (category, word) = unused.random()
        usedRepo.markUsed(word)

        val imposterCount = computeImposterCount(settings, playerCount)

        val orderedPlayers = current.players.toMutableList().apply { shuffle() }
        val forced = current.nextStarter
        if (forced != null) {
            val idx = orderedPlayers.indexOfFirst { it.id == forced.id }
            if (idx > 0) {
                val target = orderedPlayers.removeAt(idx)
                orderedPlayers.add(0, target)
            }
        }
        val imposterIndices = orderedPlayers.indices.shuffled().take(imposterCount).toSet()

        val roundPlayers = orderedPlayers.mapIndexed { idx, p ->
            val isImposter = idx in imposterIndices
            RoundPlayer(
                player = p,
                role = if (isImposter) Role.Imposter else Role.Crew(word),
            )
        }

        val firstClueGiver = roundPlayers.firstOrNull()?.player

        _state.value = current.copy(
            phase = GamePhase.REVEAL,
            roundPlayers = roundPlayers,
            currentRevealIndex = 0,
            secretWord = word,
            category = category,
            firstClueGiver = firstClueGiver,
            roundNumber = current.roundNumber + 1,
            nextStarter = null,
            historyJustReset = historyReset,
        )
    }

    private fun computeImposterCount(settings: GameSettings, playerCount: Int): Int {
        val maxAllowed = playerCount
        return when (settings.imposterMode) {
            ImposterCountMode.FIXED -> settings.fixedImposters.coerceIn(0, maxAllowed)
            ImposterCountMode.RANDOM -> {
                val lo = settings.randomMin.coerceIn(0, maxAllowed)
                val hi = settings.randomMax.coerceIn(lo, maxAllowed)
                if (lo == hi) lo else Random.nextInt(lo, hi + 1)
            }
            ImposterCountMode.AUTO -> {
                val auto = when {
                    playerCount <= 3 -> 1
                    playerCount <= 5 -> Random.nextInt(1, 3)
                    playerCount <= 8 -> Random.nextInt(1, 4)
                    playerCount <= 12 -> Random.nextInt(2, 5)
                    else -> Random.nextInt(2, 6)
                }
                auto.coerceIn(0, maxAllowed)
            }
        }
    }

    fun markCurrentRevealed() {
        val current = _state.value
        val updated = current.roundPlayers.toMutableList()
        updated[current.currentRevealIndex] = updated[current.currentRevealIndex].copy(revealed = true)
        _state.value = current.copy(roundPlayers = updated)
    }

    fun nextReveal() {
        val current = _state.value
        if (current.currentRevealIndex < current.roundPlayers.size - 1) {
            _state.value = current.copy(currentRevealIndex = current.currentRevealIndex + 1)
        } else {
            _state.value = current.copy(phase = GamePhase.CLUES)
        }
    }

    fun goToVoting() {
        _state.value = _state.value.copy(phase = GamePhase.VOTING)
    }

    fun accuse(player: Player?) {
        val current = _state.value
        val imposters = current.roundPlayers
            .filter { it.role is Role.Imposter }
            .map { it.player }
        val totalPlayers = current.players.size
        val crewWon = when {
            imposters.isEmpty() -> true
            imposters.size == totalPlayers -> false
            else -> player != null && imposters.any { it.id == player.id }
        }
        val result = WinResult(
            crewWon = crewWon,
            accusedPlayer = player,
            imposters = imposters,
            secretWord = current.secretWord,
            category = current.category?.name ?: "",
        )

        val updatedPlayers = current.players.map { p ->
            val isImposter = imposters.any { it.id == p.id }
            val gain = when {
                imposters.isEmpty() -> 1
                imposters.size == totalPlayers -> 2
                crewWon && !isImposter -> 1
                !crewWon && isImposter -> 2
                else -> 0
            }
            if (gain > 0) p.copy(score = p.score + gain) else p
        }

        _state.value = current.copy(
            players = updatedPlayers,
            phase = GamePhase.REVEAL_RESULT,
            lastWinResult = result,
            nextStarter = current.players.randomOrNull(),
        )
    }

    fun goToLobby() {
        _state.value = _state.value.copy(phase = GamePhase.LOBBY)
    }
}
