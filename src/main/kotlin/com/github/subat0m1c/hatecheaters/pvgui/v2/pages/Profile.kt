package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.truncate
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillCap
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillColor
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerCap
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerColor
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerSkillLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import me.odinmain.utils.round

object Profile: Pages.PVPage("Profile") {
    private val lineWidth = mainCenterX - mainX - lineY
    private val mainLineY = mainHeight * 0.1

    private val skillAverage: String by profileLazy { "§6Skill Average§7: §${ct.fontCode}${profile.playerData.cappedSkillAverage.round(2).colorize(55)} §7(${profile.playerData.skillAverage.round(2)})" }
    private val skillText: List<Pair<String, String>> by profileLazy {
        profile.playerData.experience.without("SKILL_DUNGEONEERING").entries.sortedByDescending { it.value }.map {
            val level = getSkillLevel(it.value).round(2).toDouble()
            val skill = it.key.lowercase().substringAfter("skill_")
            val cap = getSkillCap(skill).toDouble()
            skill to "${skill.capitalizeWords()}§7: ${level.coerceAtMost(cap).colorize(cap)} §7(${level})"
        }
    }

    private val otherText: List<String> by profileLazy {
        listOf(
            "§6Purse§7: §r${profile.currencies.coins.truncate(3)}",
            "§6Bank§7: §r${player.profileOrSelected(profileName)?.banking?.balance?.truncate(3) ?: 0}${if (player.profileData.profiles.size?.let { it > 1 } == true) " | ${profile.profile.bankAccount.truncate}" else ""}",
            "§6Gold Collection§7: §r${profile.collection["GOLD_INGOT"]?.let { "${it.commas.colorizeNumber(100000000)} §8(${it.toString().length})" }}"
        )
    }

    private val skillEntryHeight: Double by profileLazy { (mainHeight- mainLineY-lineY-ot) / skillText.size }

    private val slayerText: List<Pair<String, String>> by profileLazy {
        profile.slayer.bosses.entries.sortedByDescending { it.value.xp }.map {
            val level = getSlayerSkillLevel(it.value.xp.toDouble(), it.key).round(2)
            it.key to "${it.key.capitalizeWords()}§7: ${level.colorize(getSlayerCap(it.key))} §7(${it.value.xp.toDouble().truncate})"
        }
    }

    private val middleY: Double = mainHeight / 2.0
    private val halfHeight: Double = mainHeight - middleY - lineY

    private val slayerEntryHeight: Double by profileLazy { halfHeight / slayerText.size }
    private val otherEntryHeight: Double by profileLazy { halfHeight / otherText.size }

    override fun draw() {
        Shaders.rect(mainCenterX, lineY, ot, mainHeight, color = ct.line.hc())
        centeredText(skillAverage, mainCenterX - mainWidth / 4 - lineY / 3, lineY + mainLineY / 2, 2.7f, ct.font.hc())

        Shaders.rect(mainX, mainLineY, lineWidth, ot, color = ct.line.hc())
        Shaders.rect(lineY + mainCenterX, lineY * 2 + halfHeight, lineWidth, ot, color = ct.line.hc())

        skillText.forEachIndexed { i, text ->
            val y = (mainLineY + lineY + ot) + (skillEntryHeight * i) + skillEntryHeight / 2 - Text.textHeight(2.5) / 2
            Text.text(text.second, mainX, y, 2.5f, getSkillColor(text.first), center = false)
        }

        slayerText.forEachIndexed { i, text ->
            val y = lineY + slayerEntryHeight * i + slayerEntryHeight / 2 - Text.textHeight(2.5) / 2
            Text.text(text.second, mainCenterX + lineY + ot, y, 2.5f, getSlayerColor(text.first), center = false)
        }

        otherText.forEachIndexed { i, text ->
            val y = middleY + lineY + otherEntryHeight * i + otherEntryHeight / 2 - Text.textHeight(2.5) / 2
            Text.text(text, mainCenterX + lineY + ot, y, 2.5f, ct.font.hc(), center = false)
        }
    }
}