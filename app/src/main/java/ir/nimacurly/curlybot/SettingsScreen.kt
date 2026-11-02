package ir.nimacurly.curlybot

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    running: Boolean, room: String, token: String, owner: String, admins: String,
    onSave: (String, String, String, String) -> Unit
) {
    val ctx = LocalContext.current
    var r by remember(room) { mutableStateOf(room) }
    var t by remember(token) { mutableStateOf(token) }
    var o by remember(owner) { mutableStateOf(owner) }
    var a by remember(admins) { mutableStateOf(admins) }
    var show by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    LaunchedEffect(saved) { if (saved) { delay(1200); saved = false } }

    val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
    var ignoring by remember { mutableStateOf(pm.isIgnoringBatteryOptimizations(ctx.packageName)) }
    LaunchedEffect(Unit) {
        while (true) { ignoring = pm.isIgnoringBatteryOptimizations(ctx.packageName); delay(1500) }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        SectionTitle("⚙️ تنظیمات")
        GCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                if (running) {
                    Text("🔒 برای ویرایش، اول ربات رو خاموش کن", color = RedSoft, fontSize = 12.sp)
                }
                LabeledField("🏠 آیدی روم (ROOM_ID)", r, !running) { r = it }
                LabeledField(
                    "🔑 توکن ربات (API_TOKEN)", t, !running,
                    visual = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                    trailing = {
                        Text(
                            if (show) "مخفی" else "نمایش", color = RedSoft, fontSize = 12.sp,
                            modifier = Modifier.clickable { show = !show }.padding(10.dp)
                        )
                    }
                ) { t = it }
                LabeledField("👑 یوزرنیم مالک (بدون @)", o, !running) { o = it }
                LabeledField("🛡 ادمین‌ها (با کاما)", a, !running, hint = "ali,reza") { a = it }
                Spacer(Modifier.height(16.dp))
                PrimaryButton(if (saved) "ذخیره شد ✓" else "ذخیره", enabled = !running) {
                    onSave(r, t, o, a); saved = true
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        GCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("🔋 آنلاین موندن ۲۴ ساعته", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        if (ignoring) "بهینه‌سازی باتری برای CurlyBot خاموشه ✓"
                        else "اجازه بده اندروید ربات رو توی پس‌زمینه نکشه",
                        color = Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (!ignoring) {
                    Spacer(Modifier.size(10.dp))
                    GhostChip("اجازه بده") {
                        runCatching {
                            ctx.startActivity(
                                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                    .setData(Uri.parse("package:${ctx.packageName}"))
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        GCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.logo), contentDescription = null,
                    contentScale = ContentScale.Fit, modifier = Modifier.size(120.dp)
                )
                Text("درباره‌ی CurlyBot", color = androidx.compose.ui.graphics.Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
                Text("نسخه ۱٫۰ • سازنده: @nima_curly", color = Gray, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                Text(
                    "توکن و آیدی روم فقط روی همین گوشی ذخیره می‌شن.",
                    color = Gray, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(120.dp))
    }
}
