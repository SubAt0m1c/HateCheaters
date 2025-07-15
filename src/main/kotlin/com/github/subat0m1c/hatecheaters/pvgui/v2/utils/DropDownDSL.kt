package com.github.subat0m1c.hatecheaters.pvgui.v2.utils


import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*

fun <T> dropDownMenu(
    box: Box,
    ot: Number,
    default: T,
    options: List<T>,
    textScale: Number,
    color: Color,
    accent: Color,
    radius: Float = 0f,
    edgeSoftness: Float = 0f,
    dropDown: DropDownDSL<T>.() -> Unit
): DropDownDSL<T> = DropDownDSL(box, ot, default, options, textScale, color, accent, radius, edgeSoftness).apply(dropDown)

class DropDownDSL <T> (
    private val box: Box,
    private val outlineThickness: Number,
    private val default: T,
    private val options: List<T>,
    private val textScale: Number,
    private val color: Color,
    private val accent: Color,
    private val radius: Float = 0f,
    private val edgeSoftness: Float = 0f,
) {
    private var scaledBox: Box? = null

    private var onExtend: () -> Unit = {}
    private var onSelect: (T) -> Unit = {}
    private var displayText: (T) -> String = { it.toString() }
    private var selectedText: (T) -> String = displayText

    private var extended: Boolean = false
    private var selected = default
    private inline val trueOptions: List<T> get() = options.without(selected)

    val getSelected: T get() = selected

    infix fun scaledBox(box: Box) { scaledBox = box }

    fun onSelect(init: (T) -> Unit) { onSelect = init }

    fun onExtend(init: () -> Unit) { onExtend = init }

    fun displayText(init: (T) -> String) { displayText = init }

    fun selectedText(init: (T) -> String) { selectedText = init }

    fun draw() {
        Shaders.rect(box.x, box.y, box.w, box.h, radius, color)
        Text.text(
            selectedText(selected),
            (box.x + box.w / 2),
            (box.y + (box.h / 2)) - Text.textHeight(textScale) / 2,
            textScale.toFloat(),
            Colors.WHITE
        )
        trueOptions.takeIf { extended }?.forEachIndexed { i, entry ->
            val y = box.y + ((box.h+box.h/8) * (i+1)) + (box.h/8)
            Shaders.rect(box.x, y, box.w, box.h, radius, color)
            Text.text(
                displayText(entry),
                (box.x + box.w / 2),
                (y + (box.h / 2)) - Text.textHeight(textScale) / 2,
                textScale.toFloat(),
                Colors.WHITE
            )
        }
    }

    /**
     * @return true if clicked, false if it isn't.
     */
    fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val box = scaledBox ?: box
        if (isObjectHovered(box, mouseX, mouseY)) {
            extended = !extended
            onExtend()
            return true
        } else if (extended) {
            trueOptions.withIndex().find { (i, _) ->
                val y = box.y + ((box.h+(box.h/8)) * (i+1)) + (box.h/8)
                isObjectHovered(Box(box.x, y, box.w, box.h), mouseX, mouseY)
            }?.let { (_, entry) ->
                selected = entry
                onSelect(entry)
                extended = false
                return true
            }
        }
        extended = false
        return false
    }
}
