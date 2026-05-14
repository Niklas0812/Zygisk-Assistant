package com.imposter.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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

@Composable
private fun AppRoot(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    var showHome by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }

    when {
        showSettings -> SettingsScreen(
            settings = state.settings,
            onChange = { viewModel.updateSettings { _ -> it } },
            onBack = { showSettings = false },
            playerCount = state.players.size,
        )
        showHome && state.phase == GamePhase.LOBBY -> HomeScreen(
            onPlay = { showHome = false },
            onSettings = { showSettings = true },
        )
        state.phase == GamePhase.LOBBY -> LobbyScreen(
            state = state,
            onAddPlayer = { viewModel.addPlayer() },
            onRemovePlayer = { viewModel.removePlayer(it) },
            onRename = { id, name -> viewModel.renamePlayer(id, name) },
            onOpenSettings = { showSettings = true },
            onBack = { showHome = true },
            onStart = { viewModel.startRound() },
        )
        state.phase == GamePhase.REVEAL -> RevealScreen(
            state = state,
            onMarkRevealed = { viewModel.markCurrentRevealed() },
            onNext = { viewModel.nextReveal() },
        )
        state.phase == GamePhase.CLUES -> CluesScreen(
            state = state,
            onGoToVote = { viewModel.goToVoting() },
        )
        state.phase == GamePhase.VOTING -> VotingScreen(
            state = state,
            onAccuse = { viewModel.accuse(it) },
        )
        state.phase == GamePhase.REVEAL_RESULT -> ResultScreen(
            state = state,
            onPlayAgain = { viewModel.startRound() },
            onEndSession = {
                viewModel.goToLobby()
            },
        )
        else -> LobbyScreen(
            state = state,
            onAddPlayer = { viewModel.addPlayer() },
            onRemovePlayer = { viewModel.removePlayer(it) },
            onRename = { id, name -> viewModel.renamePlayer(id, name) },
            onOpenSettings = { showSettings = true },
            onBack = { showHome = true },
            onStart = { viewModel.startRound() },
        )
    }
}
