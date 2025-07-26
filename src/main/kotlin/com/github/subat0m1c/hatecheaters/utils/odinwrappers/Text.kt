package com.github.subat0m1c.hatecheaters.utils.odinwrappers

import me.odinmain.utils.noControlCodes
import me.odinmain.utils.ui.rendering.NVGRenderer

object Text {
    enum class Centering {
        LEFT, CENTER, RIGHT
    }

    enum class Alignment {
        ABOVE, MIDDLE, BELOW
    }

    fun fillText(
        text: String?,
        x: Number,
        y: Number,
        width: Number,
        maxHeight: Number,
        color: Color,
        alignment: Alignment = Alignment.MIDDLE
    ) {
        val scaleFactor: Float = width.toFloat() / getWidthSized(text.toString().noControlCodes, maxHeight.toFloat())
        colorText(
            text,
            x.toFloat(),
            y.toFloat(),
            maxHeight.toFloat() * scaleFactor.coerceAtMost(1f),
            color,
            Centering.CENTER,
            alignment
        )
    }

    fun text(
        text: String?, x: Number, y: Number,
        scale: Float = 1f, color: Color, centering: Centering = Centering.CENTER, alignment: Alignment = Alignment.BELOW
    ) {
        colorText(text, x, y, 9 * scale, color, centering, alignment)
    }

    fun rawText(text: String, x: Number, y: Number, height: Float, color: Color, bold: Boolean = false) {
        NVGRenderer.text(
            text,
            x.toFloat(),
            y.toFloat(),
            (if (bold) 1 else 0) + height,
            color.rgba,
            NVGRenderer.defaultFont
        )
    }

    fun colorText(
        text: String?, x: Number, y: Number, height: Float, color: Color,
        centering: Centering = Centering.CENTER, alignment: Alignment = Alignment.BELOW
    ) {
        if (text == null) return
        var i = 0
        var color = color

        val yPos = when (alignment) {
            Alignment.BELOW -> y.toFloat()
            Alignment.MIDDLE -> y.toFloat() - height / 2f
            Alignment.ABOVE -> y.toFloat() - height
        }

        var xPos = when (centering) {
            Centering.LEFT -> x.toFloat()
            Centering.CENTER -> x.toFloat() - getWidthSized(text.noControlCodes, height) / 2f
            Centering.RIGHT -> x.toFloat() - getWidthSized(text.noControlCodes, height)
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
                val colorCode = "0123456789abcdefr".indexOf(text.lowercase()[i + 1]).takeUnless { it == -1 } ?: 16
                color = colorCodes[colorCode]
                i += 2
                continue
            }
            rawText(char.toString(), xPos, yPos, height, color, bold)
            xPos += getWidthSized(char.toString(), height)
            i++
        }
    }

    fun getWidth(text: String, scale: Float = 1f): Float {
        return NVGRenderer.textWidth(text, 9 * scale, NVGRenderer.defaultFont)
    }

    fun getWidthSized(text: String, size: Float): Float {
        return NVGRenderer.textWidth(text, size, NVGRenderer.defaultFont)
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