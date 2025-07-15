package com.github.subat0m1c.hatecheaters.utils.odinwrappers

import me.odinmain.utils.render.Color
import me.odinmain.utils.render.RenderUtils.bind
import me.odinmain.utils.render.Color as OdinColor
import me.odinmain.utils.render.Colors as OdinColors

/**
 * Enum wrapping odin's Colors.
 *
 * the odin value should not be used outside debugging as its the very thing intended to be wrapped.
 */
class Color(val odin: OdinColor) {
    val rgba get() = odin.rgba

    fun bind() {
        odin.bind()
    }

    constructor(
        r: Int, g: Int, b: Int, a: Float = 1f
    ) : this(OdinColor(r, g, b, a))

    constructor(
        hex: String
    ) : this(OdinColor(hex))
}

object Colors {
    val RED = Color(OdinColors.MINECRAFT_RED)
    val GREEN = Color(OdinColors.MINECRAFT_GREEN)
    val BLUE = Color(OdinColors.MINECRAFT_BLUE)
    val YELLOW = Color(OdinColors.MINECRAFT_YELLOW)
    val WHITE = Color(OdinColors.WHITE)
    val BLACK = Color(OdinColors.BLACK)
    val GRAY = Color(OdinColors.MINECRAFT_GRAY)
    val DARKGRAY = Color(OdinColors.MINECRAFT_DARK_GRAY)
    val ORANGE = Color(OdinColors.MINECRAFT_GOLD)
    val AQUA = Color(OdinColors.MINECRAFT_AQUA)
    val PURPLE = Color(OdinColors.MINECRAFT_DARK_PURPLE)
    val MAGENTA = Color(OdinColors.MINECRAFT_LIGHT_PURPLE)
    val DARKGREEN = Color(OdinColors.MINECRAFT_DARK_GREEN)
    val TRANSPARENT = Color(OdinColors.TRANSPARENT)
}

/**
 * this is so we can use odin's ColorSetting with our own wrapped class.
 */
fun OdinColor.hc(): Color {
    return Color(this)
}