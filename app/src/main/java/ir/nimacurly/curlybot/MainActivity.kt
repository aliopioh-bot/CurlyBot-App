package ir.nimacurly.curlybot

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            CurlyTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val ctx = LocalContext.current
    val py = remember { Python.getInstance().getModule("runner") }

    var tab by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var uptime by remember { mutableLongStateOf(0L) }
    var logs by remember { mutableStateOf("") }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var pendingUntil by remember { mutableLongStateOf(0L) }
    var toast by remember { mutableStateOf("") }

    var room by remember { mutableStateOf(Prefs.room(ctx)) }
    var token by remember { mutableStateOf(Prefs.token(ctx)) }
    var owner by remember { mutableStateOf(Prefs.owner(ctx)) }
    var admins by remember { mutableStateOf(Prefs.admins(ctx)) }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        while (true) {
            val r = withContext(Dispatchers.Default) {
                runCatching {
                    Triple(
                        py.callAttr("is_running").toBoolean(),
                        py.callAttr("uptime").toLong(),
                        py.callAttr("get_logs").toString()
                    )
                }.getOrNull()
            }
            now = System.currentTimeMillis()
            if (r != null) {
                running = r.first
                uptime = r.second
                logs = r.third
                if (r.first) pendingUntil = 0L
            }
            delay(1000)
        }
    }

    LaunchedEffect(toast) { if (toast.isNotEmpty()) { delay(1500); toast = "" } }

    val busy = !running && now < pendingUntil
    val hasCreds = room.isNotBlank() && token.isNotBlank() && owner.isNotBlank()

    fun toggle() {
        if (running || busy) {
            pendingUntil = 0L
            BotService.stop(ctx)
            return
        }
        if (!hasCreds) {
            Toast.makeText(ctx, "اول آیدی روم، توکن و یوزرنیم مالک رو توی تنظیمات وارد کن", Toast.LENGTH_SHORT).show()
            tab = 3
            return
        }
        pendingUntil = System.currentTimeMillis() + 15000
        now = System.currentTimeMillis()
        BotService.start(ctx)
    }

    AppBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            AppHeader()
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    0 -> HomeScreen(
                        running = running, busy = busy, uptime = uptime, logs = logs, room = room,
                        hasCreds = hasCreds, onToggle = { toggle() }, goSettings = { tab = 3 }
                    )
                    1 -> LogsScreen(
                        logs = logs,
                        onClear = { runCatching { py.callAttr("clear_logs") }; logs = "" }
                    )
                    2 -> GuideScreen(onToast = { toast = it })
                    else -> SettingsScreen(
                        running = running || busy, room = room, token = token, owner = owner, admins = admins,
                        onSave = { r, t, o, a ->
                            Prefs.save(ctx, r, t, o, a)
                            room = r.trim(); token = t.trim(); owner = o.trim().removePrefix("@"); admins = a.trim()
                        }
                    )
                }
            }
        }

        // نوار پایین شناور
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .navigationBarsPadding().padding(bottom = 14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNav(tab) { tab = it }
        }

        // توست کپی
        if (toast.isNotEmpty()) {
            val shape = RoundedCornerShape(16.dp)
            Box(
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 100.dp)
                    .clip(shape).background(Color(0xFF0D2016)).border(1.dp, Color(0x6643E68A), shape)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) { Text(toast, color = Color(0xFF9BFFC3), fontSize = 13.sp, maxLines = 1) }
        }
    }
}
