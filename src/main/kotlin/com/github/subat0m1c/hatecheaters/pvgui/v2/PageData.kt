package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer

data class PageData(
    val totalWidth: Int = 1000,
    val totalHeight: Int = 560,
    val mainStart: Int = 216,
    val spacer: Int = 10,
    val offsetX: Int = -totalWidth / 2,
    val offsetY: Int = -totalHeight / 2,
    val mainWidth: Int = totalWidth - (mainStart + spacer),
    val mainHeight: Int = totalHeight - 2 * spacer,
) {
    inline val ct get() = ProfileViewer.currentTheme
}
