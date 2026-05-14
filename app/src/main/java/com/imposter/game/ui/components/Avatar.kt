package com.imposter.game.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright

@Composable
fun rememberAvatarBitmap(path: String?): ImageBitmap? {
    if (path.isNullOrBlank()) return null
    return remember(path) {
        try {
            BitmapFactory.decodeFile(path)?.asImageBitmap()
        } catch (_: Throwable) {
            null
        }
    }
}

@Composable
fun PlayerAvatar(
    avatarPath: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tint: Color = AccentBright,
    ring: Color? = null,
) {
    val bitmap = rememberAvatarBitmap(avatarPath)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (bitmap != null) Color.Transparent else Accent.copy(alpha = 0.25f),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = tint,
            )
        }
        if (ring != null) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = ring,
                    radius = this.size.minDimension / 2 - 2f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f),
                )
            }
        }
    }
}
