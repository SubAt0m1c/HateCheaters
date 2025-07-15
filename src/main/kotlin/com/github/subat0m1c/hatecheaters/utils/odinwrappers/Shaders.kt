package com.github.subat0m1c.hatecheaters.utils.odinwrappers

import me.odinmain.OdinMain.mc
import me.odinmain.utils.ui.rendering.NVGRenderer

object Shaders {
    fun scale(scale: Float) {
        NVGRenderer.scale(scale, scale)
    }

    fun center() {
        NVGRenderer.translate(
            mc.displayWidth / 2f,
            mc.displayHeight / 2f
        )
    }

    fun translate(x: Float, y: Float) {
        NVGRenderer.translate(x, y)
    }

    fun pushMatrix() {
        NVGRenderer.push()
    }

    fun popMatrix() {
        NVGRenderer.pop()
    }

    fun startDraw(x: Float = mc.displayWidth.toFloat(), y: Float = mc.displayHeight.toFloat()) {
        NVGRenderer.beginFrame(x, y)
    }

    fun stopDraw() {
        NVGRenderer.endFrame()
    }

    fun rect(
        x: Float, y: Float, width: Float, height: Float,
        radius: Float = 0f, color: Color
    ) {
        NVGRenderer.rect(
            x, y, width, height,
            color.rgba, radius
        )
    }

    fun rect(
        x: Number, y: Number, width: Number, height: Number,
        radius: Number = 0f, color: Color
    ) {
        NVGRenderer.rect(
            x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(),
            color.rgba, radius.toFloat()
        )
    }

    fun rect(
        box: Box, radius: Float = 0f, color: Color
    ) {
        NVGRenderer.rect(
            box.x, box.y, box.w, box.h,
            color.rgba, radius
        )
    }

    fun scissor(
        x: Float, y: Float, width: Float, height: Float
    ) {
        NVGRenderer.pushScissor(
            x, y, width, height
        )
    }

    fun scissor(
        box: Box
    ) {
        NVGRenderer.pushScissor(
            box.x, box.y, box.w, box.h
        )
    }

    fun popScissor() {
        NVGRenderer.popScissor()
    }
}