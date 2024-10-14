package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillCap
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillColor
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSkillLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSlayerCap
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSlayerColor
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.getSlayerSkillLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.truncate
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round

object Profile: Pages.PVPage("Profile") {
    private val lineWidth = mainCenterX - mainX - lineY
    private val mainLineY = mainHeight*0.1

    private val skillAverage: String by profileLazy { "§6Skill Average§7: §${ct.fontCode}${profile.playerData.cappedSkillAverage.round(2).colorize(55)} §7(${profile.playerData.skillAverage.round(2)})" }
    private val skillText: List<Pair<String, String>> by profileLazy {
        profile.playerData.experience.without("SKILL_DUNGEONEERING").entries.sortedByDescending { it.value }.map {
            val level = getSkillLevel(it.value).round(2).toDouble()
            val skill = it.key.lowercase().substringAfter("skill_")
            val cap = getSkillCap(skill).toDouble()
            skill to "${skill.capitalizeWords()}§7: ${level.coerceAtMost(cap).colorize(cap)} §7(${level})"
        }
    }

    private val skillEntryHeight: Double by profileLazy { (mainHeight- mainLineY-lineY-ot) / skillText.size }

    private val slayerText: List<Pair<String, String>> by profileLazy {
        profile.slayer.bosses.entries.sortedByDescending { it.value.xp }.map {
            val level = getSlayerSkillLevel(it.value.xp.toDouble(), it.key).round(2)
            val slayer = it.key.capitalizeWords()
            it.key to "${slayer}§7: ${level.colorize(getSlayerCap(it.key))} §7(${it.value.xp.toDouble().truncate})"
        }
    }

    private val slayerEntryHeight: Double by profileLazy { mainHeight.toDouble()/slayerText.size }

    override fun draw() {
        roundedRectangle(mainCenterX, lineY, ot, mainHeight, ct.line)
        centeredText(skillAverage, mainCenterX - mainWidth/4-lineY/3, lineY + mainLineY /2, 2.7, ct.font)

        roundedRectangle(mainX, mainLineY, lineWidth, ot, ct.line)

        skillText.forEachIndexed { i, text ->
            val y = (mainLineY + lineY + ot) + (skillEntryHeight*i) + skillEntryHeight/2 - getMCTextHeight()*2.5/2
            mcText(text.second, mainX, y, 2.5, getSkillColor(text.first), shadow = true, center = false)
        }

        slayerText.forEachIndexed { i, text ->
            val y = lineY + slayerEntryHeight*i + slayerEntryHeight/2 - getMCTextHeight()*2.5/2
            mcText(text.second, mainCenterX + lineY + ot, y, 2.5, getSlayerColor(text.first), shadow = true, center = false)
        }
    }
}