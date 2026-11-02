package ir.nimacurly.curlybot

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PowerButton(on: Boolean, busy: Boolean, onClick: () -> Unit) {
    val active = on || busy
    val inf = rememberInfiniteTransition(label = "rings")
    val p1 by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "ring1"
    )
    val p2 by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = LinearEasing), RepeatMode.Restart, initialStartOffset = StartOffset(800)
        ),
        label = "ring2"
    )
    Box(Modifier.size(250.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val base = 75.dp.toPx()
            if (on) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(RedGlow, Color.Transparent), radius = base * 1.7f),
                    radius = base * 1.7f
                )
                listOf(p1, p2).forEach { p ->
                    drawCircle(
                        color = Red.copy(alpha = 0.7f * (1f - p)),
                        radius = base * (0.9f + 0.6f * p),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            drawCircle(
                color = if (active) Red else Line,
                radius = 70.dp.toPx(), style = Stroke(width = 1.dp.toPx())
            )
        }
        Box(
            Modifier.size(132.dp).clip(CircleShape)
                .background(
                    if (on) Brush.radialGradient(listOf(Color(0xFFFF6B7D), Red, Color(0xFFB3142A)))
                    else Brush.linearGradient(listOf(Color(0xFF2A2A31), Color(0xFF141417)))
                )
                .border(1.dp, if (active) Red else Line, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_power), contentDescription = "power",
                tint = if (on) Color.White else if (busy) RedSoft else Gray,
                modifier = Modifier.size(58.dp)
            )
        }
    }
}

@Composable
fun HomeScreen(
    running: Boolean, busy: Boolean, uptime: Long, logs: String, room: String,
    hasCreds: Boolean, onToggle: () -> Unit, goSettings: () -> Unit
) {
    val lastLine = logs.split("\n").lastOrNull { it.isNotBlank() }
        ?.let { if (it.length > 10) it.substring(10).trim() else it } ?: "—"

    val inf = rememberInfiniteTransition(label = "float")
    val floatY by inf.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatY"
    )

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        if (!hasCreds) {
            GCard(
                Modifier.fillMaxWidth(), borderColor = RedDark,
                background = Brush.verticalGradient(listOf(Color(0xFF2A1014), Color(0xFF2A1014))),
                onClick = goSettings
            ) {
                Text(
                    "⚠️ تنظیمات ناقصه — اول از تب تنظیمات پرش کن.",
                    color = RedSoft, fontSize = 14.sp, modifier = Modifier.padding(16.dp)
                )
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(10.dp))
            Box(Modifier.size(150.dp).offset(y = floatY.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(if (running) RedGlow else Color(0x22FF2D46), Color.Transparent),
                            radius = size.minDimension * 0.62f
                        ),
                        radius = size.minDimension * 0.62f
                    )
                }
                Image(
                    painter = painterResource(R.drawable.logo), contentDescription = null,
                    contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize()
                )
            }
            PowerButton(on = running, busy = busy, onClick = onToggle)
            Text(
                if (running) "آنلاین و آماده" else if (busy) "در حال اتصال..." else "خاموش — برای روشن کردن بزن",
                color = if (running) Green else if (busy) RedSoft else Gray,
                fontSize = 14.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
            )
        }

        Tile(Modifier.fillMaxWidth(), "⏱ مدت فعالیت", if (running) fmtTime(uptime) else "۰۰:۰۰:۰۰", valueSize = 26, white = true)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Tile(Modifier.weight(1f), "🏠 روم", room.ifBlank { "—" }, ltr = true)
            Tile(Modifier.weight(1f), "📜 آخرین رویداد", lastLine, valueSize = 13)
        }
        Spacer(Modifier.height(120.dp))
    }
}
