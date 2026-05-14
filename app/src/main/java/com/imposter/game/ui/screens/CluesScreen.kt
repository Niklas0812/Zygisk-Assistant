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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.Icon
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
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState

@Composable
fun CluesScreen(state: GameUiState, onGoToVote: () -> Unit) {
    val showOrder = state.settings.showClueOrder
    val firstName = state.firstClueGiver?.name ?: state.players.firstOrNull()?.name ?: ""
    val totalImposters = state.roundPlayers.count { it.role is com.imposter.game.model.Role.Imposter }
    val total = state.roundPlayers.size
    val imposterNote = when {
        totalImposters == 0 -> "Free round — no imposters. Everyone says one word about the secret."
        totalImposters >= total -> "Chaos round — everyone is an imposter. Bluff or be exposed!"
        totalImposters == 1 -> "There is 1 imposter among you."
        else -> "There are $totalImposters imposters among you."
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Category",
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
            Spacer(Modifier.height(20.dp))

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
                        text = "Clue Round",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Everyone says ONE word related to the secret word.",
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
                            text = "$firstName goes first.",
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
                    text = "Order",
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
                text = "Everyone Voted? Find Imposter",
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
                text = "Discuss, then vote on who you think is the imposter.",
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
