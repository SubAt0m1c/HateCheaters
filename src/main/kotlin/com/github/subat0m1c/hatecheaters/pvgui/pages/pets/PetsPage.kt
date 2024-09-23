package com.github.subat0m1c.hatecheaters.pvgui.pages.pets

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.petsList
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.petItem
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round
import kotlin.math.floor

object PetsPage: PVGuiPage() {
    override fun draw(screen: ScreenObjects, player: PlayerInfo) {
        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val lineY = floor(screen.mainHeight*0.1)
            roundedRectangle(screen.mainX, lineY, screen.mainWidth, screen.outlineThickness, line)
            val tH = getMCTextHeight()
            val activePet = it.pets.pets.find { it.active }
            val cataText = "§6Active Pet§7: §$c${activePet?.colorName} §7(§${c}${activePet?.petItem}§7)"
            val cataScale = 4f
            mcText(cataText, screen.mainCenterX-((cataText.mcWidth * cataScale)/2), lineY - screen.lineY - (tH*cataScale), cataScale, font, center = false)

            val pets = it.pets.pets.filter { it.type.lowercase().replace("_", " ").capitalizeWords() in petsList}
        }


        val text2 = "Add pets to the pets list to make them show up here. (/pv pets add or /hcpv pets add)"
        val centerY = screen.mainCenterY
        val fontScale = 2.5f
        //mcText(text, screen.mainCenterX-((text.mcWidth*fontScale)/2), centerY-((getMCTextHeight() *fontScale)), fontScale, font, center = false)
        mcText(text2, screen.mainCenterX-((text2.mcWidth*fontScale)/2), screen.mainY + screen.mainHeight - screen.lineY -((getMCTextHeight() *fontScale)/2), fontScale, font, center = false)
    }

}