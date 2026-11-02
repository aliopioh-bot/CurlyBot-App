package ir.nimacurly.curlybot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun String.fa(): String =
    map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")

fun fmtTime(sec: Long): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s).fa()
}

@Composable
fun AppBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Bg)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3A0D14), Color.Transparent),
                        center = Offset(size.width / 2f, -size.height * 0.02f),
                        radius = size.width * 1.15f
                    )
                )
            },
        content = content
    )
}

@Composable
fun AppHeader() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "CurlyBot",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(46.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = buildAnnotatedString {
                    append("Curly")
                    withStyle(SpanStyle(color = Red)) { append("Bot") }
                },
                color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
            Text("ربات کامل هایرایز", color = Gray, fontSize = 12.sp)
        }
    }
}

/** کارت با گرادیان ملایم و حاشیه‌ی نازک مثل طرح اصلی */
@Composable
fun GCard(
    modifier: Modifier = Modifier,
    radius: Dp = 20.dp,
    borderColor: Color = Line,
    background: Brush = Brush.verticalGradient(listOf(Surface2, Surface1)),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius)
    var m = modifier.clip(shape).background(background).border(1.dp, borderColor, shape)
    if (onClick != null) m = m.clickable { onClick() }
    Box(m) { content() }
}

@Composable
fun Tile(modifier: Modifier = Modifier, label: String, value: String, valueSize: Int = 17, white: Boolean = false, ltr: Boolean = false) {
    GCard(modifier, radius = 18.dp) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(label, color = Gray, fontSize = 12.sp)
            Spacer(Modifier.size(6.dp))
            Text(
                value,
                color = if (white) Color.White else GrayLight,
                fontSize = valueSize.sp,
                fontWeight = if (valueSize >= 24) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = TextStyle(textDirection = if (ltr) TextDirection.Ltr else TextDirection.Content)
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp, top = 6.dp, bottom = 8.dp))
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled) Brush.linearGradient(listOf(Color(0xFFFF4D63), Color(0xFFD41A31)))
                else Brush.linearGradient(listOf(Surface2, Surface2))
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (enabled) Color.White else Gray, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun GhostButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier.fillMaxWidth().clip(shape).background(Surface2).border(1.dp, Line, shape)
            .clickable(onClick = onClick).padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = GrayLight, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun LabeledField(
    label: String, value: String, enabled: Boolean, hint: String = "",
    visual: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
    onChange: (String) -> Unit
) {
    Column {
        Text(label, color = Gray, fontSize = 13.sp, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange, enabled = enabled, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (hint.isNotEmpty()) ({ Text(hint, color = Gray, fontSize = 14.sp) }) else null,
            visualTransformation = visual, trailingIcon = trailing,
            textStyle = TextStyle(color = GrayLight, fontSize = 15.sp, textDirection = TextDirection.Ltr, textAlign = TextAlign.Left),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Red, unfocusedBorderColor = Line, disabledBorderColor = Line,
                cursorColor = Red,
                focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg, disabledContainerColor = FieldBg,
                focusedTextColor = GrayLight, unfocusedTextColor = GrayLight, disabledTextColor = Gray
            )
        )
    }
}

@Composable
fun BottomNav(tab: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Pair(R.drawable.ic_home, "خانه"),
        Pair(R.drawable.ic_log, "لاگ"),
        Pair(R.drawable.ic_book, "راهنما"),
        Pair(R.drawable.ic_gear, "تنظیمات")
    )
    val shape = RoundedCornerShape(26.dp)
    Row(
        Modifier.padding(horizontal = 14.dp).widthIn(max = 420.dp).fillMaxWidth()
            .clip(shape).background(Color(0xE616161A)).border(1.dp, Line, shape).padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { i, (icon, label) ->
            val sel = tab == i
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                    .background(
                        if (sel) Brush.linearGradient(listOf(Red, RedDeep))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onSelect(i) }.padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = if (sel) Color.White else Gray, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(3.dp))
                Text(label, fontSize = 11.sp, color = if (sel) Color.White else Gray)
            }
        }
    }
}

@Composable
fun GhostChip(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.clip(shape).background(Surface2).border(1.dp, Line, shape)
            .clickable { onClick() }.padding(horizontal = 14.dp, vertical = 8.dp)
    ) { Text(text, color = GrayLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
}
