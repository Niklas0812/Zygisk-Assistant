package com.imposter.game.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.model.Role
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.CrewGreen
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState
import kotlinx.coroutines.delay

@Composable
fun RevealScreen(
    state: GameUiState,
    onMarkRevealed: () -> Unit,
    onNext: () -> Unit,
) {
    val roundPlayer = state.roundPlayers.getOrNull(state.currentRevealIndex) ?: return
    var revealing by remember(state.currentRevealIndex) { mutableStateOf(false) }
    var revealed by remember(state.currentRevealIndex) { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(revealing) {
        if (revealing && !revealed) {
            delay(150)
            onMarkRevealed()
            revealed = true
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Round ${state.roundNumber}",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pass the phone to",
                color = TextSecondary,
                fontSize = 16.sp,
            )
            Text(
                text = roundPlayer.player.name,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            Spacer(Modifier.height(12.dp))

            ProgressDots(
                total = state.roundPlayers.size,
                current = state.currentRevealIndex,
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = revealing,
                    transitionSpec = {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(220))
                    },
                    label = "reveal",
                ) { isRevealing ->
                    if (!isRevealing) {
                        TapToRevealCard(
                            onPressStart = { revealing = true },
                            onPressEnd = {
                                if (revealed) {
                                    revealing = false
                                }
                            },
                        )
                    } else {
                        RoleCard(role = roundPlayer.role, categoryName = state.category?.name ?: "")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = if (state.currentRevealIndex < state.roundPlayers.size - 1) "Pass to next player" else "Start clue round",
                onClick = onNext,
                enabled = roundPlayer.revealed,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (roundPlayer.revealed) "Don't peek at others' cards!" else "Hold the card to reveal your role",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TapToRevealCard(onPressStart: () -> Unit, onPressEnd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(BgCard, Accent.copy(alpha = 0.45f)),
                ),
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.any { it.pressed }
                        if (pressed) onPressStart() else onPressEnd()
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = null,
                tint = AccentBright,
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "HOLD TO REVEAL",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Make sure no one else is looking",
                color = TextSecondary,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun RoleCard(role: Role, categoryName: String) {
    val isImposter = role is Role.Imposter
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isImposter) ImposterRed else CrewGreen,
                        if (isImposter) Color(0xFF7F1D1D) else Color(0xFF14532D),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(
                text = if (isImposter) "🃏" else "🔑",
                fontSize = 56.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isImposter) "YOU ARE THE IMPOSTER" else "Your word",
                color = Color.White,
                fontSize = if (isImposter) 22.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            when (role) {
                is Role.Crew -> {
                    Text(
                        text = role.word,
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = categoryName,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 16.sp,
                    )
                }
                Role.Imposter -> {
                    Text(
                        text = "Bluff a word that fits the category.\nDon't get caught!",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Category: $categoryName",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressDots(total: Int, current: Int) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { idx ->
            val active = idx <= current
            Box(
                modifier = Modifier
                    .size(width = if (idx == current) 24.dp else 8.dp, height = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (active) AccentBright else Color.White.copy(alpha = 0.18f)),
            )
        }
    }
}
