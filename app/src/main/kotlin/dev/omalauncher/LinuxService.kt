package dev.omalauncher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import dev.omalauncher.linux.LinuxState
import dev.omalauncher.ui.PlusPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Keeps the process (and with it proot and the Wayland server) in the
 * foreground while Linux is installed or installing; never without Linux.
 * The notification shows the install's progress.
 */
class LinuxService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var watch: Job? = null

    /** Whether the notification is up; Plus takes it down while Linux idles. */
    private var foreground = false

    /** The channel the notification posts to. */
    private var channel = CHANNEL

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val nm = getSystemService(NotificationManager::class.java)
        PlusPrefs.load(this)
        nm.createNotificationChannel(NotificationChannel(CHANNEL, "Linux session", NotificationManager.IMPORTANCE_LOW))
        channel = CHANNEL
        val state = (application as App).runtime.bootstrap.state
        // A foreground service must show one at once; the watch takes it down again when Plus hides it.
        foreground = true
        startForeground(1, notification(state.value), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        if (watch == null) {
            watch =
                scope.launch {
                    state.collect { show(nm, it) }
                }
        }
        // Not restarted after the process dies: Linux only comes back with the launcher (Runtime.start),
        // and Android 12+ may refuse startForeground() in a restart from the background.
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun notification(state: LinuxState) = build(text(state))

    /**
     * Shows the state, or takes the notification down: hidden while Linux idles with Plus hiding it.
     * Installs, errors and removals always show, whatever the setting.
     */
    private fun show(
        nm: NotificationManager,
        state: LinuxState,
    ) {
        val hide = PlusPrefs.hideLinuxNotification && state !is LinuxState.Installing && state !is LinuxState.Failed && state != LinuxState.Removing
        if (hide) {
            if (foreground) {
                foreground = false
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        } else {
            if (!foreground) {
                foreground = true
                startForeground(1, notification(state), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                nm.notify(1, build(text(state)))
            }
        }
    }

    private fun text(state: LinuxState): String =
        when (state) {
            is LinuxState.Installing -> "Installing Linux" + (state.progress?.let { " · ${(it * 100).toInt()}%" } ?: "") + " · ${state.stage.label}"
            is LinuxState.Failed -> "Linux setup stopped · open settings › linux to retry"
            LinuxState.Removing -> "Removing Linux"
            else -> "Linux session running"
        }

    private fun build(text: String): Notification =
        Notification
            .Builder(this, channel)
            .setContentTitle(text)
            .setSmallIcon(if (PlusPrefs.hideLinuxNotification) R.drawable.ic_notification_empty else R.drawable.ic_notification)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    companion object {
        private const val CHANNEL = "session"
    }
}
