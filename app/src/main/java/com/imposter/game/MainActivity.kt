package com.imposter.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.imposter.game.data.WordCategories
import com.imposter.game.model.GamePhase
import com.imposter.game.ui.screens.CluesScreen
import com.imposter.game.ui.screens.HomeScreen
import com.imposter.game.ui.screens.LobbyScreen
import com.imposter.game.ui.screens.RevealScreen
import com.imposter.game.ui.screens.ResultScreen
import com.imposter.game.ui.screens.SettingsScreen
import com.imposter.game.ui.screens.VotingScreen
import com.imposter.game.ui.theme.ImposterTheme
import com.imposter.game.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImposterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent,
                ) {
                    AppRoot(viewModel)
                }
            }
        }
    }
}

private enum class AppScreen { HOME, SETTINGS, LOBBY, REVEAL, CLUES, VOTING, RESULT }

@Composable
private fun AppRoot(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val used by viewModel.usedWords.collectAsState()
    var showHome by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }

    val totalPool = remember { WordCategories.all.sumOf { it.words.size } }
    val activePool = remember(state.settings.selectedCategoryIds) {
        val selected = state.settings.selectedCategoryIds
        if (selected.isEmpty()) totalPool
        else WordCategories.all.filter { it.id in selected }.sumOf { it.words.size }
    }

    val screen = when {
        showSettings -> AppScreen.SETTINGS
        showHome && state.phase == GamePhase.LOBBY -> AppScreen.HOME
        state.phase == GamePhase.LOBBY -> AppScreen.LOBBY
        state.phase == GamePhase.REVEAL -> AppScreen.REVEAL
        state.phase == GamePhase.CLUES -> AppScreen.CLUES
        state.phase == GamePhase.VOTING -> AppScreen.VOTING
        state.phase == GamePhase.REVEAL_RESULT -> AppScreen.RESULT
        else -> AppScreen.LOBBY
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            val enterDir = if (forward) 1 else -1
            (slideInHorizontally(animationSpec = tween(320)) { it * enterDir } + fadeIn(tween(220))) togetherWith
                (slideOutHorizontally(animationSpec = tween(320)) { -it * enterDir } + fadeOut(tween(180)))
        },
        label = "screen",
    ) { target ->
        when (target) {
            AppScreen.SETTINGS -> SettingsScreen(
                settings = state.settings,
                onChange = { viewModel.updateSettings { _ -> it } },
                onBack = { showSettings = false },
                playerCount = state.players.size,
                usedWordCount = used.size,
                totalWordPool = activePool,
                onClearHistory = { viewModel.clearUsedWords() },
            )
            AppScreen.HOME -> HomeScreen(
                onPlay = { showHome = false },
                onSettings = { showSettings = true },
            )
            AppScreen.LOBBY -> LobbyScreen(
                state = state,
                onAddPlayer = { viewModel.addPlayer() },
                onRemovePlayer = { viewModel.removePlayer(it) },
                onRename = { id, name -> viewModel.renamePlayer(id, name) },
                onSetAvatar = { id, path -> viewModel.setAvatar(id, path) },
                onOpenSettings = { showSettings = true },
                onBack = { showHome = true },
                onStart = { viewModel.startRound() },
            )
            AppScreen.REVEAL -> RevealScreen(
                state = state,
                onMarkRevealed = { viewModel.markCurrentRevealed() },
                onNext = { viewModel.nextReveal() },
            )
            AppScreen.CLUES -> CluesScreen(
                state = state,
                onGoToVote = { viewModel.goToVoting() },
            )
            AppScreen.VOTING -> VotingScreen(
                state = state,
                onAccuse = { viewModel.accuse(it) },
            )
            AppScreen.RESULT -> ResultScreen(
                state = state,
                onPlayAgain = { viewModel.startRound() },
                onEndSession = { viewModel.goToLobby() },
            )
        }
    }
}
