package com.github.subat0m1c.hatecheaters.utils.odinwrappers

import me.odinmain.utils.minus
import me.odinmain.utils.noControlCodes
import me.odinmain.utils.plus
import me.odinmain.utils.ui.rendering.NVGRenderer

object Text {
    fun text(
        text: String?, x: Number, y: Number,
        scale: Float = 1f, color: Color, center: Boolean = true
    ) {
        if (text == null) return
        var i = 0
        var color = color
        var xPos = if (center) {
            x - (getWidth(text.noControlCodes, scale) / 2f)
        } else {
            x.toFloat()
        }
        var bold = false
        while (i < text.length) {
            val char = text[i]
            if (char == '\u00a7' && i + 1 < text.length) {
                val char = text.lowercase()[i + 1]
                if (char == 'l') {
                    bold = true
                    i += 2
                    continue
                }
                if (char == 'r') {
                    bold = false
                }
                val colorCode = "0123456789abcdefr".indexOf(text.lowercase()[i + 1])
                color = colorCodes[colorCode]
                i += 2
                continue
            }
            rawText(char.toString(), xPos, y, scale, color, bold)
            xPos += getWidth(char.toString(), scale)
            i++
        }
    }

    fun rawText(text: String, x: Number, y: Number, scale: Float = 1f, color: Color, bold: Boolean = false) {
        NVGRenderer.text(
            text,
            x.toFloat(),
            y.toFloat(),
            (if (bold) 10 else 9) * scale,
            color.rgba,
            NVGRenderer.defaultFont
        )
    }

    fun getWidth(text: String, scale: Float = 1f): Float {
        return NVGRenderer.textWidth(text, 9 * scale, NVGRenderer.defaultFont)
    }

    fun textHeight(scale: Number): Float {
        return 9f * scale.toFloat()
    }

    val colorCodes = arrayOf(
        Color(0, 0, 0),
        Color(0, 0, 170),
        Color(0, 170, 0),
        Color(0, 170, 170),
        Color(170, 0, 0),
        Color(170, 0, 170),
        Color(255, 170, 0),
        Color(170, 170, 170),
        Color(85, 85, 85),
        Color(85, 85, 255),
        Color(85, 255, 85),
        Color(85, 255, 255),
        Color(255, 85, 85),
        Color(255, 85, 255),
        Color(255, 255, 85),
        Color(255, 255, 255),
        Color(255, 255, 255)
    )
}