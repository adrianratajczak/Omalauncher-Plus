package dev.omalauncher.ui

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

/**
 * Frosted widget tiles: the wallpaper, shrunk to a thumbnail once, sampled per
 * tile at its place on screen. One tiny bitmap (~160 KB) serves every tile; the
 * GPU stretches each crop, so scrolling it costs a redraw, never a recompose.
 */
class FrostData(
    /** The wallpaper at 1/[DIV] size; stretched crops read as a soft blur. */
    val tiny: ImageBitmap,
    /** tiny.width / wallpaper.width, to map wallpaper pixels into the thumbnail. */
    val tinyScale: Float,
    /** The wallpaper's pixels. */
    val wallW: Int,
    val wallH: Int,
    /** The window's pixels, which the background crop fills. */
    val screenW: Int,
    val screenH: Int,
    /** The pager's viewport top-left in window pixels; steady while scrolling. */
    val viewportOrigin: Offset,
    /** The background's parallax offset, read while drawing. */
    val offset: () -> Float,
) {
    companion object {
        const val DIV = 8
    }
}

/** The wallpaper shrunk to a thumbnail, or null when it can't shrink. */
fun frostTiny(wallpaper: ImageBitmap): ImageBitmap? =
    runCatching {
        val full = wallpaper.asAndroidBitmap()
        val w = (full.width / FrostData.DIV).coerceAtLeast(1)
        val h = (full.height / FrostData.DIV).coerceAtLeast(1)
        Bitmap.createScaledBitmap(full, w, h, true).asImageBitmap()
    }.getOrNull()

/**
 * Draws frost under every tile rect: the wallpaper crop the background shows,
 * sampled where each tile sits on screen. [slotOffsetPx] is the page's offset
 * from the viewport's start (negative while it slides out); rects are page-local.
 */
fun DrawScope.drawFrostPage(
    frost: FrostData,
    slotOffsetPx: Float,
    rects: List<IntRect>,
) {
    if (rects.isEmpty() || frost.screenW <= 0 || frost.screenH <= 0) return
    // Same crop the background draws, so the frost lines up with it.
    val crop = wallpaperCrop(frost.wallW, frost.wallH, frost.screenW, frost.screenH, frost.offset())
    val sx = crop.width().toFloat() / frost.screenW
    val sy = crop.height().toFloat() / frost.screenH
    val tinyW = frost.tiny.width
    val tinyH = frost.tiny.height
    for (rect in rects) {
        val tx = frost.viewportOrigin.x + slotOffsetPx + rect.left
        val ty = frost.viewportOrigin.y + rect.top
        val sl = ((crop.left + tx * sx) * frost.tinyScale).toInt().coerceIn(0, tinyW - 1)
        val st = ((crop.top + ty * sy) * frost.tinyScale).toInt().coerceIn(0, tinyH - 1)
        val sw = (rect.width * sx * frost.tinyScale).toInt().coerceIn(1, tinyW - sl)
        val sh = (rect.height * sy * frost.tinyScale).toInt().coerceIn(1, tinyH - st)
        drawImage(
            image = frost.tiny,
            srcOffset = IntOffset(sl, st),
            srcSize = IntSize(sw, sh),
            dstOffset = IntOffset(rect.left, rect.top),
            dstSize = IntSize(rect.width, rect.height),
            filterQuality = FilterQuality.Medium,
        )
    }
}
