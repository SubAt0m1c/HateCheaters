package com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.animations
import me.odinmain.utils.ui.animations.LinearAnimation

/**
 * copied from odin under BSD-3-Clause License
 *
 * todo: llink and stuff this
 */
class Hover(private val startDelay: Long, delay: Long) {
    constructor(delay: Long) : this(0, delay)

    val anim = LinearAnimation<Float>(delay)

    private var hoverStartTime: Long? = null
    var hasStarted = false

    fun percent(): Float {
        if (!animations) return 100f
        if (!hasStarted) return 100 - anim.getPercent()
        return anim.getPercent()
    }

    fun handle(hovered: Boolean) {
        if (!animations) return
        if (hovered) {
            if (hoverStartTime == null) hoverStartTime = System.currentTimeMillis()

            if (System.currentTimeMillis() - hoverStartTime!! >= startDelay && !hasStarted) {
                anim.start()
                hasStarted = true
            }
        } else {
            hoverStartTime = null
            if (hasStarted) {
                anim.start()
                hasStarted = false
            }
        }
    }

    fun reset() {
        if (!animations) return
        hoverStartTime = null
        hasStarted = false
    }
}