package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Color
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Colors
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders

fun <T> buttons(
    box: Box,
    padding: Int,
    ot: Number,
    default: T,
    options: List<T>,
    textScale: Number,
    color: Color,
    accent: Color,
    radius: Float = 0f,
    edgeSoftness: Float = 0f,
    vertical: Boolean = false,
    buttonDSL: ButtonDSL<T>.() -> Unit
): ButtonDSL<T> = ButtonDSL(box, padding, ot, default, options, textScale, color, accent, radius, edgeSoftness, vertical).apply(buttonDSL)

class ButtonDSL<T>(
    private val box: Box,
    private val padding: Int,
    private val outlineThickness: Number,
    private val default: T,
    private val options: List<T>,
    private val textScale: Number,
    private val color: Color,
    private val accent: Color,
    private val radius: Float = 0f,
    private val edgeSoftness: Float = 0f,
    private val vertical: Boolean
) {
    private val lineY =
        10 // same value as the profile viewer lineY but i dont want to un protect it. Should be moved to a padding value instead thouhg.

    private var selected: T = default
    private var onSelect: (T) -> Unit = {}

    fun onSelect(init: (T) -> Unit) { onSelect = init }

    private val buttonWidth = (box.w - (options.size-1)*padding)/options.size
    private val buttonHeight = (box.h - (options.size-1)*padding)/options.size

    val getSelected get() = selected

    fun draw() {
        options.forEachIndexed { i, option ->
            val y = if (!vertical) box.y else box.y + (buttonHeight + lineY) * i
            val x = if (!vertical) box.x + (buttonWidth + lineY) * i else box.x
            if (option == selected) Shaders.rect(x, y, buttonWidth, box.h, radius, accent)
            else Shaders.rect(x, y, buttonWidth, box.h, radius, color)
            centeredText(option.toString(), x + buttonWidth / 2, box.y + box.h / 2, textScale.toFloat(), Colors.WHITE)
        }
    }

    fun click(mouseX: Int, mouseY: Int, button: Int) {
        options.withIndex().find { (i, _) ->
            isObjectHovered(Box(box.x + (buttonWidth + lineY) * i, box.y, buttonWidth, box.h), mouseX, mouseY)
        }?.let { (_, entry) ->
            if (entry == selected) return@let
            selected = entry
            onSelect(entry)
        }
    }
}