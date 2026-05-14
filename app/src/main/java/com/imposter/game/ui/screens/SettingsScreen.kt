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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.imposter.game.ui.theme.BgMid
import com.imposter.game.ui.theme.ImposterRed
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item { SectionTitle("Imposter") }
                item {
                    ImposterModeSelector(
                        mode = settings.imposterMode,
                        onSelect = { onChange(settings.copy(imposterMode = it)) },
                    )
                }
                when (settings.imposterMode) {
                    ImposterCountMode.FIXED -> item {
                        Stepper(
                            label = "Anzahl",
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
                            )
                        }
                    }
                    ImposterCountMode.AUTO -> item {
                        SubtleNote("Anzahl wird pro Runde zufällig gewählt.")
                    }
                }

                item { Spacer(Modifier.height(6.dp)) }
                item { SectionTitle("Zeitlimit") }
                item {
                    ToggleRow(
                        label = "Aktiv",
                        sublabel = null,
                        checked = settings.timeLimitEnabled,
                        onChange = { onChange(settings.copy(timeLimitEnabled = it)) },
                    )
                }
                if (settings.timeLimitEnabled) {
                    item {
                        TimePicker(
                            seconds = settings.timeLimitSeconds.coerceAtLeast(5),
                            onChange = { onChange(settings.copy(timeLimitSeconds = it.coerceAtLeast(5))) },
                        )
                    }
                }

                item { Spacer(Modifier.height(6.dp)) }
                item { SectionTitle("Anzeige") }
                item {
                    ToggleRow(
                        label = "Reihenfolge zeigen",
                        sublabel = null,
                        checked = settings.showClueOrder,
                        onChange = { onChange(settings.copy(showClueOrder = it)) },
                    )
                }
                item {
                    ToggleRow(
                        label = "Imposter sieht Kategorie",
                        sublabel = null,
                        checked = settings.imposterKnowsCategory,
                        onChange = { onChange(settings.copy(imposterKnowsCategory = it)) },
                    )
                }

                item { Spacer(Modifier.height(6.dp)) }
                item { SectionTitle("Wortverlauf") }
                item {
                    HistoryCard(
                        usedCount = usedWordCount,
                        totalPool = totalWordPool,
                        onClear = { showHistoryConfirm = true },
                    )
                }

                item { Spacer(Modifier.height(6.dp)) }
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
            title = "Verlauf löschen?",
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

private fun describeImposters(count: Int, playerCount: Int): String = when {
    count <= 0 -> "Niemand"
    count >= playerCount && playerCount > 0 -> "Alle Spieler"
    count == 1 -> "1 von $playerCount"
    else -> "$count von $playerCount"
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp, bottom = 4.dp),
    )
}

@Composable
private fun ImposterModeSelector(mode: ImposterCountMode, onSelect: (ImposterCountMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .padding(4.dp),
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
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) Accent else Color.Transparent)
                    .clickable { onSelect(m) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (sublabel != null) {
                Text(text = sublabel, color = TextMuted, fontSize = 12.sp)
            }
        }
        StepperButton(text = "−", enabled = value > min) { onChange(max(min, value - 1)) }
        Box(modifier = Modifier.size(width = 44.dp, height = 32.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "$value",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        StepperButton(text = "+", enabled = value < max) { onChange(min(max, value + 1)) }
    }
}

@Composable
private fun StepperButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (enabled) Accent.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) AccentBright else TextMuted,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TimePicker(seconds: Int, onChange: (Int) -> Unit) {
    val totalMin = seconds / 60
    val totalSec = seconds % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatClock(seconds),
                color = AccentBright,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Minuten / Sekunden",
                color = TextMuted,
                fontSize = 12.sp,
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniSpinner(
                value = totalMin,
                min = 0,
                max = 30,
                onChange = { newMin -> onChange(newMin * 60 + totalSec) },
                modifier = Modifier.weight(1f),
                suffix = "Min",
            )
            MiniSpinner(
                value = totalSec,
                min = 0,
                max = 59,
                step = 5,
                onChange = { newSec -> onChange(totalMin * 60 + newSec) },
                modifier = Modifier.weight(1f),
                suffix = "Sek",
            )
        }
    }
}

@Composable
private fun MiniSpinner(
    value: Int,
    min: Int,
    max: Int,
    step: Int = 1,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String = "",
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BgMid)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(text = "−", enabled = value > min) { onChange(max(min, value - step)) }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "$value", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (suffix.isNotEmpty()) {
                Text(text = suffix, color = TextMuted, fontSize = 11.sp)
            }
        }
        StepperButton(text = "+", enabled = value < max) { onChange(min(max, value + step)) }
    }
}

private fun formatClock(seconds: Int): String {
    val s = seconds.coerceAtLeast(0)
    val m = s / 60
    val r = s % 60
    return "%d:%02d".format(m, r)
}

@Composable
private fun ToggleRow(
    label: String,
    sublabel: String?,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .clickable { onChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (sublabel != null) {
                Text(text = sublabel, color = TextMuted, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.14f),
            ),
        )
    }
}

@Composable
private fun CategoryRow(category: WordCategory, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Accent.copy(alpha = 0.16f) else BgCard)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = category.emoji, fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = category.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "${category.words.size} Wörter", color = TextMuted, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (selected) Accent else Color.White.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun HistoryCard(usedCount: Int, totalPool: Int, onClear: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Gespielte Wörter", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(text = "$usedCount / $totalPool", color = TextMuted, fontSize = 12.sp)
            }
            val enabled = usedCount > 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (enabled) ImposterRed.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.05f))
                    .clickable(enabled = enabled) { onClear() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Löschen",
                    color = if (enabled) Color.White else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        val progress = if (totalPool > 0) (usedCount.toFloat() / totalPool).coerceIn(0f, 1f) else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.06f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(Accent),
            )
        }
    }
}

@Composable
private fun SubtleNote(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(14.dp),
    ) {
        Text(text = text, color = TextSecondary, fontSize = 13.sp)
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
                .clip(RoundedCornerShape(18.dp))
                .background(BgMid)
                .padding(20.dp),
        ) {
            Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(message, color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Abbrechen", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ImposterRed)
                        .clickable { onConfirm() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(confirmLabel, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}
