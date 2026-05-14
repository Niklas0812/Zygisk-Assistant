package com.imposter.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.model.Role
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PlayerAvatar
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.CrewGreen
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState

@Composable
fun RevealScreen(
    state: GameUiState,
    onMarkRevealed: () -> Unit,
    onNext: () -> Unit,
) {
    val roundPlayer = state.roundPlayers.getOrNull(state.currentRevealIndex) ?: return
    var revealing by remember(state.currentRevealIndex) { mutableStateOf(false) }
    var revealed by remember(state.currentRevealIndex) { mutableStateOf(false) }
    val rotation = remember(state.currentRevealIndex) { Animatable(0f) }
    var photoExpanded by remember(state.currentRevealIndex) { mutableStateOf(false) }

    LaunchedEffect(revealing) {
        if (revealing) {
            rotation.animateTo(180f, tween(500))
            if (!revealed) {
                onMarkRevealed()
                revealed = true
            }
        } else {
            rotation.animateTo(0f, tween(380))
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
                text = "Runde ${state.roundNumber}",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(8.dp))
            PlayerAvatar(
                avatarPath = roundPlayer.player.avatarPath,
                size = 56.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(enabled = roundPlayer.player.avatarPath != null) {
                        photoExpanded = true
                    },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Handy an",
                color = TextSecondary,
                fontSize = 14.sp,
            )
            Text(
                text = roundPlayer.player.name,
                color = Color.White,
                fontSize = 28.sp,
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

            FlippableCard(
                rotation = rotation.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onPressStart = { revealing = true },
                onPressEnd = { if (revealed) revealing = false },
                front = { TapToRevealCard() },
                back = { RoleCard(role = roundPlayer.role, categoryName = state.category?.name ?: "") },
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = if (state.currentRevealIndex < state.roundPlayers.size - 1) "Weitergeben" else "Hinweisrunde starten",
                onClick = onNext,
                enabled = roundPlayer.revealed,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (roundPlayer.revealed) "Nicht spicken!" else "Karte halten",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(
            visible = photoExpanded && roundPlayer.player.avatarPath != null,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(150)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { photoExpanded = false },
                contentAlignment = Alignment.Center,
            ) {
                PlayerAvatar(
                    avatarPath = roundPlayer.player.avatarPath,
                    size = 280.dp,
                )
            }
        }
    }
}

@Composable
private fun FlippableCard(
    rotation: Float,
    modifier: Modifier = Modifier,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
) {
    val showBack = rotation > 90f
    // pointerInput key is stable across recompositions, so the lambdas it
    // captures stay alive for the lifetime of the node. Without these
    // rememberUpdatedState handles, advancing to the next player keeps the
    // old lambdas active and writes into a MutableState nothing observes —
    // i.e. the card stops responding to touch (the freeze we hit).
    val pressStart by rememberUpdatedState(onPressStart)
    val pressEnd by rememberUpdatedState(onPressEnd)
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(28.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.any { it.pressed }
                        if (pressed) pressStart() else pressEnd()
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = if (showBack) 180f else 0f },
            contentAlignment = Alignment.Center,
        ) {
            if (showBack) back() else front()
        }
    }
}

@Composable
private fun TapToRevealCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(BgCard, Accent.copy(alpha = 0.55f))),
            ),
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
                text = "AUFDECKEN",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Karte halten",
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
            when (role) {
                is Role.Crew -> {
                    Text(
                        text = "Dein Wort",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 3.sp,
                    )
                    Spacer(Modifier.height(10.dp))
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
                        text = "IMPOSTER",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Bluffe ein Wort.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
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

