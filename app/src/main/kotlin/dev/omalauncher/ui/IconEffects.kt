package dev.omalauncher.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode

/** How Plus reshapes app icons: untouched, circled, or rounded (squircle-ish). */
enum class IconShape(
    val id: String,
    val label: String,
) {
    System("system", "system"),
    Circle("circle", "circle"),
    Squircle("squircle", "squircle"),
    ;

    companion object {
        fun of(id: String?) = entries.firstOrNull { it.id == id } ?: System
    }
}

/**
 * Finishes a rendered app icon bitmap: Plus's shape crops it (a circle, or a
 * squircle at ~30% radius), grayscale drains its color, and a tint multiplies
 * it toward a color. Silhouettes for the monochrome style stay as drawn.
 * Returns the bitmap itself when idle.
 */
fun finishIcon(
    source: Bitmap,
    shape: IconShape,
    gray: Boolean,
    tint: Int? = null,
): Bitmap {
    var bmp = source
    if (shape != IconShape.System) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val path = Path()
        val half = bmp.width / 2f
        if (shape == IconShape.Circle) {
            path.addCircle(half, half, half, Path.Direction.CW)
        } else {
            path.addRoundRect(0f, 0f, bmp.width.toFloat(), bmp.height.toFloat(), half * 0.6f, half * 0.6f, Path.Direction.CW)
        }
        Canvas(out).run {
            drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG))
            drawBitmap(bmp, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) })
        }
        bmp = out
    }
    if (gray) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) }) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    if (tint != null) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.MULTIPLY) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    return bmp
}
