package com.github.subat0m1c.hatecheaters.pvgui.v2.utils


import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.animations.Hover
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.*
import me.odinmain.utils.ui.animations.EaseInOutAnimation
import kotlin.math.floor

fun <T> dropDownMenu(
    box: Box,
    default: T,
    options: List<T>,
    spacer: Number,
    color: Color,
    radius: Float = 0f,
    extended: Boolean = false,
    dropDown: DropDownDSL<T>.() -> Unit
): DropDownDSL<T> = DropDownDSL(box, default, options, spacer, color, radius, extended).apply(dropDown)

class DropDownDSL <T> (
    private val box: Box,
    private val default: T,
    private val options: List<T>,
    private val spacer: Number,
    private val color: Color,
    private val radius: Float = 0f,
    var extended: Boolean = false
) {
    private val mainHover = Hover(250)
    private val dropAnim = EaseInOutAnimation(250)

    private val itemAnims = List(options.size) { Hover(250) }

    private var onExtend: () -> Unit = {}
    private var onSelect: (T) -> Unit = {}
    private var displayText: (T) -> String = { it.toString() }
    private var selectedText: (T) -> String = displayText

    private var selected = default
    private inline val trueOptions: List<T> get() = options.without(selected)

    val getSelected: T get() = selected

    fun onSelect(init: (T) -> Unit) { onSelect = init }
    fun onExtend(init: () -> Unit) { onExtend = init }

    fun displayText(init: (T) -> String) { displayText = init }
    fun selectedText(init: (T) -> String) { selectedText = init }

    fun draw(mouseX: Int, mouseY: Int) {
        mainHover.handle(isObjectHovered(box, mouseX, mouseY))

        Shaders.rect(box, radius, color.brighter(1 + mainHover.percent() / 500f))

        Text.fillText(
            selectedText(selected),
            (box.x + box.w / 2),
            (box.y + box.h / 2),
            box.w - 2 * spacer.toFloat(),
            box.h - 2 * spacer.toFloat(),
            Colors.WHITE
        )

        val totalHeight =
            box.h + floor(dropAnim.get(0f, (box.h + spacer.toFloat()) * (trueOptions.size + 1), !extended))

        if (!dropAnim.isAnimating() && !extended) return
        Shaders.scissor(box.x, box.y, box.w, totalHeight)
        trueOptions.forEachIndexed { i, entry ->
            val y = box.y + (box.h + spacer.toFloat()) * (i + 1)

            itemAnims[i].handle(isObjectHovered(Box(box.x, y, box.w, box.h), mouseX, mouseY))

            Shaders.rect(box.x, y, box.w, box.h, radius, color.brighter(1 + itemAnims[i].percent() / 500f))
            Text.fillText(
                displayText(entry),
                (box.x + box.w / 2),
                (y + box.h / 2),
                box.w - 2 * spacer.toFloat(),
                box.h - 2 * spacer.toFloat(),
                Colors.WHITE
            )
        }
        Shaders.popScissor()
    }

    /**
     * @return true if clicked, false if it isn't.
     */
    fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (isObjectHovered(box, mouseX, mouseY)) {
            dropAnim.start()
            extended = !extended
            onExtend()
            return true
        } else if (extended) {
            trueOptions.withIndex().find { (i, _) ->
                val y = box.y + ((box.h+(box.h/8)) * (i+1)) + (box.h/8)
                isObjectHovered(Box(box.x, y, box.w, box.h), mouseX, mouseY)
            }?.let { (_, entry) ->
                dropAnim.start()
                selected = entry
                onSelect(entry)
                extended = false
                return true
            }
        }
        if (extended) {
            dropAnim.start()
            extended = false
        }
        return false
    }
}
