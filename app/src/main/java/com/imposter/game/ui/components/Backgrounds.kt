package com.imposter.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.imposter.game.ui.theme.BgDeep
import com.imposter.game.ui.theme.BgMid

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    topColor: Color = BgMid,
    bottomColor: Color = BgDeep,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor),
                ),
            ),
    ) {
        content()
    }
}
