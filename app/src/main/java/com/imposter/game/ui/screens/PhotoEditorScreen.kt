package com.imposter.game.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.data.AvatarStore
import com.imposter.game.data.PhotoEffect
import com.imposter.game.data.PhotoEffects
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.components.SecondaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import java.io.File

@Composable
fun PhotoEditorScreen(
    sourceFile: File,
    onCancel: () -> Unit,
    onSave: (Bitmap) -> Unit,
) {
    val source = remember(sourceFile.absolutePath) {
        AvatarStore.loadOriented(sourceFile, maxDimension = 1024)
    }
    if (source == null) {
        LaunchedEffect(Unit) { onCancel() }
        return
    }
    val sourceImageBitmap = remember(source) { source.asImageBitmap() }

    var selected by remember { mutableStateOf(PhotoEffects.identity) }
    var rotation by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    val colorMatrix = remember(selected) { ColorMatrix(selected.matrix.copyOf()) }

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
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        bitmap = sourceImageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(colorMatrix),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = rotation.toFloat()
                                scaleX = if (flipped) -1f else 1f
                            },
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selected.emoji + "  " + selected.name,
                    color = AccentBright,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ActionPill(
                    icon = Icons.Filled.RotateRight,
                    label = "Drehen",
                    modifier = Modifier.weight(1f),
                    onClick = { rotation = (rotation + 90) % 360 },
                )
                ActionPill(
                    icon = Icons.Filled.Flip,
                    label = if (flipped) "Spiegel an" else "Spiegeln",
                    modifier = Modifier.weight(1f),
                    onClick = { flipped = !flipped },
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "EFFEKTE",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(PhotoEffects.all, key = { it.id }) { effect ->
                    EffectChip(
                        effect = effect,
                        sourceBitmap = sourceImageBitmap,
                        selected = effect.id == selected.id,
                        onClick = { selected = effect },
                    )
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
                    val final = bakeBitmap(
                        source = source,
                        matrix = selected.matrix,
                        rotation = rotation,
                        flipped = flipped,
                    )
                    onSave(final)
                },
            )
            Spacer(Modifier.height(8.dp))
            SecondaryButton(
                text = "Verwerfen",
                onClick = onCancel,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EffectChip(
    effect: PhotoEffect,
    sourceBitmap: androidx.compose.ui.graphics.ImageBitmap,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val matrix = remember(effect.id) { ColorMatrix(effect.matrix.copyOf()) }
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) AccentBright else Color.White.copy(alpha = 0.15f),
                    shape = CircleShape,
                ),
        ) {
            Image(
                bitmap = sourceBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(matrix),
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = effect.name,
            color = if (selected) AccentBright else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = AccentBright)
        Spacer(Modifier.size(8.dp))
        Text(text = label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun bakeBitmap(
    source: Bitmap,
    matrix: FloatArray,
    rotation: Int,
    flipped: Boolean,
): Bitmap {
    val androidMatrix = android.graphics.Matrix().apply {
        if (flipped) preScale(-1f, 1f)
        if (rotation != 0) postRotate(rotation.toFloat())
    }
    val transformed = if (!androidMatrix.isIdentity) {
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, androidMatrix, true)
    } else source
    val output = Bitmap.createBitmap(transformed.width, transformed.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        colorFilter = ColorMatrixColorFilter(matrix)
    }
    canvas.drawBitmap(transformed, 0f, 0f, paint)
    if (transformed !== source) transformed.recycle()
    return output
}
