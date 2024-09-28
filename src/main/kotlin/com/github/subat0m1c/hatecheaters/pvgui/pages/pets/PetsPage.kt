package com.github.subat0m1c.hatecheaters.pvgui.pages.pets

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.petsList
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.scale
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.somethingWentWrong
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round
import kotlin.math.ceil
import kotlin.math.floor

object PetsPage: PVGuiPage() {
    override fun draw(screen: ScreenObjects, player: PlayerInfo) {
        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val lineY = floor(screen.mainHeight*0.1)
            roundedRectangle(screen.mainX, lineY, screen.mainWidth, screen.outlineThickness, line)
            val tH = getMCTextHeight()
            val activePet = it.pets.pets.find { it.active }
            val cataText = "§6Active Pet§7: §$c${activePet?.colorName ?: "None!"} ${activePet?.petItem?.let { "§7(§${c}${it}§7)" } ?: ""}"
            val cataScale = 4f * screen.scale
            mcText(cataText, screen.mainCenterX-((cataText.mcWidth * cataScale)/2), lineY - screen.lineY - (tH*cataScale), cataScale, font, center = false)

            val pets = it.pets.pets.filter { it.type.lowercase().replace("_", " ").capitalizeWords() in petsList}.map { "${it.colorName} ${it.petItem?.let { "§7(§${c}${it}§7)" } ?: ""}" }

            val fontScale = 2.5f * screen.scale

            val centerX = screen.mainCenterX

            val usableY = lineY + screen.lineY
            val usableHeight = screen.mainHeight - (lineY + screen.lineY) - (screen.mainHeight - (screen.mainY + screen.mainHeight - screen.lineY -((getMCTextHeight() * 2.5f)/2))) - screen.lineY

            //roundedRectangle(screen.mainX, usableY, screen.mainWidth, usableHeight, line)

            val entryHeight = usableHeight/ceil((pets.size.toDouble()+1)/3)
            //roundedRectangle(screen.mainX, usableY, screen.mainWidth, entryHeight, line)

            pets.forEachIndexed { i, pet ->
                val x = when (i % 3) {
                    0 -> centerX - screen.mainWidth/4
                    1 -> centerX + screen.mainWidth/4
                    2 -> centerX
                    else -> centerX
                }
                val y = if (i % 3 == 2) entryHeight/2 else 0
                mcText(pet, x, (usableY + y.toDouble() + (entryHeight * floor(i.toDouble()/3)) + entryHeight/2) - ((getMCTextHeight()*fontScale)/2), fontScale, font)
            }

            val text2 = "Add pets to the pets list to make them show up here. (/pv pets add or /hcpv pets add)"
            //mcText(text, screen.mainCenterX-((text.mcWidth*fontScale)/2), centerY-((getMCTextHeight() *fontScale)), fontScale, font, center = false)
            mcText(text2, screen.mainCenterX, screen.mainY + screen.mainHeight - screen.lineY - ((getMCTextHeight() * 2.5f * screen.scale)/2), 2.5 * screen.scale, font, center = true)
            return
        }
        somethingWentWrong(screen)
    }

    override fun mouseClick(x: Int, y: Int, button: Int) {
        super.mouseClick(x, y, button)
    }

}