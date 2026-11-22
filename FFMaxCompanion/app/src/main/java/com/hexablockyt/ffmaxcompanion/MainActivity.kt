package com.hexablockyt.ffmaxcompanion

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
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
            setPadding(dp(20), dp(24), dp(20), dp(24))
            setBackgroundColor(bg)
        }

        page.addView(label("FF MAX", 30, accent, true))
        page.addView(label("COMPANION  •  OB55 READY UI", 12, muted, true))
        page.addView(space(22))
        page.addView(label("Gaming dashboard", 22, white, true))
        page.addView(label("Apne gameplay setup ko ek jagah manage karo.", 14, muted, false))
        page.addView(space(18))

        addTile(page, "BATTLE ROYALE", "BR setup notes aur HUD planning", "Open BR planner")
        addTile(page, "CLASH SQUAD", "CS ke liye alag control setup save karo", "Open CS planner")
        addTile(page, "SENSITIVITY MANAGER", "Apni tested sensitivity values save karo", "Edit sensitivity")
        addTile(page, "CUSTOM CROSSHAIR", "Practice ke liye visual crosshair preview", "Open crosshair")
        addTile(page, "DEVICE PERFORMANCE", "FPS, battery aur storage tips", "View tips")

        page.addView(space(12))
        page.addView(label(
            "Note: Ye companion utility hai. Ye game files modify nahi karti, " +
                "hidden enemies reveal nahi karti aur anti-cheat bypass nahi karti.",
            12, muted, false
        ))
        setContentView(page)
    }

    private fun addTile(parent: LinearLayout, title: String, desc: String, action: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setBackgroundColor(panel)
        }
        card.addView(label(title, 15, accent, true))
        card.addView(label(desc, 13, muted, false))
        val button = TextView(this).apply {
            text = action.uppercase()
            textSize = 12f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(12), 0, 0)
            setOnClickListener {
                Toast.makeText(this@MainActivity, "$title planner — next update mein!", Toast.LENGTH_SHORT).show()
            }
        }
        card.addView(button)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(12) }
        parent.addView(card, params)
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
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
