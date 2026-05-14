package com.imposter.game.data

import android.graphics.Bitmap
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class DistortEffect(
    val id: String,
    val name: String,
    val emoji: String,
    val apply: (Bitmap) -> Bitmap,
)

object DistortEffects {

    val all: List<DistortEffect> = listOf(
        DistortEffect("bulge", "Beule", "🤪") { bulge(it, factor = 0.55f) },
        DistortEffect("pinch", "Knautsch", "😬") { bulge(it, factor = -0.55f) },
        DistortEffect("fisheye", "Fischauge", "🐟") { bulge(it, factor = 0.9f, radiusFrac = 0.55f) },
        DistortEffect("smallhead", "Mini-Kopf", "🥒") { bulge(it, factor = -0.85f, radiusFrac = 0.6f) },
        DistortEffect("swirl", "Wirbel", "🌀") { swirl(it, strength = 2.4f) },
        DistortEffect("twist", "Twist", "🌪") { swirl(it, strength = -1.8f) },
        DistortEffect("mirrorL", "Spiegel-L", "👯") { mirrorHalf(it, leftAsSource = true) },
        DistortEffect("mirrorR", "Spiegel-R", "👯‍♀️") { mirrorHalf(it, leftAsSource = false) },
        DistortEffect("pixel16", "Pixel", "🟦") { pixelate(it, blockSize = 16) },
        DistortEffect("pixel32", "Mega-Pixel", "🟪") { pixelate(it, blockSize = 32) },
        DistortEffect("stretchV", "Lang", "📏") { stretch(it, scaleX = 0.7f, scaleY = 1.0f) },
        DistortEffect("stretchH", "Breit", "🥞") { stretch(it, scaleX = 1.0f, scaleY = 0.7f) },
    )

    /**
     * Radial bulge/pinch. factor > 0 bulges out (fisheye), < 0 pinches in.
     */
    fun bulge(src: Bitmap, factor: Float, radiusFrac: Float = 0.48f): Bitmap {
        val w = src.width
        val h = src.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val radius = minOf(w, h) * radiusFrac
        val srcPixels = IntArray(w * h)
        src.getPixels(srcPixels, 0, w, 0, 0, w, h)
        val outPixels = IntArray(w * h)
        for (y in 0 until h) {
            val dy = y - cy
            for (x in 0 until w) {
                val dx = x - cx
                val d = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                if (d < radius) {
                    val percent = d / radius
                    val mapped = if (factor >= 0f) {
                        d * (1f - factor * (1f - percent))
                    } else {
                        d * (1f + (-factor) * (1f - percent))
                    }
                    val angle = atan2(dy.toDouble(), dx.toDouble())
                    val sx = (cx + cos(angle) * mapped).toInt().coerceIn(0, w - 1)
                    val sy = (cy + sin(angle) * mapped).toInt().coerceIn(0, h - 1)
                    outPixels[y * w + x] = srcPixels[sy * w + sx]
                } else {
                    outPixels[y * w + x] = srcPixels[y * w + x]
                }
            }
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(outPixels, 0, w, 0, 0, w, h)
        return out
    }

    fun swirl(src: Bitmap, strength: Float): Bitmap {
        val w = src.width
        val h = src.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val radius = minOf(w, h) * 0.48f
        val srcPixels = IntArray(w * h)
        src.getPixels(srcPixels, 0, w, 0, 0, w, h)
        val outPixels = IntArray(w * h)
        for (y in 0 until h) {
            val dy = y - cy
            for (x in 0 until w) {
                val dx = x - cx
                val d = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                if (d < radius) {
                    val theta = atan2(dy.toDouble(), dx.toDouble())
                    val swirlAngle = theta + strength * (1f - d / radius)
                    val sx = (cx + cos(swirlAngle) * d).toInt().coerceIn(0, w - 1)
                    val sy = (cy + sin(swirlAngle) * d).toInt().coerceIn(0, h - 1)
                    outPixels[y * w + x] = srcPixels[sy * w + sx]
                } else {
                    outPixels[y * w + x] = srcPixels[y * w + x]
                }
            }
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(outPixels, 0, w, 0, 0, w, h)
        return out
    }

    fun mirrorHalf(src: Bitmap, leftAsSource: Boolean): Bitmap {
        val w = src.width
        val h = src.height
        val srcPixels = IntArray(w * h)
        src.getPixels(srcPixels, 0, w, 0, 0, w, h)
        val outPixels = IntArray(w * h)
        val mid = w / 2
        for (y in 0 until h) {
            for (x in 0 until w) {
                val sourceX = if (leftAsSource) {
                    if (x < mid) x else (w - 1 - x)
                } else {
                    if (x >= mid) x else (w - 1 - x)
                }
                outPixels[y * w + x] = srcPixels[y * w + sourceX]
            }
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(outPixels, 0, w, 0, 0, w, h)
        return out
    }

    fun pixelate(src: Bitmap, blockSize: Int): Bitmap {
        val b = blockSize.coerceAtLeast(2)
        val w = src.width
        val h = src.height
        val small = Bitmap.createScaledBitmap(src, (w / b).coerceAtLeast(1), (h / b).coerceAtLeast(1), false)
        val out = Bitmap.createScaledBitmap(small, w, h, false)
        if (small !== src) small.recycle()
        return out
    }

    fun stretch(src: Bitmap, scaleX: Float, scaleY: Float): Bitmap {
        val w = src.width
        val h = src.height
        val newW = (w * scaleX).toInt().coerceAtLeast(1)
        val newH = (h * scaleY).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(src, newW, newH, true)
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(out)
        canvas.drawColor(android.graphics.Color.BLACK)
        val dx = (w - newW) / 2f
        val dy = (h - newH) / 2f
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
        }
        canvas.drawBitmap(scaled, dx, dy, paint)
        if (scaled !== src) scaled.recycle()
        return out
    }
}
