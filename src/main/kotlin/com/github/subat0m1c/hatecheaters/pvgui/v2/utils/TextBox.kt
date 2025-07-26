package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Color
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text


class TextBox(
    val box: Box,
    val title: String?,
    val titleScale: Float,
    val text: List<String>,
    val scale: Float,
    val spacer: Float,
    val defaultColor: Color
) {
    val entryHeight = box.h / (text.size + if (title != null) 1 else 0)
    val itemY = entryHeight / 2
    val centerX = box.w / 2

    fun draw() {
        Shaders.pushMatrix()
//        Shaders.translate(box.x, box.y) DONT DO THIS IT MAKES FONT BLURRY SOMETIMES

        title?.let {
            Text.text(
                title,
                box.x + centerX,
                box.y + entryHeight - itemY,
                titleScale,
                defaultColor,
                alignment = Text.Alignment.MIDDLE
            )
        }

        text.forEachIndexed { i, s ->
            val y = box.y + (entryHeight * (i + if (title != null) 2 else 1))
            Text.text(
                s,
                box.x,
                y - itemY,
                scale,
                defaultColor,
                centering = Text.Centering.LEFT,
                alignment = Text.Alignment.MIDDLE
            )
        }

        Shaders.popMatrix()
    }
}