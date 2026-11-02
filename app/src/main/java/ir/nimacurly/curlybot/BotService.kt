package ir.nimacurly.curlybot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.chaquo.python.Python

/**
 * سرویس پیش‌زمینه: ربات تا وقتی این سرویس زنده است آنلاین می‌ماند.
 * WakeLock و WifiLock جلوی خواب رفتن CPU و وای‌فای را می‌گیرند،
 * و START_STICKY باعث می‌شود اگر اندروید سرویس را کشت، خودش دوباره بالا بیاید.
 */
class BotService : Service() {

    companion object {
        const val ACTION_START = "ir.nimacurly.curlybot.START"
        const val ACTION_STOP = "ir.nimacurly.curlybot.STOP"
        private const val CHANNEL = "curlybot_channel"
        private const val NOTIF_ID = 1

        fun start(ctx: Context) {
            val i = Intent(ctx, BotService::class.java).setAction(ACTION_START)
            ContextCompat.startForegroundService(ctx, i)
        }

        fun stop(ctx: Context) {
            ctx.startService(Intent(ctx, BotService::class.java).setAction(ACTION_STOP))
        }
    }

    @Volatile private var alive = false
    private var wake: PowerManager.WakeLock? = null
    private var wifi: WifiManager.WifiLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            shutdown()
            return START_NOT_STICKY
        }

        val notif = buildNotification()
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(NOTIF_ID, notif)
            }
        } catch (_: Throwable) {
            stopSelf()
            return START_NOT_STICKY
        }
        acquireLocks()

        val room = Prefs.room(this)
        val token = Prefs.token(this)
        val owner = Prefs.owner(this)
        val admins = Prefs.admins(this)
        val dir = filesDir.absolutePath
        if (room.isBlank() || token.isBlank() || owner.isBlank()) {
            shutdown()
            return START_NOT_STICKY
        }
        alive = true
        Thread {
            try {
                Python.getInstance().getModule("runner").callAttr("start", room, token, owner, admins, dir)
            } catch (_: Throwable) {
            }
            // نگهبان: اگه ربات بمیره (مثلاً خطای import)، سرویس و نوتیفیکیشن «آنلاین» هم بسته می‌شه
            var misses = 0
            try { Thread.sleep(8000) } catch (_: InterruptedException) {}
            while (alive) {
                val up = try {
                    Python.getInstance().getModule("runner").callAttr("is_running").toBoolean()
                } catch (_: Throwable) { false }
                if (up) misses = 0 else misses++
                if (misses >= 2) { alive = false; shutdown(); break }
                try { Thread.sleep(3000) } catch (_: InterruptedException) { break }
            }
        }.start()
        return START_STICKY
    }

    private fun shutdown() {
        alive = false
        Thread {
            try {
                Python.getInstance().getModule("runner").callAttr("stop")
            } catch (_: Throwable) {
            }
            releaseLocks()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }.start()
    }

    private fun acquireLocks() {
        if (wake == null) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "curlybot:wake").apply {
                setReferenceCounted(false)
                acquire()
            }
        }
        if (wifi == null) {
            val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifi = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "curlybot:wifi").apply {
                setReferenceCounted(false)
                acquire()
            }
        }
    }

    private fun releaseLocks() {
        try { wake?.takeIf { it.isHeld }?.release() } catch (_: Throwable) {}
        try { wifi?.takeIf { it.isHeld }?.release() } catch (_: Throwable) {}
        wake = null
        wifi = null
    }

    private fun buildNotification(): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL, "CurlyBot", NotificationManager.IMPORTANCE_LOW)
        )
        val open = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stop = PendingIntent.getService(
            this, 1, Intent(this, BotService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return Notification.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("CurlyBot آنلاین است")
            .setContentText("ربات هایرایز در حال اجراست")
            .setContentIntent(open)
            .setOngoing(true)
            .addAction(Notification.Action.Builder(
                android.graphics.drawable.Icon.createWithResource(this, R.drawable.ic_notif), "توقف", stop
            ).build())
            .build()
    }

    override fun onDestroy() {
        alive = false
        releaseLocks()
        super.onDestroy()
    }
}
