package com.github.subat0m1c.hatecheaters.pvgui.v2.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations.Button
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations.Hover
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations.HoverableButton
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*

fun <T> buttons(
    box: Box,
    padding: Int,
    default: T,
    options: List<T>,
    textScale: Number,
    color: Color,
    selectedColor: Color,
    radius: Float = 0f,
    vertical: Boolean = false,
    buttonDSL: ButtonDSL<T>.() -> Unit
): ButtonDSL<T> =
    ButtonDSL(box, padding, default, options, textScale, color, selectedColor, radius, vertical).apply(buttonDSL)

class ButtonDSL<T>(
    private val box: Box,
    private val padding: Int,
    private val default: T,
    private val options: List<T>,
    private val textScale: Number,
    private val color: Color,
    private val selectedColor: Color,
    private val radius: Float = 0f,
    private val vertical: Boolean
) {
    private val animations = List(options.size) { HoverableButton(Hover(0, 250), Button(150)) }

    private val lineY =
        10 // same value as the profile viewer lineY but i dont want to un protect it. Should be moved to a padding value instead thouhg.

    var selected: T = default
    private var onSelect: (T) -> Unit = {}

    fun onSelect(init: (T) -> Unit) { onSelect = init }

    private val buttonWidth = if (!vertical) (box.w - (options.size - 1) * padding) / options.size else box.w
    private val buttonHeight = if (!vertical) box.h else (box.h - (options.size - 1) * padding) / options.size

    fun draw(mouseX: Int, mouseY: Int) {
        options.forEachIndexed { i, option ->
            val x = if (!vertical) box.x + (buttonWidth + lineY) * i else box.x
            val y = if (!vertical) box.y else box.y + (buttonHeight + lineY) * i

            val hovered = isObjectHovered(Box(x, y, buttonWidth, buttonHeight), mouseX, mouseY)

            val selected = option == selected
            val hoverAnim = animations[i].hover.apply { handle(hovered) }
            val buttonAnim = animations[i].button.apply { handle(selected) }
            val color = buttonAnim.getColor(selected, color, selectedColor, !buttonAnim.hasStarted)
                .brighter(1 + hoverAnim.percent() / 500f)

            //Shaders.hollowRect(x, y, buttonWidth, buttonHeight, radius, hoverAnim.anim.get(0f, 3f, !hoverAnim.hasStarted), Colors.WHITE)
            Shaders.rect(x, y, buttonWidth, buttonHeight, radius, color)

            Text.text(
                option.toString(),
                x + buttonWidth / 2,
                y + buttonHeight / 2,
                textScale.toFloat(),
                Colors.WHITE,
                alignment = Text.Alignment.MIDDLE
            )
        }
    }

    fun click(mouseX: Int, mouseY: Int, button: Int) {
        options.withIndex().find { (i, _) ->
            val x = if (!vertical) box.x + (buttonWidth + lineY) * i else box.x
            val y = if (!vertical) box.y else box.y + (buttonHeight + lineY) * i

            isObjectHovered(Box(x, y, buttonWidth, buttonHeight), mouseX, mouseY)
        }?.let { (_, entry) ->
            if (entry == selected) return@let
            //modMessage("clicked: ${entry}")
            selected = entry
            onSelect(entry)
        }
    }
}