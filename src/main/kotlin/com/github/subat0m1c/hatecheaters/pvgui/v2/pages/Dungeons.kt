package com.github.subat0m1c.hatecheaters.pvgui.v2.pages

import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages
import com.github.subat0m1c.hatecheaters.pvgui.v2.Pages.centeredText
import com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Dungeons.ct
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.profileLazy
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorClass
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classLevel
import me.odinmain.utils.capitalizeFirst
import me.odinmain.utils.render.getMCTextHeight
import me.odinmain.utils.render.mcText
import me.odinmain.utils.render.roundedRectangle
import me.odinmain.utils.round

object Dungeons: Pages.PVPage("Dungeons") {
    private val cataLineWidth = mainCenterX - mainX - lineY
    private val cataCenter = mainCenterX - mainWidth / 4
    private val cataLineY = mainHeight * 0.1

    private val mmComps: Int by profileLazy { (profile.dungeons.dungeonTypes.mastermode.tierComps.without("total")).values.sum() }
    private val floorComps: Int by profileLazy { (profile.dungeons.dungeonTypes.catacombs.tierComps.without("total")).values.sum() }

    private val text: List<String> by profileLazy {
        listOf(
            "§bSecrets§7: ${profile.dungeons.secrets.commas.colorizeNumber(100000)}",
            "§dAverage Secret Count§7: ${(profile.dungeons.secrets.toDouble()/(mmComps + floorComps)).round(2).colorize(15.0)}",
            "§cBlood Mob Kills§7: ${((profile.playerStats.kills["watcher_summon_undead"] ?: 0) + (profile.playerStats.kills["master_watcher_summon_undead"] ?: 0)).commas}",
            "§7Spirit Pet: ${if (profile.pets.pets.any { it.type == "SPIRIT" && it.tier == "LEGENDARY" }) "§l§2Found!" else "§o§4Missing!"}",
        )
    }

    private val textEntryHeight: Double by profileLazy { (mainHeight/2 - (cataLineY + lineY)) / text.size }

    private val cataText by profileLazy { "§4Cata Level§7: §${ct.fontCode}${profile.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}" }

    private val classTextList: List<String> by profileLazy { profile.dungeons.classes.entries.map { "${it.key.capitalizeFirst().colorClass}§7: ${it.value.classLevel.round(2).colorize(50)}" } }
    private val classAverageText: String by profileLazy { "§6Class Average§7: ${profile.dungeons.classAverage.round(2).colorize(50)}" }
    private val selectedClass: String by profileLazy { "§aSelected Class§7: §${ct.fontCode}${profile.dungeons.selectedClass?.capitalizeFirst()?.colorClass}" }

    private val classEntryText: List<String> by profileLazy { listOf(selectedClass) + classTextList }
    private val classEntrySize: Double by profileLazy { (mainHeight/2 - cataLineY + lineY*2)/classEntryText.size }

    private val floorData: List<String> by profileLazy { (0..7).map { "§3${profile.dungeons.dungeonTypes.catacombs.floorStats(it.toString())}" } }
    private val mmData: List<String> by profileLazy { (1..7).map { "§cMM ${profile.dungeons.dungeonTypes.mastermode.floorStats(it.toString())}" } }

    private val floorHeight: Int by profileLazy { (mainHeight/2 - lineY) / floorData.size }
    private val mmHeight: Int by profileLazy { (mainHeight/2 - lineY) / mmData.size }

    override fun draw() {
        roundedRectangle(mainCenterX, lineY, ot, mainHeight, ct.line)
        centeredText(cataText, cataCenter, lineY + cataLineY/2, 4, ct.font)

        text.forEachIndexed { i, text ->
            val y = ((cataLineY + lineY) + textEntryHeight*i + textEntryHeight/2) - (getMCTextHeight()*2.5)/2
            mcText(text, mainX, y, 2.5, ct.font, shadow = true, center = false)
        }

        centeredText(classAverageText, cataCenter, mainHeight/2 + cataLineY/2, 3.5, ct.font)
        roundedRectangle(mainX, mainHeight/2 - lineY + cataLineY, cataLineWidth, ot, ct.line)

        classEntryText.forEachIndexed { i, text ->
            val y = mainHeight/2 + cataLineY - lineY + classEntrySize*i + classEntrySize/2
            if (i == 0) centeredText(text, cataCenter, y, 2.5, ct.font)
            else mcText(text, mainX, y-(getMCTextHeight()*2.5)/2, 2.5, ct.font, shadow = true, center = false)
        }

        roundedRectangle(mainX, cataLineY, cataLineWidth, ot, ct.line)

        floorData.forEachIndexed { i, text ->
            val y = lineY + (floorHeight*i) + mmHeight/2 - getMCTextHeight()
            mcText(text, mainCenterX + ot + lineY, y, 2, ct.font, shadow = true, center = false)
        }

        mmData.forEachIndexed { i, text ->
            val y = lineY + mainHeight/2 + lineY + mmHeight*i + mmHeight/2 - getMCTextHeight()
            mcText(text, mainCenterX + ot + lineY, y, 2, ct.font, shadow = true, center = false)
        }

        roundedRectangle(mainCenterX + ot + lineY, lineY + mainHeight/2, cataLineWidth, ot, ct.line)
    }
}

fun HypixelData.DungeonTypeData.floorStats(floor: String): String =
    "${if (floor == "0") "Entrance" else "Floor $floor"}: §${ct.fontCode}${this.tierComps[floor]?.commas ?: "§cDNF"} " +
        "§7| §${ct.fontCode}${this.fastestTimes[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"} " +
        "§7| §${ct.fontCode}${this.fastestTimeS[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"} " +
        "§7| §a${this.fastestTimeSPlus[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"}"