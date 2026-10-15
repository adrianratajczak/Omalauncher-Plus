package dev.omalauncher.plus.lockscreen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/** Tokyo Night, like the launcher before a theme is applied: light text on a dark screen. */
val Background = Color(0xFF1A1B26)
val Foreground = Color(0xFFC0CAF5)
val Muted = Color(0xFF565F89)
val Accent = Color(0xFFFF9E64)

/** The launcher's voice: monospace everywhere. */
val Mono = TextStyle(fontFamily = FontFamily.Monospace, color = Foreground)

/** The clock's size; the date and the hint use the sizes below it. */
val ClockSize = 84.sp
val DateSize = 18.sp
val HintSize = 14.sp
