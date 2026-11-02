package ir.nimacurly.curlybot

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CopyChip(code: String, onCopied: (String) -> Unit) {
    val ctx = LocalContext.current
    var ok by remember { mutableStateOf(false) }
    LaunchedEffect(ok) { if (ok) { delay(1400); ok = false } }
    val shape = RoundedCornerShape(11.dp)
    val bg = if (ok) Color(0xFF0D2016) else Color(0xFF1A0D10)
    val fg = if (ok) Color(0xFF9BFFC3) else Color(0xFFFFC2C9)
    val bd = if (ok) Color(0x8843E68A) else Color(0x44FF2D46)
    Row(
        Modifier.clip(shape).background(bg).border(1.dp, bd, shape)
            .clickable {
                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("curlybot-command", code))
                ok = true
                onCopied(code)
            }
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            code, color = fg, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            style = TextStyle(textDirection = TextDirection.Ltr, textAlign = TextAlign.Left)
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            painterResource(if (ok) R.drawable.ic_check else R.drawable.ic_copy),
            contentDescription = null, tint = fg, modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun LevelBadge(level: Int) {
    val c = when (level) {
        1 -> Color(0xFFFFD27A)
        2 -> Color(0xFF9FC2FF)
        else -> Color(0xFFFF8A94)
    }
    Box(
        Modifier.clip(RoundedCornerShape(9.dp)).border(1.dp, c.copy(alpha = 0.4f), RoundedCornerShape(9.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) { Text(LEVELS[level], color = c, fontSize = 10.5.sp) }
}

@Composable
fun GuideScreen(onToast: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("all") }
    val q = query.trim().lowercase()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        SectionTitle("📖 راهنمای ربات")
        Text(
            "👆 روی هر کد بزن تا کپی بشه", color = RedSoft, fontSize = 12.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 10.dp)
        )

        // قابلیت‌ها (۲ ستون)
        FEATURES.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { (e, t, d) ->
                    GCard(Modifier.weight(1f), radius = 16.dp) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(e, fontSize = 20.sp)
                            Text(t, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                            Text(d, color = Gray, fontSize = 11.sp, lineHeight = 19.sp)
                        }
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // جستجو
        OutlinedTextField(
            value = query, onValueChange = { query = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 10.dp),
            placeholder = { Text("🔎 جستجوی دستور یا قابلیت…", color = Gray, fontSize = 14.sp) },
            textStyle = TextStyle(color = GrayLight, fontSize = 15.sp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Red, unfocusedBorderColor = Line, cursorColor = Red,
                focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg,
                focusedTextColor = GrayLight, unfocusedTextColor = GrayLight
            )
        )

        // تب‌های دسته‌بندی
        val tabs = listOf(Pair("all", "همه")) + GUIDE.map { Pair(it.id, it.emoji + " " + it.title) }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            items(tabs) { (id, label) ->
                val sel = filter == id
                val shape = RoundedCornerShape(20.dp)
                Box(
                    Modifier.clip(shape)
                        .background(
                            if (sel) Brush.linearGradient(listOf(Red, RedDeep))
                            else Brush.linearGradient(listOf(Surface1, Surface1))
                        )
                        .border(1.dp, if (sel) Color.Transparent else Line, shape)
                        .clickable { filter = id }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text(label, color = if (sel) Color.White else Gray, fontSize = 12.5.sp) }
            }
        }

        var shown = 0
        GUIDE.forEach { cat ->
            if (filter != "all" && filter != cat.id) return@forEach
            val items = cat.items.filter { q.isEmpty() || (it.code + " " + it.desc).lowercase().contains(q) }
            if (items.isEmpty()) return@forEach
            shown++
            GCard(Modifier.fillMaxWidth().padding(bottom = 14.dp), radius = 22.dp) {
                Column {
                    Row(
                        Modifier.fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(cat.color.copy(alpha = 0.22f), Color.Transparent)))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(42.dp).clip(RoundedCornerShape(14.dp))
                                .background(cat.color.copy(alpha = 0.28f).compositeOver(Color(0xFF111111))),
                            contentAlignment = Alignment.Center
                        ) { Text(cat.emoji, fontSize = 22.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cat.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(cat.sub, color = Gray, fontSize = 11.5.sp)
                        }
                        Box(
                            Modifier.clip(RoundedCornerShape(12.dp)).background(FieldBg)
                                .border(1.dp, Line, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) { Text(items.size.toString().fa(), color = Gray, fontSize = 12.sp) }
                    }
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                        items.forEachIndexed { i, c ->
                            if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(Line.copy(alpha = 0.7f)))
                            Column(Modifier.padding(vertical = 11.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CopyChip(c.code) { onToast("✓ کپی شد:  $it") }
                                    if (c.level > 0) LevelBadge(c.level)
                                }
                                Text(
                                    c.desc, color = Color(0xFFB4B4BD), fontSize = 12.5.sp, lineHeight = 22.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (shown == 0) {
            Text(
                "چیزی پیدا نشد 🔍", color = Gray, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)
            )
        }
        Spacer(Modifier.height(120.dp))
    }
}
