package com.imposter.game.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.imposter.game.ui.components.SecondaryButton
import com.imposter.game.ui.theme.CrewGreen
import com.imposter.game.ui.theme.Gold
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState

@Composable
fun ResultScreen(state: GameUiState, onPlayAgain: () -> Unit, onEndSession: () -> Unit) {
    val result = state.lastWinResult ?: return
    val winColor = if (result.crewWon) CrewGreen else ImposterRed
    val ranked = state.players.sortedByDescending { it.score }
    val totalPlayers = state.players.size
    val allImposters = result.imposters.size == totalPlayers && totalPlayers > 0
    val noImposters = result.imposters.isEmpty()

    val headline = when {
        noImposters -> "FREIE RUNDE"
        allImposters -> "ALLE IMPOSTER!"
        result.crewWon -> "TEAM GEWINNT"
        else -> "IMPOSTER GEWINNEN"
    }
    val emoji = when {
        noImposters -> "🤝"
        allImposters -> "🤯"
        result.crewWon -> "🎉"
        else -> "😈"
    }
    val subtitle = when {
        noImposters -> "Keine Imposter — jeder bekommt einen Punkt!"
        allImposters -> "Alle waren Imposter — Punkte für alle!"
        result.crewWon -> "Der Imposter wurde erwischt!"
        else -> "Die Imposter sind durchgekommen."
    }

    GradientBackground(
        topColor = winColor.copy(alpha = 0.35f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = emoji,
                fontSize = 64.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = headline,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.2f),
                            ),
                        ),
                    )
                    .padding(20.dp),
            ) {
                Column {
                    LabelValue("Geheimes Wort", result.secretWord, big = true)
                    Spacer(Modifier.height(10.dp))
                    LabelValue("Kategorie", result.category)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = when {
                            result.imposters.isEmpty() -> "Imposter"
                            result.imposters.size == 1 -> "Imposter"
                            else -> "Imposter (${result.imposters.size})"
                        },
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    if (result.imposters.isEmpty()) {
                        Text(
                            text = "— keiner —",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    } else {
                        result.imposters.forEach { p ->
                            Text(
                                text = "🃏 ${p.name}",
                                color = ImposterRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    if (result.accusedPlayer != null) {
                        Spacer(Modifier.height(10.dp))
                        LabelValue("Angeklagt", result.accusedPlayer.name)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.nextStarter != null) {
                NextStarterCard(name = state.nextStarter.name)
                Spacer(Modifier.height(16.dp))
            }

            Text(
                text = "PUNKTESTAND",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                items(ranked, key = { it.id }) { p ->
                    ScoreRow(
                        rank = ranked.indexOf(p) + 1,
                        name = p.name,
                        score = p.score,
                    )
                }
            }

            PrimaryButton(text = "Nächste Runde", onClick = onPlayAgain)
            Spacer(Modifier.height(8.dp))
            SecondaryButton(text = "Sitzung beenden", onClick = onEndSession)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NextStarterCard(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Gold.copy(alpha = 0.45f), Color(0xFFFB923C).copy(alpha = 0.45f)),
                ),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🎲", fontSize = 36.sp)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NÄCHSTE RUNDE STARTET",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String, big: Boolean = false) {
    Text(
        text = label,
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
    )
    Spacer(Modifier.height(2.dp))
    Text(
        text = value,
        color = Color.White,
        fontSize = if (big) 30.sp else 18.sp,
        fontWeight = if (big) FontWeight.ExtraBold else FontWeight.Medium,
    )
}

@Composable
private fun ScoreRow(rank: Int, name: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (rank) {
                        1 -> Gold
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> Color.White.copy(alpha = 0.12f)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$rank",
                color = if (rank <= 3) Color.Black else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$score",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = "Pkt",
            color = TextMuted,
            fontSize = 12.sp,
        )
    }
}
