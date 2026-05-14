package com.imposter.game.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object AvatarStore {

    private const val AUTHORITY_SUFFIX = ".fileprovider"
    private const val MAX_DIMENSION = 512
    private const val JPEG_QUALITY = 88

    fun createCaptureTarget(context: Context): Pair<Uri, File> {
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        if (!file.exists()) file.createNewFile()
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + AUTHORITY_SUFFIX,
            file,
        )
        return uri to file
    }

    fun saveFromCapture(context: Context, playerId: Int, sourceFile: File): String? {
        if (!sourceFile.exists() || sourceFile.length() == 0L) return null
        val targetDir = File(context.filesDir, "avatars").apply { mkdirs() }
        val targetFile = File(targetDir, "p_$playerId.jpg")
        return try {
            val sampled = decodeSampled(sourceFile, MAX_DIMENSION)
                ?: return null
            val oriented = applyExifRotation(sourceFile, sampled)
            FileOutputStream(targetFile).use { out ->
                oriented.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            if (oriented !== sampled) oriented.recycle()
            sampled.recycle()
            sourceFile.delete()
            targetFile.absolutePath
        } catch (_: Throwable) {
            sourceFile.delete()
            null
        }
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        try { File(path).delete() } catch (_: Throwable) {}
    }

    fun loadOriented(file: File, maxDimension: Int = 1024): Bitmap? {
        if (!file.exists() || file.length() == 0L) return null
        return try {
            val decoded = decodeSampled(file, maxDimension) ?: return null
            applyExifRotation(file, decoded)
        } catch (_: Throwable) {
            null
        }
    }

    fun saveFinalBitmap(context: Context, playerId: Int, bitmap: Bitmap): String? {
        val targetDir = File(context.filesDir, "avatars").apply { mkdirs() }
        val targetFile = File(targetDir, "p_$playerId.jpg")
        return try {
            val scaled = if (maxOf(bitmap.width, bitmap.height) > MAX_DIMENSION) {
                val scale = MAX_DIMENSION.toFloat() / maxOf(bitmap.width, bitmap.height)
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true,
                )
            } else bitmap
            FileOutputStream(targetFile).use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            if (scaled !== bitmap) scaled.recycle()
            targetFile.absolutePath
        } catch (_: Throwable) {
            null
        }
    }

    private fun decodeSampled(file: File, maxDim: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        var w = bounds.outWidth
        var h = bounds.outHeight
        while (w / 2 >= maxDim && h / 2 >= maxDim) {
            w /= 2
            h /= 2
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val raw = BitmapFactory.decodeFile(file.absolutePath, opts) ?: return null
        val largest = maxOf(raw.width, raw.height)
        if (largest <= maxDim) return raw
        val scale = maxDim.toFloat() / largest
        val scaled = Bitmap.createScaledBitmap(
            raw,
            (raw.width * scale).toInt(),
            (raw.height * scale).toInt(),
            true,
        )
        if (scaled !== raw) raw.recycle()
        return scaled
    }

    private fun applyExifRotation(file: File, bitmap: Bitmap): Bitmap {
        val orientation = try {
            ExifInterface(file.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        } catch (_: Throwable) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val rot = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (rot == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(rot) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
