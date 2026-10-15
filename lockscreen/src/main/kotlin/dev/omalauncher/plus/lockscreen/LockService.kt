package dev.omalauncher.plus.lockscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

/**
 * Keeps the lock screen coming: on screen-off it raises [LockActivity] as a
 * full-screen intent, the only way to show an activity over the keyguard from
 * the background on modern Android.
 */
class LockService : Service() {
    private val screenOff =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                if (intent.action == Intent.ACTION_SCREEN_OFF) wake()
            }
        }

    override fun onCreate() {
        super.onCreate()
        channels()
        startForeground(ONGOING, status())
        registerReceiver(screenOff, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(screenOff) }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ) = START_STICKY

    /** The quiet note that keeps the service in the foreground. */
    private fun status(): Notification =
        Notification
            .Builder(this, ONGOING_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Omalauncher+ Lock is on")
            .build()

    /** The tap that wakes the lock screen; the system shows it because the screen is off. */
    private fun wake() {
        val open =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, LockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val wake =
            Notification
                .Builder(this, WAKE_CHANNEL)
                .setSmallIcon(android.R.drawable.ic_lock_lock)
                .setContentTitle("Lockscreen")
                .setFullScreenIntent(open, true)
                .setAutoCancel(true)
                .build()
        getSystemService(NotificationManager::class.java).notify(WAKE, wake)
    }

    private fun channels() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(ONGOING_CHANNEL, "Lockscreen", NotificationManager.IMPORTANCE_MIN))
        manager.createNotificationChannel(NotificationChannel(WAKE_CHANNEL, "Wake up", NotificationManager.IMPORTANCE_HIGH))
    }

    companion object {
        private const val ONGOING = 1
        private const val WAKE = 2
        private const val ONGOING_CHANNEL = "lock"
        private const val WAKE_CHANNEL = "wake"
    }
}

/** After a reboot the screen-off watch is gone; start it once the user unlocks. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) context.startForegroundService(Intent(context, LockService::class.java))
    }
}
