package ir.nimacurly.curlybot

import androidx.compose.ui.graphics.Color

/** level: 0 همه، 1 ادمین، 2 Host، 3 مالک */
data class Cmd(val code: String, val desc: String, val level: Int = 0)

data class Cat(
    val id: String, val emoji: String, val title: String, val sub: String,
    val color: Color, val items: List<Cmd>
)

val LEVELS = listOf("همه", "ادمین", "Host", "مالک")

val FEATURES = listOf(
    Triple("💃", "دنس و ایموت", "اجرای صدها دنس با شماره و اسم"),
    Triple("🛡️", "مدیریت کامل", "اخراج، بن، سکوت، فریز"),
    Triple("🚀", "تلپورت", "جابه‌جایی آدم‌ها و ربات"),
    Triple("🚫", "ضد اسپم", "هشدار خودکار به اسپمرها"),
    Triple("👋", "خوش‌آمدگویی", "پیام خودکار ورود"),
    Triple("🏆", "برترین‌ها", "لیست برترین‌های روم"),
    Triple("💰", "تیپ و کیف پول", "تیپ دادن به کاربران"),
    Triple("🎭", "واکنش‌ها", "قلب، دست زدن، چشمک و…")
)

val GUIDE = listOf(
    Cat("dance", "💃", "دنس‌ها", "اجرای ایموت و دنس برای خودت", Color(0xFFC64BFF), listOf(
        Cmd("12", "دنس با شماره (انگلیسی یا فارسی، از ۱ تا آخرین شماره)"),
        Cmd("zombie", "دنس با اسم انگلیسی"),
        Cmd("random", "یه دنس شانسی (می‌تونی «رندوم» بنویسی)"),
        Cmd("stop", "توقف دنس (یا «استوپ»). تا stop نزنی دنس تکرار می‌شه"),
        Cmd("!dances 1", "لیست دنس‌ها با شماره و اسم (صفحه‌ای)"),
        Cmd("!dancechain", "زنجیره‌ی ۳ دنس پشت سر هم"),
        Cmd("!punch @username", "مشت‌بازی: تو دنس زدن، اون دنس افتادن")
    )),
    Cat("fun", "🎮", "سرگرمی و واکنش‌ها", "قلب، دست زدن، لیست برترین‌ها و…", Color(0xFFFF7AB8), listOf(
        Cmd("!heart 3 @username", "فرستادن قلب (تعداد اختیاریه)"),
        Cmd("!clap", "واکنش دست زدن (مثل !heart با @username)"),
        Cmd("!wink", "واکنش چشمک"),
        Cmd("!wave", "واکنش دست تکون دادن"),
        Cmd("!thumbs", "واکنش لایک 👍"),
        Cmd("!down", "رفتن به پایین"),
        Cmd("!top", "برترین‌های روم (امتیاز)"),
        Cmd("!users", "لیست افراد روم"),
        Cmd("!info", "اطلاعات ربات و روم"),
        Cmd("!ping", "تست آنلاین بودن ربات"),
        Cmd("سازنده", "نمایش سازنده‌ی ربات (بنویس «سازنده»)")
    )),
    Cat("help", "📚", "راهنما داخل روم", "راهنمای متنی ربات داخل چت روم", Color(0xFF43E68A), listOf(
        Cmd("!help", "راهنمای اصلی (یا !راهنما / !commands)"),
        Cmd("!help dance", "راهنمای دنس‌ها"),
        Cmd("!help fun", "راهنمای سرگرمی"),
        Cmd("!help admin", "راهنمای مدیریت"),
        Cmd("!help owner", "راهنمای مالک و Host")
    )),
    Cat("admin", "🛡️", "مدیریت روم", "مخصوص ادمین‌های ربات", Color(0xFFFFB84D), listOf(
        Cmd("!kick @username", "اخراج از روم", 1),
        Cmd("!mute @username 5", "سکوت کاربر (دقیقه اختیاریه)", 1),
        Cmd("!ban @username", "بن کردن کاربر", 1),
        Cmd("!unban @username", "برداشتن بن", 1),
        Cmd("!banlist", "لیست بن‌شده‌ها", 1),
        Cmd("!mod @username", "ادمین روم کردن", 1),
        Cmd("!unmod @username", "برداشتن ادمین روم", 1),
        Cmd("!freeze @username", "فریز کردن کاربر سر جاش", 1),
        Cmd("!unfreeze @username", "آزاد کردن (یا all برای همه)", 1),
        Cmd("!party @username 12", "اجبار دنس به یه نفر (یا all)", 1),
        Cmd("!partys @username", "توقف دنس اجباری (یا all)", 1),
        Cmd("!antiflood on", "روشن/خاموش ضد اسپم (on/off)", 1)
    )),
    Cat("tele", "🚀", "تلپورت و جابه‌جایی", "جابه‌جا کردن آدم‌ها و ربات توی روم", Color(0xFF6EA8FF), listOf(
        Cmd("!tele @username place", "فرستادن کاربر به یه مکان ذخیره‌شده", 1),
        Cmd("!tele to @username", "رفتن به کنار یه نفر", 1),
        Cmd("!tele me @username", "آوردن اون نفر پیش خودت", 1),
        Cmd("!tele me all", "آوردن همه پیش خودت", 1),
        Cmd("!tele list", "لیست مکان‌های ذخیره‌شده", 1),
        Cmd("!addtele name", "ذخیره‌ی مکان فعلی با اسم", 1),
        Cmd("!deltele name", "حذف مکان ذخیره‌شده", 1),
        Cmd("!vip", "مکان VIP (مشابه !vip1 و !dj)", 1),
        Cmd("!set", "ربات میاد کنار تو", 1)
    )),
    Cat("bot", "🤖", "کنترل خود ربات", "رفتار و ظاهر ربات", Color(0xFFFF5A6A), listOf(
        Cmd("!emotebot 12", "دنس دائمی ربات (شماره یا اسم؛ stop برای توقف)", 1),
        Cmd("!item set @username", "ظاهر ربات مثل اون کاربر بشه", 1),
        Cmd("!loopchat message", "تکرار خودکار یه پیام (stop برای توقف)", 1),
        Cmd("!spam 5 message", "ارسال پیام به تعداد دلخواه", 1),
        Cmd("!welcome message", "پیام خوش‌آمدگویی (on/off هم داره)", 1),
        Cmd("!announce message", "اعلام همگانی", 1),
        Cmd("!interval 60", "فاصله‌ی زمانی پیام‌های خودکار (ثانیه)", 1),
        Cmd("!wallet", "نمایش موجودی کیف پول ربات", 1),
        Cmd("!tip all 5", "تیپ دادن به همه (۱، ۵، ۱۰، ۵۰، ۱۰۰)", 1),
        Cmd("!tip @username 10", "تیپ دادن به یه نفر", 1),
        Cmd("!listadd", "لیست ادمین‌های ربات", 1)
    )),
    Cat("owner", "👑", "ویژه‌ی Host و مالک", "مدیریت سطح بالا", Color(0xFFFF2D46), listOf(
        Cmd("!hosts", "لیست Hostها", 1),
        Cmd("!addadmin @username", "اضافه کردن ادمین ربات", 2),
        Cmd("!removeadmin @username", "حذف ادمین ربات", 2),
        Cmd("!addhost @username", "اضافه کردن Host (فقط مالک)", 3),
        Cmd("!removehost @username", "حذف Host (فقط مالک)", 3)
    ))
)
