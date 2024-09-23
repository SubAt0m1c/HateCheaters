package com.github.subat0m1c.hatecheaters.pvgui.pages

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.mouseX
import com.github.subat0m1c.hatecheaters.pvgui.mouseY
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle

object TestPage : PVGuiPage() {

    override fun draw(screen: ScreenObjects, player: HypixelProfileData.PlayerInfo) {
        val text = "This page is for testing!"
        val text2 = "This test is for mouse positioning! YES ITS CHOPPY I KNOW"
        val centerY = screen.mainCenterY
        val fontScale = 3f
        mcText(text, screen.mainCenterX-((text.mcWidth*fontScale)/2), centerY-((getMCTextHeight() *fontScale)), fontScale, font, center = false)
        mcText(text2, screen.mainCenterX-((text2.mcWidth*fontScale)/2), centerY+((getMCTextHeight() *fontScale)), fontScale, font, center = false)

        val mouseX = screen.mouseX
        val mouseY = screen.mouseY

        roundedRectangle(mouseX - 20, mouseY - 20, 40, 40, line)
    }
}