package com.imposter.game.viewmodel

import androidx.lifecycle.ViewModel
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
import kotlin.math.max
import kotlin.math.min
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
)

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var nextId: Int = 0

    init {
        addPlayer("Player 1")
        addPlayer("Player 2")
        addPlayer("Player 3")
        addPlayer("Player 4")
    }

    fun addPlayer(name: String = "Player ${_state.value.players.size + 1}") {
        val players = _state.value.players + Player(id = nextId++, name = name.ifBlank { "Player ${_state.value.players.size + 1}" })
        _state.value = _state.value.copy(players = players)
    }

    fun removePlayer(id: Int) {
        _state.value = _state.value.copy(players = _state.value.players.filter { it.id != id })
    }

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

        val category = availableCategories.random()
        val word = category.words.random()

        val imposterCount = computeImposterCount(settings, playerCount)
        val shuffledIndices = current.players.indices.shuffled()
        val imposterIndices = shuffledIndices.take(imposterCount).toSet()

        val roundPlayers = current.players.shuffled().mapIndexed { idx, p ->
            val isImposter = idx in imposterIndices
            RoundPlayer(
                player = p,
                role = if (isImposter) Role.Imposter else Role.Crew(word),
            )
        }

        val firstClueGiver = roundPlayers.first { it.role !is Role.Imposter }.player

        _state.value = current.copy(
            phase = GamePhase.REVEAL,
            roundPlayers = roundPlayers,
            currentRevealIndex = 0,
            secretWord = word,
            category = category,
            firstClueGiver = firstClueGiver,
            roundNumber = current.roundNumber + 1,
        )
    }

    private fun computeImposterCount(settings: GameSettings, playerCount: Int): Int {
        val maxAllowed = max(1, playerCount / 2)
        return when (settings.imposterMode) {
            ImposterCountMode.FIXED -> min(settings.fixedImposters, maxAllowed).coerceAtLeast(1)
            ImposterCountMode.RANDOM -> {
                val lo = min(settings.randomMin, maxAllowed).coerceAtLeast(1)
                val hi = min(settings.randomMax, maxAllowed).coerceAtLeast(lo)
                Random.nextInt(lo, hi + 1)
            }
            ImposterCountMode.AUTO -> {
                when {
                    playerCount <= 4 -> 1
                    playerCount <= 7 -> Random.nextInt(1, 3)
                    playerCount <= 10 -> Random.nextInt(1, 4)
                    else -> Random.nextInt(2, 5)
                }.coerceAtMost(maxAllowed)
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
        val crewWon = player != null && imposters.any { it.id == player.id }
        val result = WinResult(
            crewWon = crewWon,
            accusedPlayer = player,
            imposters = imposters,
            secretWord = current.secretWord,
            category = current.category?.name ?: "",
        )

        val updatedPlayers = current.players.map { p ->
            val gain = when {
                crewWon && imposters.none { it.id == p.id } -> 1
                !crewWon && imposters.any { it.id == p.id } -> 2
                else -> 0
            }
            if (gain > 0) p.copy(score = p.score + gain) else p
        }

        _state.value = current.copy(
            players = updatedPlayers,
            phase = GamePhase.REVEAL_RESULT,
            lastWinResult = result,
        )
    }

    fun goToLobby() {
        _state.value = _state.value.copy(phase = GamePhase.LOBBY)
    }
}
