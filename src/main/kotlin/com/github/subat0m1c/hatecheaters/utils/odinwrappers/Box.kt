package com.github.subat0m1c.hatecheaters.utils.odinwrappers

data class Box(val x: Float, val y: Float, val w: Float, val h: Float) {
    constructor(
        x: Number, y: Number, width: Number, height: Number
    ) : this(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())

    val centerX: Float get() = x + w / 2
    val centerY: Float get() = y + h / 2
}