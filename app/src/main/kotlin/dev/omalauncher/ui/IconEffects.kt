package dev.omalauncher.ui

import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Shader

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

/** Where the icon tint comes from: none, the custom colors, or the theme's accent. */
enum class TintSource(
    val id: String,
    val label: String,
) {
    Off("off", "off"),
    Custom("custom", "custom"),
    Theme("theme", "theme accent"),
}

/**
 * Finishes a rendered app icon bitmap: Plus's shape crops it (a circle, or a
 * squircle at ~30% radius), grayscale drains its color, and tints recolor it:
 * one color multiplies, two shade a duotone gradient top to bottom keeping the
 * light. Silhouettes for the monochrome style stay as drawn.
 * Returns the bitmap itself when idle.
 */
fun finishIcon(
    source: Bitmap,
    shape: IconShape,
    gray: Boolean,
    top: Int? = null,
    bottom: Int? = null,
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
    // A gradient needs its light base; a lone bottom stands in for the top.
    val single = top ?: bottom
    if (gray || (top != null && bottom != null)) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) }) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    if (top != null && bottom != null) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint =
            Paint().apply {
                shader = LinearGradient(0f, 0f, 0f, bmp.height.toFloat(), top, bottom, Shader.TileMode.CLAMP)
                blendMode = BlendMode.COLOR
            }
        Canvas(out).run {
            drawBitmap(bmp, 0f, 0f, null)
            drawRect(0f, 0f, bmp.width.toFloat(), bmp.height.toFloat(), paint)
        }
        bmp = out
    } else if (single != null) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = PorterDuffColorFilter(single, PorterDuff.Mode.MULTIPLY) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    return bmp
}
