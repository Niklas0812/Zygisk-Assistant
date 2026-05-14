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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.Gold
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState
import kotlinx.coroutines.delay

@Composable
fun CluesScreen(state: GameUiState, onGoToVote: () -> Unit) {
    val showOrder = state.settings.showClueOrder
    val firstName = state.firstClueGiver?.name ?: state.players.firstOrNull()?.name ?: ""
    val totalImposters = state.roundPlayers.count { it.role is com.imposter.game.model.Role.Imposter }
    val total = state.roundPlayers.size
    val imposterNote = when {
        totalImposters == 0 -> "Freie Runde – kein Imposter. Jeder sagt ein Wort zum Begriff."
        totalImposters >= total -> "Chaos-Runde – alle sind Imposter. Bluffen oder auffliegen!"
        totalImposters == 1 -> "Unter euch ist 1 Imposter."
        else -> "Unter euch sind $totalImposters Imposter."
    }

    val timerEnabled = state.settings.timeLimitEnabled
    val totalSeconds = state.settings.timeLimitSeconds
    var secondsLeft by remember(state.roundNumber, totalSeconds, timerEnabled) {
        mutableIntStateOf(totalSeconds)
    }
    var paused by remember(state.roundNumber) { mutableStateOf(false) }
    val timeUp = timerEnabled && secondsLeft <= 0

    LaunchedEffect(state.roundNumber, timerEnabled, paused) {
        if (!timerEnabled || paused) return@LaunchedEffect
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Kategorie",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${state.category?.emoji ?: ""}  ${state.category?.name ?: ""}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            if (timerEnabled) {
                Spacer(Modifier.height(14.dp))
                TimerBar(
                    totalSeconds = totalSeconds,
                    secondsLeft = secondsLeft,
                    paused = paused,
                    timeUp = timeUp,
                    onTogglePause = { paused = !paused },
                    onReset = { secondsLeft = totalSeconds; paused = false },
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(listOf(BgCard, Accent.copy(alpha = 0.4f))),
                    )
                    .padding(20.dp),
            ) {
                Column {
                    Text(
                        text = "HINWEISRUNDE",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Jeder sagt EIN Wort zum geheimen Begriff.",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = imposterNote,
                        color = TextSecondary,
                        fontSize = 14.sp,
                    )
                    if (showOrder && firstName.isNotBlank()) {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "$firstName fängt an.",
                            color = AccentBright,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (showOrder) {
                Text(
                    text = "REIHENFOLGE",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    itemsIndexed(state.roundPlayers) { idx, rp ->
                        OrderRow(index = idx + 1, name = rp.player.name)
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            PrimaryButton(
                text = if (timeUp) "Zeit vorbei – Abstimmen!" else "Bereit? Imposter aufdecken",
                onClick = onGoToVote,
                leading = {
                    Icon(
                        imageVector = Icons.Filled.HowToVote,
                        contentDescription = null,
                        tint = Color.White,
                    )
                },
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Diskutiert und stimmt ab, wer der Imposter ist.",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TimerBar(
    totalSeconds: Int,
    secondsLeft: Int,
    paused: Boolean,
    timeUp: Boolean,
    onTogglePause: () -> Unit,
    onReset: () -> Unit,
) {
    val progress = if (totalSeconds <= 0) 0f else (secondsLeft.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val timeColor = when {
        timeUp -> ImposterRed
        secondsLeft <= 10 -> ImposterRed
        secondsLeft <= 20 -> Gold
        else -> AccentBright
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard.copy(alpha = 0.85f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatClock(secondsLeft),
                color = timeColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable { onReset() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Replay,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable { onTogglePause() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .background(timeColor),
            )
        }
        if (timeUp) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Zeit abgelaufen — jetzt abstimmen!",
                color = ImposterRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun formatClock(seconds: Int): String {
    val s = seconds.coerceAtLeast(0)
    val m = s / 60
    val r = s % 60
    return "%d:%02d".format(m, r)
}

@Composable
private fun OrderRow(index: Int, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Accent.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$index",
                color = AccentBright,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
    }
}
