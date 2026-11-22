package com.hexablockyt.ffmaxcompanion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val bg = Color.rgb(12, 15, 22)
    private val panel = Color.rgb(25, 31, 43)
    private val accent = Color.rgb(65, 230, 150)
    private val white = Color.WHITE
    private val muted = Color.rgb(170, 180, 194)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(dp(20), dp(28), dp(20), dp(24))
            setBackgroundColor(bg)
        }
        page.addView(label("FF MAX", 30, accent, true))
        page.addView(label("COMPANION  •  FLOATING QUICK PANEL", 12, muted, true))
        page.addView(space(24))
        page.addView(label("Gaming overlay", 23, white, true))
        page.addView(label("Game ke upar movable floating bubble aur quick shortcuts.", 14, muted, false))
        page.addView(space(18))
        addAction(page, "ENABLE FLOATING PANEL", "Screen par movable circle show kare", true) { enableOverlay() }
        addAction(page, "OPEN FREE FIRE INDIA", "Game installed hai to launch kare", false) { launchGame() }
        addAction(page, "HIDE FLOATING PANEL", "Overlay ko band kare", false) {
            stopService(Intent(this, FloatingPanelService::class.java))
            Toast.makeText(this, "Floating panel hidden", Toast.LENGTH_SHORT).show()
        }
        page.addView(space(18))
        page.addView(label("Panel controls", 18, white, true))
        page.addView(label("Quick notes • HUD reminder • Focus mode • Game launch", 13, muted, false))
        page.addView(space(20))
        page.addView(label(
            "Overlay permission Android settings se allow karni hogi. No root, VPN ya Shizuku required. " +
                "This companion does not read game data, reveal hidden players, or bypass anti-cheat.",
            12, muted, false
        ))
        setContentView(page)
    }

    private fun enableOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            Toast.makeText(this, "Allow 'Display over other apps', then tap Enable again", Toast.LENGTH_LONG).show()
            return
        }
        startService(Intent(this, FloatingPanelService::class.java))
        Toast.makeText(this, "Floating panel enabled", Toast.LENGTH_SHORT).show()
    }

    private fun launchGame() {
        val intent = packageManager.getLaunchIntentForPackage("com.dts.freefireth")
        if (intent != null) startActivity(intent)
        else Toast.makeText(this, "Free Fire India is not installed on this device", Toast.LENGTH_LONG).show()
    }

    private fun addAction(parent: LinearLayout, title: String, desc: String, primary: Boolean, action: () -> Unit) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            setBackgroundColor(panel)
        }
        card.addView(label(title, 14, if (primary) accent else white, true))
        card.addView(label(desc, 12, muted, false))
        card.setOnClickListener { action() }
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            .apply { bottomMargin = dp(12) }
        parent.addView(card, params)
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean) = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    private fun space(height: Int) = android.view.View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
