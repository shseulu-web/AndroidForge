package com.hexablockyt.ffmaxcompanion

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingPanelService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var panel: LinearLayout? = null
    private var expanded = false
    private var params: WindowManager.LayoutParams? = null
    private val green = Color.rgb(65, 230, 150)
    private val dark = Color.rgb(18, 23, 32)
    private val white = Color.WHITE
    private val gray = Color.rgb(180, 190, 204)

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun layoutParams(width: Int, height: Int) = WindowManager.LayoutParams(
        width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply { gravity = Gravity.TOP or Gravity.START; x = 24; y = 220 }

    private fun showBubble() {
        if (bubble != null) return
        val b = TextView(this).apply {
            text = "HB"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
            setBackgroundColor(green)
            setPadding(0, 0, 0, 0)
            elevation = 12f
            setOnClickListener { if (expanded) hidePanel() else showPanel() }
            setOnTouchListener(object : View.OnTouchListener {
                var sx = 0f; var sy = 0f; var ox = 0; var oy = 0; var moved = false
                override fun onTouch(v: View, e: MotionEvent): Boolean {
                    val p = params ?: return false
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> { sx=e.rawX; sy=e.rawY; ox=p.x; oy=p.y; moved=false; return true }
                        MotionEvent.ACTION_MOVE -> {
                            val dx=(e.rawX-sx).toInt(); val dy=(e.rawY-sy).toInt()
                            if (kotlin.math.abs(dx)+kotlin.math.abs(dy)>8) moved=true
                            p.x=ox+dx; p.y=oy+dy
                            try { wm.updateViewLayout(bubble, p) } catch (_: Exception) {}
                            return true
                        }
                        MotionEvent.ACTION_UP -> { if (!moved) { if (expanded) hidePanel() else showPanel() }; return true }
                    }
                    return false
                }
            })
        }
        bubble = b
        params = layoutParams(dp(54), dp(54))
        try { wm.addView(b, params) } catch (_: Exception) { stopSelf() }
    }

    private fun showPanel() {
        if (panel != null) return
        expanded = true
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            setBackgroundColor(dark)
            elevation = 16f
        }
        box.addView(text("HEXABLOCK • QUICK PANEL", 13, green, true))
        box.addView(text("Gaming shortcuts", 11, gray, false))
        box.addView(action("OPEN FREE FIRE INDIA") { launchGame() })
        box.addView(action("HUD REMINDER") { Toast.makeText(this, "Use your saved in-game HUD layout", Toast.LENGTH_SHORT).show() })
        box.addView(action("FOCUS MODE: ON / OFF") { Toast.makeText(this, "Focus reminder toggled", Toast.LENGTH_SHORT).show() })
        box.addView(action("CLOSE PANEL") { hidePanel() })
        panel = box
        val p = layoutParams(dp(220), WindowManager.LayoutParams.WRAP_CONTENT).apply {
            x = (params?.x ?: dp(24)); y = (params?.y ?: dp(220)) + dp(62)
        }
        try { wm.addView(box, p) } catch (_: Exception) { panel = null; expanded = false }
    }

    private fun action(title: String, run: () -> Unit) = text(title, 12, white, true).apply {
        setPadding(0, dp(12), 0, dp(8))
        setOnClickListener { run() }
    }

    private fun text(value: String, size: Int, color: Int, bold: Boolean) = TextView(this).apply {
        text = value; textSize = size.toFloat(); setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    private fun hidePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        panel = null; expanded = false
    }

    private fun launchGame() {
        val i = packageManager.getLaunchIntentForPackage("com.dts.freefireth")
        if (i != null) { i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i) }
        else Toast.makeText(this, "Free Fire India not found", Toast.LENGTH_SHORT).show()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY
    override fun onDestroy() {
        hidePanel()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
