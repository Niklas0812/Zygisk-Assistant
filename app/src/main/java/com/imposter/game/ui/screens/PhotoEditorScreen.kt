package com.imposter.game.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.data.AvatarStore
import com.imposter.game.data.DistortEffect
import com.imposter.game.data.DistortEffects
import com.imposter.game.data.StickerCatalog
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.components.SecondaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.ImposterRed
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StickerInstance(
    val id: Long,
    val emoji: String,
    initialNormX: Float = 0.5f,
    initialNormY: Float = 0.5f,
    initialScale: Float = 1f,
    initialRotation: Float = 0f,
) {
    var normX by mutableStateOf(initialNormX)
    var normY by mutableStateOf(initialNormY)
    var scale by mutableStateOf(initialScale)
    var rotation by mutableStateOf(initialRotation)
}

@Composable
fun PhotoEditorScreen(
    sourceFile: File,
    onCancel: () -> Unit,
    onSave: (Bitmap) -> Unit,
) {
    val originalBitmap = remember(sourceFile.absolutePath) {
        AvatarStore.loadOriented(sourceFile, maxDimension = 768)
    }
    if (originalBitmap == null) {
        LaunchedEffect(Unit) { onCancel() }
        return
    }

    var workingBitmap by remember { mutableStateOf(originalBitmap) }
    var imageBitmap by remember { mutableStateOf(workingBitmap.asImageBitmap()) }
    val stickers = remember { mutableStateListOf<StickerInstance>() }
    var stickerIdSeq by remember { mutableIntStateOf(1) }
    var processing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun applyDistort(effect: DistortEffect) {
        if (processing) return
        processing = true
        scope.launch {
            val result = withContext(Dispatchers.Default) { effect.apply(workingBitmap) }
            if (workingBitmap !== originalBitmap) workingBitmap.recycle()
            workingBitmap = result
            imageBitmap = result.asImageBitmap()
            processing = false
        }
    }

    fun resetEdits() {
        if (workingBitmap !== originalBitmap) workingBitmap.recycle()
        workingBitmap = originalBitmap
        imageBitmap = originalBitmap.asImageBitmap()
        stickers.clear()
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconCircleButton(icon = Icons.Filled.Close, onClick = onCancel)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Foto bearbeiten",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                IconCircleButton(icon = Icons.Filled.Refresh, onClick = ::resetEdits)
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                PreviewCanvas(
                    imageBitmap = imageBitmap,
                    stickers = stickers,
                    onRemoveSticker = { stickers.remove(it) },
                )
                if (processing) {
                    CircularProgressIndicator(color = AccentBright)
                }
            }

            Spacer(Modifier.height(14.dp))
            if (stickers.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgCard)
                            .clickable { stickers.clear() }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = ImposterRed,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "Sticker entfernen",
                            color = ImposterRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            SectionLabel("Sticker")
            Spacer(Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(StickerCatalog.emojis) { emoji ->
                    EmojiButton(emoji = emoji) {
                        stickers.add(
                            StickerInstance(
                                id = stickerIdSeq.toLong(),
                                emoji = emoji,
                            ),
                        )
                        stickerIdSeq += 1
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            SectionLabel("Verzerrung")
            Spacer(Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(DistortEffects.all, key = { it.id }) { effect ->
                    DistortButton(effect = effect, enabled = !processing) {
                        applyDistort(effect)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                text = "Speichern",
                leading = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                    )
                },
                onClick = {
                    val finalBitmap = bakeStickers(workingBitmap, stickers.toList())
                    onSave(finalBitmap)
                },
            )
            Spacer(Modifier.height(8.dp))
            SecondaryButton(text = "Verwerfen", onClick = onCancel)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PreviewCanvas(
    imageBitmap: ImageBitmap,
    stickers: SnapshotStateList<StickerInstance>,
    onRemoveSticker: (StickerInstance) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f)),
    ) {
        val density = LocalDensity.current
        val canvasSizePx = with(density) { 260.dp.toPx() }

        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        stickers.forEach { sticker ->
            StickerView(
                sticker = sticker,
                canvasSizePx = canvasSizePx,
                onDoubleTap = { onRemoveSticker(sticker) },
            )
        }
    }
}

@Composable
private fun StickerView(
    sticker: StickerInstance,
    canvasSizePx: Float,
    onDoubleTap: () -> Unit,
) {
    val density = LocalDensity.current
    val baseSizeDp = 56.dp
    val baseSizePx = with(density) { baseSizeDp.toPx() }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (sticker.normX * canvasSizePx - baseSizePx / 2).toInt(),
                    y = (sticker.normY * canvasSizePx - baseSizePx / 2).toInt(),
                )
            }
            .size(baseSizeDp)
            .graphicsLayer {
                scaleX = sticker.scale
                scaleY = sticker.scale
                rotationZ = sticker.rotation
            }
            .pointerInput(sticker.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    val newScale = (sticker.scale * zoom).coerceIn(0.4f, 4f)
                    sticker.scale = newScale
                    sticker.rotation += rotation
                    sticker.normX = (sticker.normX + pan.x / canvasSizePx).coerceIn(0.05f, 0.95f)
                    sticker.normY = (sticker.normY + pan.y / canvasSizePx).coerceIn(0.05f, 0.95f)
                }
            }
            .pointerInput(sticker.id) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = sticker.emoji, fontSize = 44.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(start = 2.dp),
    )
}

@Composable
private fun EmojiButton(emoji: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 28.sp)
    }
}

@Composable
private fun DistortButton(effect: DistortEffect, enabled: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BgCard)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = effect.emoji, fontSize = 26.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = effect.name,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun bakeStickers(base: Bitmap, stickers: List<StickerInstance>): Bitmap {
    val out = base.copy(Bitmap.Config.ARGB_8888, true)
    if (stickers.isEmpty()) return out
    val canvas = Canvas(out)
    val baseSize = base.width * 0.22f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = android.graphics.Color.WHITE
    }
    stickers.forEach { s ->
        val px = s.normX * base.width
        val py = s.normY * base.height
        val sz = baseSize * s.scale
        paint.textSize = sz
        canvas.save()
        canvas.translate(px, py)
        canvas.rotate(s.rotation)
        val metrics = paint.fontMetrics
        val baseline = -(metrics.ascent + metrics.descent) / 2f
        canvas.drawText(s.emoji, 0f, baseline, paint)
        canvas.restore()
    }
    return out
}
