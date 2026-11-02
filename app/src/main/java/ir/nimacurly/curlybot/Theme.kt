package ir.nimacurly.curlybot

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// پالت خاکستری × قرمز (هماهنگ با طرح CurlyBot)
val Bg = Color(0xFF0B0B0D)
val Surface1 = Color(0xFF16161A)
val Surface2 = Color(0xFF1E1E23)
val Line = Color(0xFF2A2A31)
val Gray = Color(0xFF8E8E98)
val GrayLight = Color(0xFFE4E4EA)
val Red = Color(0xFFFF2D46)
val RedDark = Color(0xFF9E1B2B)
val RedDeep = Color(0xFFA5152A)
val RedSoft = Color(0xFFFF8A94)
val RedGlow = Color(0x66FF2D46)
val Green = Color(0xFF7DFFB0)
val Amber = Color(0xFFFFC46B)
val ErrText = Color(0xFFFF6B7D)
val FieldBg = Color(0xFF0E0E11)

@Composable
fun CurlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Red,
            onPrimary = Color.White,
            background = Bg,
            surface = Surface1,
            onSurface = GrayLight,
            onBackground = GrayLight,
        ),
        content = content
    )
}
