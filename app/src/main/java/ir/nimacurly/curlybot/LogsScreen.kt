package ir.nimacurly.curlybot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogsScreen(logs: String, onClear: () -> Unit) {
    val lines = remember(logs) { logs.split("\n").filter { it.isNotBlank() } }
    val state = rememberLazyListState()
    LaunchedEffect(lines.size) { if (lines.isNotEmpty()) state.scrollToItem(lines.lastIndex) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SectionTitle("📜 لاگ زنده")
        GCard(Modifier.weight(1f).fillMaxWidth()) {
            if (lines.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("هنوز گزارشی نیست.\nربات رو روشن کن.", color = Gray, textAlign = TextAlign.Center)
                }
            } else {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    LazyColumn(state = state, modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        items(lines) { l ->
                            val color = when {
                                l.contains("❌") -> ErrText
                                l.contains("⚠️") || l.contains("🔁") -> Amber
                                l.contains("✅") || l.contains("🚀") || l.contains("🔌") -> Green
                                else -> androidx.compose.ui.graphics.Color(0xFFB9B9C2)
                            }
                            Text(
                                l, color = color, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp, modifier = Modifier.padding(vertical = 2.dp),
                                style = TextStyle(textDirection = TextDirection.Content)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GhostButton("پاک کردن لاگ", onClear)
        Spacer(Modifier.height(112.dp))
    }
}
