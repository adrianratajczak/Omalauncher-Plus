package dev.omalauncher.plus.lockscreen

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * The lock screen: a clock and the date over the system keyguard, dismissed
 * with a swipe up. With a PIN or biometric set, the system asks for it after
 * the swipe, as it should: this screen never sees credentials.
 */
class LockActivity : ComponentActivity() {
    private var unlocking = false

    /** Unlocked another way (another swipe target, the power button dance): get out of the way. */
    private val unlocked =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                if (intent.action == Intent.ACTION_USER_PRESENT) finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        enableEdgeToEdge()
        registerReceiver(unlocked, IntentFilter(Intent.ACTION_USER_PRESENT), RECEIVER_NOT_EXPORTED)
        setContent { LockScreen(onUnlock = ::dismiss) }
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(unlocked) }
        super.onDestroy()
    }

    /** Hands the keyguard back to the system, then gets out of the way: with a PIN set the system keyguard (and its PIN pad) shows; without one the phone is open. */
    private fun dismiss() {
        if (unlocking) return
        unlocking = true
        getSystemService(KeyguardManager::class.java).requestDismissKeyguard(
            this,
            object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissSucceeded() = finish()

                override fun onDismissCancelled() = finish()

                override fun onDismissError() = finish()
            },
        )
        // Some builds never call back; don't stick: the system keyguard underneath is always correct.
        window.decorView.postDelayed({ finish() }, 1500)
    }
}

/** The enabled blocks, in order; the content follows the finger a little on the way up. */
@Composable
private fun LockScreen(onUnlock: () -> Unit) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    // Just past each second, so the clock never visibly lags it.
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000 - System.currentTimeMillis() % 1000)
            now = System.currentTimeMillis()
        }
    }
    Blocks.load(LocalContext.current)
    val time = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }
    val threshold = with(LocalDensity.current) { 96.dp.toPx() }
    var drag by remember { mutableFloatStateOf(0f) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(threshold) {
                detectVerticalDragGestures(
                    onDragStart = { drag = 0f },
                    onDragEnd = {
                        if (drag < -threshold) onUnlock()
                        drag = 0f
                    },
                    onDragCancel = { drag = 0f },
                ) { change, amount ->
                    change.consume()
                    drag += amount
                }
            }.padding(vertical = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.offset { IntOffset(0, (drag * 0.3f).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val shown = Blocks.enabled
            if (Block.Clock in shown) BasicText(time.format(now), style = Mono.copy(fontSize = ClockSize))
            if (Block.Date in shown) BasicText(date.format(now), style = Mono.copy(color = Muted, fontSize = DateSize))
            if (Block.Battery in shown) {
                Spacer(Modifier.height(16.dp))
                BatteryLine()
            }
            if (Block.Actions in shown) {
                Spacer(Modifier.height(32.dp))
                ActionButtons()
            }
            Spacer(Modifier.height(48.dp))
            BasicText("swipe up to unlock", style = Mono.copy(color = Accent, fontSize = HintSize))
        }
    }
}

/** The battery level, live from the sticky battery broadcast. */
@Composable
private fun BatteryLine() {
    val context = LocalContext.current
    var line by remember { mutableStateOf("") }
    DisposableEffect(context) {
        val charge =
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent,
                ) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val percent = (level * 100 / scale.coerceAtLeast(1)).coerceIn(0, 100)
                    val charging =
                        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                    line = "$percent%" + if (charging) " · charging" else ""
                }
            }
        // Sticky: the current level arrives at once, then updates follow.
        context.registerReceiver(charge, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { runCatching { context.unregisterReceiver(charge) } }
    }
    if (line.isNotEmpty()) BasicText(line, style = Mono.copy(color = Muted, fontSize = DateSize))
}

/** Flashlight and camera, side by side under the clock. */
@Composable
private fun ActionButtons() {
    val context = LocalContext.current
    val cameras = remember { context.getSystemService(CameraManager::class.java) }
    val torchId =
        remember {
            runCatching {
                cameras.cameraIdList.firstOrNull { id ->
                    cameras.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
            }.getOrNull()
        }
    var torchOn by remember { mutableStateOf(false) }
    // The hardware tells the truth: another app may hold the torch.
    DisposableEffect(cameras) {
        val watch =
            object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(
                    cameraId: String,
                    enabled: Boolean,
                ) {
                    if (cameraId == torchId) torchOn = enabled
                }
            }
        runCatching { cameras.registerTorchCallback(watch, null) }
        onDispose { runCatching { cameras.unregisterTorchCallback(watch) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LockButton(if (torchOn) "● flashlight" else "flashlight", enabled = torchId != null) {
            if (torchId == null) return@LockButton
            runCatching { cameras.setTorchMode(torchId, !torchOn) }.onFailure { Log.w("lockscreen", "torch", it) }
        }
        LockButton("camera") {
            runCatching { context.startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                .onFailure { Log.w("lockscreen", "camera", it) }
        }
    }
}

/** One outlined action; dimmed when it has nothing to do (no flash on the phone). */
@Composable
private fun LockButton(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(148.dp, 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Foreground.copy(alpha = if (enabled) 0.12f else 0.05f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(label, style = Mono.copy(color = if (enabled) Accent else Muted, fontSize = HintSize))
    }
}
