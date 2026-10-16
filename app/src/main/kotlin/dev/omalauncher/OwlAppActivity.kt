package dev.omalauncher

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import dev.omalauncher.ui.BackTitle
import dev.omalauncher.ui.PanelFrame
import dev.omalauncher.ui.PanelGap
import dev.omalauncher.ui.Palette
import dev.omalauncher.ui.applySystemBars
import dev.omalauncher.widgets.lang.text
import dev.omalauncher.widgets.render.LocalInputFocus
import dev.omalauncher.widgets.render.WidgetScreen

/**
 * An owl app (`app "name" { }`) as its own task and Recents card, named after it and with its icon,
 * opened from the drawer or a home icon. It stays live while it shows, home or not; its close(),
 * or the app going away (removed, or no longer compiling), closes the task.
 */
class OwlAppActivity : InstantActivity() {
    private val runtime get() = (application as App).runtime
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.data?.schemeSpecificPart
        // Gone since Recents was built, or the process restarted before the files loaded.
        if (name == null || runtime.widgets.appInstance(name) == null) {
            finishAndRemoveTask()
            return
        }
        this.name = name
        applySystemBars(this, Palette.isLight)
        setInstantContent {
            LaunchedEffect(Palette.isLight) { applySystemBars(this@OwlAppActivity, Palette.isLight) }
            LaunchedEffect(name) { runtime.widgets.appCloses.collect { if (it == name) closeInstantly() } }
            val (spec, inst) =
                runtime.widgets.appInstance(name) ?: run {
                    LaunchedEffect(Unit) { closeInstantly() }
                    return@setInstantContent
                }
            val icon = runtime.owlIcon(spec, mono = false)
            LaunchedEffect(spec.name, icon, Palette.accent) { describeTask(spec.name, icon?.asAndroidBitmap()) }
            val title = inst.program.screenTitle?.let { inst.eval(it, inst.root, "title").text() } ?: spec.name
            // While the app's `back` holds, Back and the chevron go up a level inside it; otherwise Back leaves it.
            val up = inst.program.back?.let { inst.eval(it, inst.root, "back").truthy } == true
            val goUp: () -> Unit = { inst.program.onBack?.run(inst.root, emptyList()) }
            BackHandler { if (up) goUp() else backInstantly() }
            Box(Modifier.fillMaxSize().background(Palette.background).safeDrawingPadding().padding(PanelGap)) {
                // No ✕: Back leaves the app and Recents closes it, as with any Android app; close() still does.
                PanelFrame(onClose = null, title = { BackTitle(title, if (up) goUp else null) }) {
                    CompositionLocalProvider(LocalInputFocus provides true) {
                        WidgetScreen(inst, Modifier.weight(1f).fillMaxWidth())
                    }
                }
            }
        }
    }

    /** Recents names the card after the app, with its icon, else its initials in the theme colors. */
    private fun describeTask(
        label: String,
        appIcon: android.graphics.Bitmap?,
    ) {
        val icon = appIcon ?: taskIcon(label, Palette.background.toArgb(), Palette.accent.toArgb())
        val description =
            if (Build.VERSION.SDK_INT >= 37) {
                ActivityManager.TaskDescription
                    .Builder()
                    .setLabel(label)
                    .setIcon(Icon.createWithBitmap(icon))
                    .setPrimaryColor(Palette.background.toArgb())
                    .build()
            } else {
                // Builder.setIcon(Icon) is API 37+; the bitmap constructor still works before that.
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(label, icon, Palette.background.toArgb())
            }
        setTaskDescription(description)
    }

    override fun onStart() {
        super.onStart()
        name?.let(runtime.widgets::appStarted)
        runtime.screenStarted()
    }

    override fun onStop() {
        super.onStop()
        name?.let(runtime.widgets::appStopped)
        runtime.screenStopped()
    }

    companion object {
        /** The app in file [name]; the data names the document, so each app gets one task. */
        fun intent(
            context: Context,
            name: String,
        ): Intent = Intent(context, OwlAppActivity::class.java).setData(Uri.fromParts("owl", name, null))
    }
}
