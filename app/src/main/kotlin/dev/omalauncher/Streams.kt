package dev.omalauncher

import java.io.ByteArrayOutputStream
import java.io.InputStream

/** Reads to the end, or [limit] bytes. Not readNBytes: that's API 33, and before it the call throws. */
fun InputStream.readUpTo(limit: Int): ByteArray {
    val out = ByteArrayOutputStream()
    val chunk = ByteArray(8192)
    while (out.size() < limit) {
        val n = read(chunk, 0, minOf(chunk.size, limit - out.size()))
        if (n < 0) break
        out.write(chunk, 0, n)
    }
    return out.toByteArray()
}
