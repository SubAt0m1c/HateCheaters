package com.github.subat0m1c.hatecheaters.pvgui.v2.utils


import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.isObjectHovered
import me.odinmain.utils.div
import me.odinmain.utils.minus
import me.odinmain.utils.plus
import me.odinmain.utils.render.*
import me.odinmain.utils.times

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
    val with = this

    private var scaledBox: Box? = null

    private var onExtend: () -> Unit = {}
    private var onSelect: (T) -> Unit = {}

    private var extended: Boolean = false
    private var selected = default
    private inline val trueOptions: List<T> get() = options.without(selected)

    val getSelected: T get() = selected

    infix fun scaledBox(box: Box) {
        scaledBox = box
    }

    fun onSelect(init: (T) -> Unit) {
        onSelect = init
    }

    fun onExtend(init: () -> Unit) {
        onExtend = init
    }

    fun draw() {
        roundedRectangle(box.x, box.y, box.w, box.h, color, radius, edgeSoftness)
        mcText(selected.toString(), (box.x + box.w/2), (box.y + (box.h/2))-(getMCTextHeight() *textScale)/2, textScale.toFloat(), Color.WHITE)
        if (extended) {
            trueOptions.forEachIndexed { i, entry ->
                val y = box.y + ((box.h+box.h/8) * (i+1)) + (box.h/8)
                roundedRectangle(box.x, y, box.w, box.h, color, radius, edgeSoftness)
                mcText(entry.toString(), (box.x + box.w/2), (y + (box.h/2))-(getMCTextHeight() *textScale)/2, textScale.toFloat(), Color.WHITE)
            }
        }
    }

    fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val box = scaledBox ?: box
        if (isObjectHovered(box, mouseX, mouseY)) {
            extended = !extended
            onExtend()
            return true
        } else if (extended) {
            trueOptions.filterIndexed { i, entry ->
                val y = box.y + ((box.h+(box.h/8)) * (i+1)) + (box.h/8)
                isObjectHovered(Box(box.x, y, box.w, box.h), mouseX, mouseY)
            }.firstOrNull()?.let {
                selected = it
                onSelect(it)
                extended = false
                return true
            }
        }
        extended = false
        return false
    }
}

fun <T> List<T>.without(vararg items: T): List<T> = this.filter { !items.contains(it) }
fun <K, V> Map<K, V>.without(vararg items: K): Map<K, V> = this.filter { !items.contains(it.key) }
