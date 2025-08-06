package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.profileName
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.TextBox
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.resettableLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.truncate
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cappedSkillAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillCap
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillColorCode
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSkillLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerCap
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerColorCode
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.getSlayerSkillLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.skillAverage
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import me.odinmain.utils.round

object Profile : PVPage("Profile") {
    private val skillAverage: String by resettableLazy {
        "§6Skill Average§7: §${ct.fontCode}${
            profile.playerData.cappedSkillAverage.round(2).colorize(55)
        } §7(${profile.playerData.skillAverage.round(2)})"
    }

    private val skillText: List<String> by resettableLazy {
        profile.playerData.experience.without("SKILL_DUNGEONEERING").entries.sortedByDescending { it.value }.map {
            val skill = it.key.lowercase().substringAfter("skill_")
            val level = getSkillLevel(skill, it.value).round(2).toDouble()
            val cap = getSkillCap(skill).toDouble()
            "§${getSkillColorCode(skill)}${skill.capitalizeWords()}§7: ${
                level.coerceAtMost(cap).colorize(cap)
            } §7(${level})"
        }
    }

    private val otherText: List<String> by resettableLazy {
        listOf(
            "§6Purse§7: §r${profile.currencies.coins.truncate(3)}",
            "§6Bank§7: §r${player.profileOrSelected(profileName)?.banking?.balance?.truncate(3) ?: 0}${if (player.profileData.profiles.size.let { it > 1 } == true) " | ${profile.profile.bankAccount.truncate}" else ""}",
            "§6Gold Collection§7: §r${profile.collection["GOLD_INGOT"]?.let { "${it.commas.colorizeNumber(100000000)} §8(${it.toString().length})" }}"
        )
    }

    private val slayerText: List<String> by resettableLazy {
        profile.slayer.bosses.entries.sortedByDescending { it.value.xp }.map {
            val level = getSlayerSkillLevel(it.value.xp.toDouble(), it.key).round(2)
            "§${getSlayerColorCode(it.key)}${it.key.capitalizeWords()}§7: ${level.colorize(getSlayerCap(it.key))} §7(${it.value.xp.toDouble().truncate})"
        }
    }

    private val skillBox by resettableLazy {
        TextBox(
            Box(mainX + spacer, 2 * spacer, quadrantWidth, mainHeight - spacer * 2),
            skillAverage, 2.7f, skillText, 2.5f, spacer.toFloat(), ct.font.hc(),
        )
    }

    private val slayerBox by resettableLazy {
        TextBox(
            Box(
                mainX + 2 * spacer + quadrantWidth,
                2 * spacer,
                quadrantWidth,
                (mainHeight / 2) - (spacer / 2) - 2 * spacer
            ),
            null, 0f, slayerText, 2.5f, spacer.toFloat(), ct.font.hc()
        )
    }

    private val purseBox by resettableLazy {
        TextBox(
            Box(
                mainX + 2 * spacer + quadrantWidth,
                3 * spacer + (mainHeight / 2) - (spacer / 2),
                quadrantWidth,
                (mainHeight / 2) - (spacer / 2) - 2 * spacer
            ),
            null, 0f, otherText, 2.5f, spacer.toFloat(), ct.font.hc()
        )
    }

    override fun draw(mouseX: Int, mouseY: Int) {
        Shaders.rect(mainX, spacer, quadrantWidth, mainHeight, ct.roundness, ct.items.hc())
        Shaders.rect(
            mainX + spacer + quadrantWidth,
            spacer,
            quadrantWidth,
            (mainHeight / 2) - (spacer / 2),
            ct.roundness,
            ct.items.hc()
        )
        Shaders.rect(
            mainX + spacer + quadrantWidth,
            2 * spacer + (mainHeight / 2) - (spacer / 2),
            quadrantWidth,
            (mainHeight / 2) - (spacer / 2),
            ct.roundness,
            ct.items.hc()
        )
        skillBox.draw()
        slayerBox.draw()
        purseBox.draw()
    }
}