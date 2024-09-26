package com.github.subat0m1c.hatecheaters.pvgui.pages.profile

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.somethingWentWrong
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillCap
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillColor
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSlayerCap
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSlayerSkillLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.truncate
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round
import kotlin.math.floor

object ProfilePage: PVGuiPage() {
    override fun draw(screen: ScreenObjects, player: PlayerInfo) {
        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val lineX = screen.mainCenterX-(screen.outlineThickness/2)
            val cataLineWidth = lineX - screen.mainX - screen.lineY
            val lineY = floor(screen.mainHeight*0.1)
            roundedRectangle(screen.mainX, lineY, cataLineWidth, screen.outlineThickness, line)
            val tH = getMCTextHeight()
            val cataText = "§6Skill Average§7: §$c${it.playerData.cappedSkillAverage.round(2).colorize(55)} §7(${it.playerData.skillAverage.round(2)})"
            val textScale = 4f * screen.scale
            val centerLeft = lineX+(screen.outlineThickness/2) - (screen.mainWidth/4)
            mcText(cataText, centerLeft-((cataText.mcWidth * textScale)/2), lineY - screen.lineY - (tH*textScale), textScale, font, center = false)

            val usableY = lineY + screen.outlineThickness + screen.lineY

            roundedRectangle(screen.mainCenterX, screen.lineY, screen.outlineThickness, screen.mainHeight, line)
            val skillX = screen.mainCenterX + screen.outlineThickness + screen.lineY

            val textList = it.playerData.experience.entries.sortedByDescending { it.value }.filter { it.key != "SKILL_DUNGEONEERING" }.map {
                val level = getSkillLevel(it.value).round(2).toDouble()
                val skill = it.key.lowercase().substringAfter("skill_")
                val cap = getSkillCap(skill).toDouble()
                skill to "${skill.capitalizeWords()}§7: ${level.coerceAtMost(cap).colorize(cap)} §7(${level})"
            }
            val entryHeight = (screen.mainHeight-usableY + screen.lineY)/textList.size

            textList.forEachIndexed { i, text ->
                mcText(text.second, screen.mainX, (usableY + (entryHeight * i) + entryHeight/2) - ((getMCTextHeight() * textScale)/2), textScale, getSkillColor(text.first), center = false)
            }

            val rightList = it.slayer.bosses.entries.sortedByDescending { it.value.xp }.map {
                val level = getSlayerSkillLevel(it.value.xp.toDouble(), it.key).round(2)
                val slayer = it.key.capitalizeWords()
                it.key to "${slayer}§7: ${level.colorize(getSlayerCap(it.key))} §7(${it.value.xp.toDouble().truncate})"
            }

            val rightHeight = (screen.mainHeight-usableY + screen.lineY)/rightList.size
            rightList.forEachIndexed { i, text ->
                mcText(text.second, skillX, (usableY + (rightHeight * i) + rightHeight/2) - ((getMCTextHeight()*textScale)/2), textScale, font, center = false)
            }

            return
        }

        somethingWentWrong(screen)
    }
}