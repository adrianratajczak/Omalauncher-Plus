package dev.omalauncher.plus.lockscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** The app's only settings: one button that asks for notifications and starts the watch. */
class SetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SetupScreen() }
    }
}

@Composable
private fun SetupScreen() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(false) }
    Blocks.load(context)
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { start(context).also { on = it } }
    Box(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BasicText("omalauncher+ lock", style = Mono.copy(fontSize = DateSize))
            Spacer(Modifier.height(8.dp))
            BasicText(
                if (on) "on: locking the phone shows the clock" else "shows a clock over the lock screen",
                style = Mono.copy(color = Muted, fontSize = HintSize),
            )
            Spacer(Modifier.height(24.dp))
            BasicText(
                if (on) "● on" else "tap to enable",
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Foreground.copy(alpha = 0.12f))
                    .clickable {
                        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            on = start(context)
                        }
                    }.padding(horizontal = 24.dp, vertical = 14.dp),
                style = Mono.copy(color = Accent, fontSize = DateSize),
            )
            Spacer(Modifier.height(32.dp))
            BasicText("blocks", style = Mono.copy(color = Muted, fontSize = HintSize))
            Spacer(Modifier.height(8.dp))
            for (block in Block.entries) {
                val picked = block in Blocks.enabled
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (picked) Accent.copy(alpha = 0.12f) else Foreground.copy(alpha = 0.05f))
                        .clickable { Blocks.toggle(context, block) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        block.label,
                        Modifier.weight(1f),
                        style = Mono.copy(color = if (picked) Accent else Foreground, fontSize = DateSize),
                    )
                    BasicText(if (picked) "● " else "  ", style = Mono.copy(color = Accent))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/** Starts the screen-off watch; false when the service couldn't start. */
private fun start(context: android.content.Context) =
    runCatching {
        context.startForegroundService(Intent(context, LockService::class.java))
        true
    }.getOrDefault(false)
