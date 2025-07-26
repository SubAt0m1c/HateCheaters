package com.github.subat0m1c.hatecheaters.pvgui.v2

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.playerData
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.PageHandler.pageData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.MemberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo.Companion.dummyPlayer
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Colors
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text

abstract class PVPage(val name: String) {
    protected inline val player: PlayerInfo
        get() = playerData ?: dummyPlayer
    protected inline val profile: MemberData
        get() = player.profileOrSelected(profileName)?.members?.get(player.uuid) ?: MemberData(playerId = "")

    protected inline val ct get() = pageData.ct
    protected inline val totalHeight get() = pageData.totalHeight
    protected inline val spacer get() = pageData.spacer
    protected inline val mainWidth get() = pageData.mainWidth
    protected inline val mainHeight get() = pageData.mainHeight
    protected inline val mainX get() = pageData.mainStart

    protected val mainCenterX = pageData.mainStart + mainWidth / 2
    protected val quadrantWidth = (mainWidth / 2) - (spacer / 2)

    open fun draw(mouseX: Int, mouseY: Int) = Text.text(
        "This page is not yet implemented!",
        mainCenterX,
        totalHeight / 2,
        3f,
        Colors.WHITE,
        alignment = Text.Alignment.MIDDLE
    )

    open fun mouseClick(x: Int, y: Int, button: Int) {}
}