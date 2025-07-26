package com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.animations
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Color
import me.odinmain.utils.ui.animations.LinearAnimation

class Button(delay: Long) {
    val anim = LinearAnimation<Int>(delay)

    private var selectedStartTime: Long? = null
    var hasStarted = false

    fun handle(selected: Boolean) {
        if (!animations) return

        if (selected) {
            if (selectedStartTime == null) selectedStartTime = System.currentTimeMillis()
            if (System.currentTimeMillis() - selectedStartTime!! >= 0 && !hasStarted) {
                anim.start()
                hasStarted = true
            }
        } else {
            selectedStartTime = null
            if (hasStarted) {
                anim.start()
                hasStarted = false
            }
        }
    }

    fun getColor(selected: Boolean, start: Color, end: Color, reverse: Boolean): Color {
        if (!animations) return if (selected) end else start

        return Color(
            anim.get(start.odin.red, end.odin.red, reverse),
            anim.get(start.odin.green, end.odin.green, reverse),
            anim.get(start.odin.blue, end.odin.blue, reverse),
            anim.get(start.odin.alpha, end.odin.alpha, reverse) / 255f,
        )
    }
//
//    fun reset() {
//        if (!animations) return
//        hoverStartTime = null
//        hasStarted = false
//    }
}