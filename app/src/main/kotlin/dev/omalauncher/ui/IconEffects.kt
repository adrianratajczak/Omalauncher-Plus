package dev.omalauncher.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
    if (gray && (top == null || bottom == null)) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) }) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    if (top != null && bottom != null) {
        bmp = duotone(bmp, top, bottom)
    } else if (single != null) {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = PorterDuffColorFilter(single, PorterDuff.Mode.MULTIPLY) }
        Canvas(out).drawBitmap(bmp, 0f, 0f, paint)
        bmp = out
    }
    return bmp
}

/**
 * Shades [bmp] with a vertical duotone gradient, dark pixels toward [top] and
 * light toward [bottom], keeping every pixel's own light. Done per pixel:
 * bitmap canvases can't rely on blend modes, which need hardware rendering.
 */
private fun duotone(
    bmp: Bitmap,
    top: Int,
    bottom: Int,
): Bitmap {
    val w = bmp.width
    val h = bmp.height
    val pixels = IntArray(w * h)
    bmp.getPixels(pixels, 0, w, 0, 0, w, h)
    val topHsv = FloatArray(3)
    val bottomHsv = FloatArray(3)
    val pixelHsv = FloatArray(3)
    val outHsv = FloatArray(3)
    Color.RGBToHSV(top shr 16 and 0xff, top shr 8 and 0xff, top and 0xff, topHsv)
    Color.RGBToHSV(bottom shr 16 and 0xff, bottom shr 8 and 0xff, bottom and 0xff, bottomHsv)
    // Hue turns the short way around.
    var hueSpan = bottomHsv[0] - topHsv[0]
    if (hueSpan > 180f) hueSpan -= 360f
    if (hueSpan < -180f) hueSpan += 360f
    for (y in 0 until h) {
        // The gradient runs top to bottom; each pixel keeps its own light.
        val t = if (h <= 1) 0f else y.toFloat() / (h - 1)
        val hue = (topHsv[0] + hueSpan * t + 360f) % 360f
        val saturation = (topHsv[1] + (bottomHsv[1] - topHsv[1]) * t).coerceIn(0f, 1f)
        for (x in 0 until w) {
            val i = y * w + x
            val pixel = pixels[i]
            Color.RGBToHSV(pixel shr 16 and 0xff, pixel shr 8 and 0xff, pixel and 0xff, pixelHsv)
            outHsv[0] = hue
            outHsv[1] = saturation
            outHsv[2] = pixelHsv[2]
            pixels[i] = Color.HSVToColor(pixel ushr 24, outHsv)
        }
    }
    return Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
}
