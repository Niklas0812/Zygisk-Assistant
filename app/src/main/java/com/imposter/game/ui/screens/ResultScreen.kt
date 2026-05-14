package com.imposter.game.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.model.Player
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PlayerAvatar
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.components.SecondaryButton
import com.imposter.game.ui.theme.CrewGreen
import com.imposter.game.ui.theme.Gold
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

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
        noImposters -> "+1 für alle"
        allImposters -> "+2 für alle"
        result.crewWon -> "Erwischt!"
        else -> "Entkommen!"
    }

    val headlineScale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        headlineScale.animateTo(1f, tween(550, easing = EaseOutBack))
    }
    val showConfetti = result.crewWon && !noImposters
    val showSparkles = !result.crewWon && !allImposters

    GradientBackground(
        topColor = winColor.copy(alpha = 0.35f),
    ) {
        if (showConfetti) {
            ConfettiOverlay(modifier = Modifier.fillMaxSize())
        }
        if (showSparkles) {
            SparkleOverlay(modifier = Modifier.fillMaxSize())
        }
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
                modifier = Modifier.scale(headlineScale.value),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = headline,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                modifier = Modifier.scale(headlineScale.value),
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
                NextStarterCard(
                    chosen = state.nextStarter,
                    pool = state.players,
                )
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
                        avatarPath = p.avatarPath,
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
private fun NextStarterCard(chosen: Player, pool: List<Player>) {
    var displayName by remember(chosen.id) { mutableStateOf(pool.firstOrNull()?.name ?: chosen.name) }
    var rolling by remember(chosen.id) { mutableStateOf(true) }
    var diceAngle by remember(chosen.id) { mutableFloatStateOf(0f) }
    LaunchedEffect(chosen.id) {
        val names = (pool.map { it.name } - chosen.name).ifEmpty { listOf(chosen.name) }
        val totalMs = 1200
        var elapsed = 0
        var step = 60
        while (elapsed < totalMs) {
            displayName = names.random()
            diceAngle += 90f
            delay(step.toLong())
            elapsed += step
            step += 18
        }
        displayName = chosen.name
        rolling = false
    }
    val angle by animateFloatAsState(targetValue = diceAngle, animationSpec = tween(120), label = "dice")
    val infinite = rememberInfiniteTransition(label = "starter-shine")
    val shine by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shine",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Gold.copy(alpha = 0.55f * shine),
                        Color(0xFFFB923C).copy(alpha = 0.55f * shine),
                    ),
                ),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🎲",
                fontSize = 36.sp,
                modifier = Modifier.graphicsLayer { rotationZ = angle },
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "STARTET DIE NÄCHSTE RUNDE",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = displayName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun ConfettiOverlay(modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFFFBBF24), Color(0xFF22C55E), Color(0xFF38BDF8),
        Color(0xFFEC4899), Color(0xFFA855F7), Color(0xFFF97316),
    )
    val pieces = remember {
        List(48) {
            ConfettiPiece(
                x = Random.nextFloat(),
                startOffset = Random.nextFloat() * 0.5f,
                speed = 0.7f + Random.nextFloat() * 0.6f,
                drift = (Random.nextFloat() - 0.5f) * 0.3f,
                size = 6f + Random.nextFloat() * 8f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(3200))
    }
    Canvas(modifier = modifier) {
        pieces.forEach { p ->
            val t = (progress.value - p.startOffset).coerceAtLeast(0f) * p.speed
            if (t <= 0f) return@forEach
            val cx = size.width * (p.x + p.drift * t)
            val cy = size.height * t * 1.2f
            if (cy > size.height + 40) return@forEach
            rotate(degrees = t * p.rotationSpeed, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                drawRect(
                    color = p.color,
                    topLeft = androidx.compose.ui.geometry.Offset(cx - p.size / 2, cy - p.size / 2),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size),
                )
            }
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val startOffset: Float,
    val speed: Float,
    val drift: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
)

@Composable
private fun SparkleOverlay(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "sparkles")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400)),
        label = "phase",
    )
    val sparkles = remember {
        List(22) {
            SparklePoint(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                phase = Random.nextFloat(),
                radius = 2f + Random.nextFloat() * 3f,
            )
        }
    }
    Canvas(modifier = modifier) {
        sparkles.forEach { sp ->
            val t = ((phase + sp.phase) % 1f)
            val alpha = (sin(t.toDouble() * 2.0 * PI).toFloat() + 1f) / 2f
            drawCircle(
                color = ImposterRed.copy(alpha = alpha * 0.55f),
                radius = sp.radius * (0.8f + alpha),
                center = androidx.compose.ui.geometry.Offset(sp.x * size.width, sp.y * size.height),
            )
        }
    }
}

private data class SparklePoint(
    val x: Float,
    val y: Float,
    val phase: Float,
    val radius: Float,
)


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
private fun ScoreRow(rank: Int, name: String, score: Int, avatarPath: String?) {
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
        Spacer(Modifier.size(10.dp))
        PlayerAvatar(avatarPath = avatarPath, size = 32.dp)
        Spacer(Modifier.size(10.dp))
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
