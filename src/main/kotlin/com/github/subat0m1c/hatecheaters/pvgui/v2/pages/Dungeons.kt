package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.PVPage
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.TextBox
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.resettableLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorClass
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classLevel
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Box
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Colors
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Shaders
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc
import me.odinmain.utils.capitalizeFirst
import me.odinmain.utils.round

object Dungeons : PVPage("Dungeons") {
    private val mmComps: Float by resettableLazy { (profile.dungeons.dungeonTypes.mastermode.tierComps.without("total")).values.sum() }
    private val floorComps: Float by resettableLazy { (profile.dungeons.dungeonTypes.catacombs.tierComps.without("total")).values.sum() }

    private val text: List<String> by resettableLazy {
        listOf(
            "§bSecrets§7: ${profile.dungeons.secrets.commas.colorizeNumber(100000)}",
            "§dAverage Secret Count§7: ${
                (profile.dungeons.secrets.toDouble() / (mmComps + floorComps).toDouble()).round(
                    2
                ).colorize(15.0)
            }",
            "§cBlood Mob Kills§7: ${profile.playerStats.bloodMobKills.commas}",
            "§7Spirit Pet: ${if (profile.pets.pets.any { it.type == "SPIRIT" && it.tier == "LEGENDARY" }) "§l§2Found!" else "§l§4Missing!"}",
        )
    }

    private val cataText by resettableLazy {
        "§4Cata Level§7: §${ct.fontCode}${profile.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}"
    }

    private val classTextList: List<String> by resettableLazy {
        profile.dungeons.classes.entries.map {
            "${it.key.capitalizeFirst().colorClass}§7: ${it.value.classLevel.round(2).colorize(50)}"
        }
    }
    private val classAverageText: String by resettableLazy {
        "§6Class Average§7: ${
            profile.dungeons.classAverage.round(2).colorize(50)
        }"
    }

    private val selectedClass: String by resettableLazy { "§aSelected Class§7: §${ct.fontCode}${profile.dungeons.selectedClass?.capitalizeFirst()?.colorClass}" }
    private val classEntryText: List<String> by resettableLazy { listOf(selectedClass) + classTextList }

    private val floorData: List<String> by resettableLazy {
        (0..7).map {
            "§3${profile.dungeons.dungeonTypes.catacombs.floorStats(it.toString())}"
        }
    }
    private val mmData: List<String> by resettableLazy {
        (1..7).map {
            "§cMM ${profile.dungeons.dungeonTypes.mastermode.floorStats(it.toString())}"
        }
    }

    private val backgroundHeight = (mainHeight / 2) - (spacer / 2)

    private val mainBox by resettableLazy {
        TextBox(
            Box(mainX + spacer, 2 * spacer, quadrantWidth - 2 * spacer, backgroundHeight - 2 * spacer),
            cataText, 4f, text, 2.5f, spacer.toFloat(), Colors.WHITE
        )
    }

    private val classBox by resettableLazy {
        TextBox(
            Box(
                mainX + spacer,
                3 * spacer + backgroundHeight,
                quadrantWidth - 2 * spacer,
                backgroundHeight - 2 * spacer
            ),
            classAverageText, 3.5f, classEntryText, 2.5f, spacer.toFloat(), Colors.WHITE
        )
    }

    private val floorBox by resettableLazy {
        TextBox(
            Box(
                mainX + 2 * spacer + quadrantWidth,
                2 * spacer,
                quadrantWidth - 2 * spacer,
                backgroundHeight - 2 * spacer
            ),
            null, 0f, floorData, 2.5f, spacer.toFloat(), Colors.WHITE
        )
    }

    private val mmBox by resettableLazy {
        TextBox(
            Box(
                mainX + 2 * spacer + quadrantWidth,
                3 * spacer + backgroundHeight,
                quadrantWidth - 2 * spacer,
                backgroundHeight - 2 * spacer
            ),
            null, 0f, mmData, 2.3f, spacer.toFloat(), Colors.WHITE
        )
    }

    override fun draw(mouseX: Int, mouseY: Int) {
        Shaders.rect(mainX, spacer, quadrantWidth, backgroundHeight, ct.roundness, ct.items.hc())
        Shaders.rect(
            mainX + quadrantWidth + spacer,
            spacer + backgroundHeight + spacer,
            quadrantWidth,
            backgroundHeight,
            ct.roundness,
            ct.items.hc()
        )
        Shaders.rect(
            mainX,
            spacer + backgroundHeight + spacer,
            quadrantWidth,
            backgroundHeight,
            ct.roundness,
            ct.items.hc()
        )
        Shaders.rect(
            mainX + quadrantWidth + spacer,
            spacer,
            quadrantWidth,
            backgroundHeight,
            ct.roundness,
            ct.items.hc()
        )

        mainBox.draw()
        classBox.draw()
        floorBox.draw()
        mmBox.draw()
    }

    private fun HypixelData.DungeonTypeData.floorStats(floor: String): String =
        "${if (floor == "0") "Entrance" else "Floor $floor"}: §${ct.fontCode}${this.tierComps[floor]?.toLong()?.commas ?: "§cDNF"} " +
                "§7| §${ct.fontCode}${this.fastestTimes[floor]?.let { secondsToMinutes(it * 0.001) } ?: "§cDNF"} " +
                "§7| §${ct.fontCode}${this.fastestTimeS[floor]?.let { secondsToMinutes(it * 0.001) } ?: "§cDNF"} " +
                "§7| §a${this.fastestTimeSPlus[floor]?.let { secondsToMinutes(it * 0.001) } ?: "§cDNF"}"
}