package com.imposter.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.data.WordCategories
import com.imposter.game.data.WordCategory
import com.imposter.game.model.GameSettings
import com.imposter.game.model.ImposterCountMode
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import kotlin.math.max
import kotlin.math.min

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onChange: (GameSettings) -> Unit,
    onBack: () -> Unit,
    playerCount: Int,
    usedWordCount: Int,
    totalWordPool: Int,
    onClearHistory: () -> Unit,
) {
    val maxImposters = playerCount.coerceAtLeast(0)
    var showHistoryConfirm by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconCircleButton(icon = Icons.Filled.ArrowBack, onClick = onBack)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Einstellungen",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item {
                    SectionTitle("Imposter")
                }
                item {
                    ImposterModeSelector(
                        mode = settings.imposterMode,
                        onSelect = { onChange(settings.copy(imposterMode = it)) },
                    )
                }
                when (settings.imposterMode) {
                    ImposterCountMode.FIXED -> item {
                        Stepper(
                            label = "Imposter pro Runde",
                            value = settings.fixedImposters.coerceIn(0, maxImposters),
                            min = 0,
                            max = maxImposters,
                            onChange = { onChange(settings.copy(fixedImposters = it)) },
                            sublabel = describeImposters(settings.fixedImposters, playerCount),
                        )
                    }
                    ImposterCountMode.RANDOM -> {
                        item {
                            Stepper(
                                label = "Minimum",
                                value = settings.randomMin.coerceIn(0, maxImposters),
                                min = 0,
                                max = settings.randomMax.coerceAtMost(maxImposters),
                                onChange = { onChange(settings.copy(randomMin = it)) },
                            )
                        }
                        item {
                            Stepper(
                                label = "Maximum",
                                value = settings.randomMax.coerceIn(settings.randomMin, maxImposters),
                                min = settings.randomMin,
                                max = maxImposters,
                                onChange = { onChange(settings.copy(randomMax = it)) },
                                sublabel = "Wird pro Runde zufällig gewählt (max. $playerCount)",
                            )
                        }
                    }
                    ImposterCountMode.AUTO -> item {
                        InfoCard(
                            title = "Auto-Modus",
                            text = "Die Anzahl der Imposter wird pro Runde basierend auf der Spieleranzahl gewürfelt. Mehr Spieler – mehr Imposter.",
                        )
                    }
                }

                item { SectionTitle("Zeitlimit") }
                item {
                    ToggleRow(
                        label = "Zeitlimit",
                        sublabel = if (settings.timeLimitEnabled)
                            "Countdown während der Hinweisrunde: ${formatSeconds(settings.timeLimitSeconds)}"
                        else
                            "Aus — keine Begrenzung",
                        checked = settings.timeLimitEnabled,
                        onChange = { onChange(settings.copy(timeLimitEnabled = it)) },
                    )
                }
                if (settings.timeLimitEnabled) {
                    item {
                        TimeLimitChips(
                            currentSeconds = settings.timeLimitSeconds,
                            onSelect = { onChange(settings.copy(timeLimitSeconds = it)) },
                        )
                    }
                }

                item { SectionTitle("Hinweise") }
                item {
                    ToggleRow(
                        label = "Reihenfolge anzeigen",
                        sublabel = "Zeigt, wer in welcher Reihenfolge dran ist",
                        checked = settings.showClueOrder,
                        onChange = { onChange(settings.copy(showClueOrder = it)) },
                    )
                }
                item {
                    ToggleRow(
                        label = "Imposter sieht Kategorie",
                        sublabel = "Hilft beim Bluffen — einfacher für den Imposter",
                        checked = settings.imposterKnowsCategory,
                        onChange = { onChange(settings.copy(imposterKnowsCategory = it)) },
                    )
                }

                item { SectionTitle("Wortverlauf") }
                item {
                    HistoryCard(
                        usedCount = usedWordCount,
                        totalPool = totalWordPool,
                        onClear = { showHistoryConfirm = true },
                    )
                }

                item { SectionTitle("Kategorien") }
                items(WordCategories.all, key = { it.id }) { cat ->
                    CategoryRow(
                        category = cat,
                        selected = cat.id in settings.selectedCategoryIds,
                        onToggle = {
                            val ids = settings.selectedCategoryIds.toMutableSet()
                            if (cat.id in ids) ids.remove(cat.id) else ids.add(cat.id)
                            onChange(settings.copy(selectedCategoryIds = ids))
                        },
                    )
                }
            }
        }
    }

    if (showHistoryConfirm) {
        ConfirmDialog(
            title = "Wortverlauf löschen?",
            message = "Bereits gespielte Wörter werden wieder freigegeben.",
            confirmLabel = "Löschen",
            onConfirm = {
                onClearHistory()
                showHistoryConfirm = false
            },
            onDismiss = { showHistoryConfirm = false },
        )
    }
}

@Composable
private fun HistoryCard(usedCount: Int, totalPool: Int, onClear: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bereits gespielte Wörter",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$usedCount von $totalPool Wörtern verbraucht",
                    color = TextMuted,
                    fontSize = 13.sp,
                )
            }
            val enabled = usedCount > 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (enabled) com.imposter.game.ui.theme.ImposterRed.copy(alpha = 0.85f)
                        else Color.White.copy(alpha = 0.06f),
                    )
                    .clickable(enabled = enabled) { onClear() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Löschen",
                    color = if (enabled) Color.White else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        val progress = if (totalPool > 0) (usedCount.toFloat() / totalPool).coerceIn(0f, 1f) else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.08f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .background(AccentBright),
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(com.imposter.game.ui.theme.BgMid)
                .padding(20.dp),
        ) {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Abbrechen", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(com.imposter.game.ui.theme.ImposterRed)
                        .clickable { onConfirm() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(confirmLabel, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun describeImposters(count: Int, playerCount: Int): String = when {
    count <= 0 -> "0 — Freie Runde, kein Imposter"
    count >= playerCount && playerCount > 0 -> "$count — Alle sind Imposter!"
    count == 1 -> "1 Imposter unter $playerCount Spielern"
    else -> "$count Imposter unter $playerCount Spielern"
}

fun formatSeconds(s: Int): String {
    val m = s / 60
    val r = s % 60
    return if (m > 0) "%d:%02d Min".format(m, r) else "%d Sek".format(r)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeLimitChips(currentSeconds: Int, onSelect: (Int) -> Unit) {
    val options = listOf(30, 45, 60, 90, 120, 180, 300)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(14.dp),
    ) {
        Text(
            text = "Dauer",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { sec ->
                val active = sec == currentSeconds
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (active) Accent else Color.White.copy(alpha = 0.08f))
                        .clickable { onSelect(sec) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = formatSeconds(sec),
                        color = if (active) Color.White else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 3.sp,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp),
    )
}

@Composable
private fun ImposterModeSelector(mode: ImposterCountMode, onSelect: (ImposterCountMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(6.dp),
    ) {
        listOf(
            ImposterCountMode.FIXED to "Fest",
            ImposterCountMode.RANDOM to "Zufall",
            ImposterCountMode.AUTO to "Auto",
        ).forEach { (m, label) ->
            val active = m == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) Accent else Color.Transparent)
                    .clickable { onSelect(m) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun Stepper(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    onChange: (Int) -> Unit,
    sublabel: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                if (sublabel != null) {
                    Text(text = sublabel, color = TextMuted, fontSize = 13.sp)
                }
            }
            StepperButton(text = "−", enabled = value > min) {
                onChange(max(min, value - 1))
            }
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$value",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            StepperButton(text = "+", enabled = value < max) {
                onChange(min(max, value + 1))
            }
        }
    }
}

@Composable
private fun StepperButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) Accent.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else TextMuted,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    sublabel: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .clickable { onChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = sublabel, color = TextMuted, fontSize = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.18f),
            ),
        )
    }
}

@Composable
private fun CategoryRow(category: WordCategory, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Accent.copy(alpha = 0.25f) else BgCard)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = category.emoji,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${category.words.size} Wörter",
                color = TextMuted,
                fontSize = 13.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (selected) AccentBright else Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(16.dp),
    ) {
        Text(title, color = AccentBright, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(text, color = TextSecondary, fontSize = 14.sp)
    }
}
