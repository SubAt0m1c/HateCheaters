package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory.EnderChest.lineY
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import me.odinmain.utils.div
import me.odinmain.utils.minus
import me.odinmain.utils.plus
import me.odinmain.utils.render.*
import me.odinmain.utils.times

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
    private var selected: T = default
    private var onSelect: (T) -> Unit = {}

    fun onSelect(init: (T) -> Unit) { onSelect = init }

    private val buttonWidth = (box.w - (options.size-1)*padding)/options.size
    private val buttonHeight = (box.h - (options.size-1)*padding)/options.size

    val getSelected get() = selected

    fun draw() {
        options.forEachIndexed { i, option ->
            val y = if (!vertical) box.y else box.y + (buttonHeight + lineY)*i
            val x = if (!vertical) box.x + (buttonWidth + lineY)*i else box.x
            if (option == selected) roundedRectangle(x, y, buttonWidth, box.h, accent, radius, edgeSoftness)
            else roundedRectangle(x, y, buttonWidth, box.h, color, radius, edgeSoftness)
            centeredText(option.toString(), x + buttonWidth/2, box.y + box.h/2, textScale, Color.WHITE, shadow = true)
        }
    }

    fun click(mouseX: Int, mouseY: Int, button: Int) {
        options.withIndex().find { (i, _) ->
            val x = box.x + (buttonWidth + lineY)*i
            isObjectHovered(Box(x, box.y, buttonWidth, box.h), mouseX, mouseY)
        }?.let { (_, entry) ->
            selected = entry
            onSelect(entry)
        }
    }
}