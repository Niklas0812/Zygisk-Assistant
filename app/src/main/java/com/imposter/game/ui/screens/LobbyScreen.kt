package com.imposter.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState

@Composable
fun LobbyScreen(
    state: GameUiState,
    onAddPlayer: () -> Unit,
    onRemovePlayer: (Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit,
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconCircleButton(icon = Icons.Filled.ArrowBack, onClick = onBack)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Players",
                    color = Color.White,
                    fontSize = 22.sp,
                )
                Spacer(Modifier.weight(1f))
                IconCircleButton(icon = Icons.Filled.Settings, onClick = onOpenSettings)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "${state.players.size} players · imposters ${imposterText(state)}",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                items(state.players, key = { it.id }) { player ->
                    PlayerRow(
                        name = player.name,
                        score = player.score,
                        canRemove = state.players.size > 3,
                        onRename = { onRename(player.id, it) },
                        onRemove = { onRemovePlayer(player.id) },
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BgCard.copy(alpha = 0.5f))
                            .clickable(enabled = state.players.size < 16) { onAddPlayer() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = AccentBright,
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (state.players.size < 16) "Add player" else "Maximum 16 players",
                                color = AccentBright,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }

            if (state.players.size < 3) {
                Text(
                    text = "Add at least 3 players to start",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }

            PrimaryButton(
                text = "Start Round",
                onClick = onStart,
                enabled = state.players.size >= 3,
                leading = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                    )
                },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun imposterText(state: GameUiState): String {
    val s = state.settings
    return when (s.imposterMode) {
        com.imposter.game.model.ImposterCountMode.FIXED -> s.fixedImposters.toString()
        com.imposter.game.model.ImposterCountMode.RANDOM -> "random ${s.randomMin}-${s.randomMax}"
        com.imposter.game.model.ImposterCountMode.AUTO -> "auto"
    }
}

@Composable
private fun PlayerRow(
    name: String,
    score: Int,
    canRemove: Boolean,
    onRename: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Accent.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = AccentBright,
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = name,
                onValueChange = onRename,
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 17.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBright),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            if (score > 0) {
                Text(
                    text = "$score pts",
                    color = TextMuted,
                    fontSize = 13.sp,
                )
            }
        }
        if (canRemove) {
            IconCircleButton(icon = Icons.Filled.Close, onClick = onRemove, size = 36.dp)
        }
    }
}

@Composable
fun IconCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
    }
}
