package com.github.subat0m1c.hatecheaters.pvgui.pages.dungeons

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.line
import com.github.subat0m1c.hatecheaters.pvgui.PVGuiPage
import com.github.subat0m1c.hatecheaters.pvgui.ScreenObjects
import com.github.subat0m1c.hatecheaters.pvgui.pvutils.RenderUtils.somethingWentWrong
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.classLevel
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorClass
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorizeNumber
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.commas
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.mcWidth
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerInfo
import me.odinmain.utils.capitalizeFirst
import me.odinmain.utils.render.*
import me.odinmain.utils.round
import kotlin.math.floor

object DungeonsPage: PVGuiPage() {
    override fun draw(screen: ScreenObjects, player: PlayerInfo) {
        //roundedRectangle(screen.mainX, screen.mainY, screen.mainWidth, screen.mainHeight, Color.RED, 0f, 0f)
        player.profileData.profiles.find { it.selected }?.members?.get(player.uuid)?.let {
            val lineX = screen.mainCenterX-(screen.outlineThickness/2)
            drawCata(screen, it, lineX)
            drawFloors(screen, it, lineX)
            roundedRectangle(lineX, screen.lineY, screen.outlineThickness, screen.mainHeight, line)
            return
        }
        somethingWentWrong(screen)
    }

    private fun drawCata(screen: ScreenObjects, player: HypixelProfileData.MemberData, lineX: Double) {
        val cataLineWidth = lineX - screen.mainX - screen.lineY
        val lineY = floor(screen.mainHeight*0.1)
        roundedRectangle(screen.mainX, lineY, cataLineWidth, screen.outlineThickness, line)
        val tH = getMCTextHeight()
        val cataText = "§4Cata Level§7: §$c${player.dungeons.dungeonTypes.cataLevel.round(2).colorize(50)}"
        val cataScale = 4f
        val centerLeft = lineX+(screen.outlineThickness/2) - (screen.mainWidth/4)
        mcText(cataText, centerLeft-((cataText.mcWidth * cataScale)/2), lineY - screen.lineY - (tH*cataScale), cataScale, font, center = false)
        val profileKills = player.playerStats.kills
        val mmComps = (player.dungeons.dungeonTypes.mastermode.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
        val floorComps = (player.dungeons.dungeonTypes.catacombs.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
        val renderStrings: MutableList<String> = mutableListOf()
        renderStrings.add("§bSecrets§7: ${player.dungeons.secrets.commas.colorizeNumber(100000)}")
        renderStrings.add("§dAverage Secret Count§7: ${(player.dungeons.secrets.toDouble()/(mmComps + floorComps)).round(2).colorize(15.0)}")
        renderStrings.add("§cBlood Mob Kills§7: ${((profileKills["watcher_summon_undead"] ?: 0) + (profileKills["master_watcher_summon_undead"] ?: 0)).commas}")
        renderStrings.add("§7Spirit Pet: ${if (player.pets.pets.any { it.type == "SPIRIT" && it.tier == "LEGENDARY" }) "§l§2Found!" else "§o§4Missing!"}")

        val scale = 3.5f
        val y = lineY + screen.lineY*2
        renderStrings.forEachIndexed { i, it ->
            mcText(it, screen.mainX, y + ((tH*scale)*i*1.3), scale, font, center = false)
        }

        val classY = y + ((tH*scale)*(renderStrings.size+1)*1.5)
        val classavgText = "§6Class Average§7: ${player.dungeons.classAverage.round(2).colorize(50)}"
        mcText(classavgText, centerLeft-((classavgText.mcWidth*4f)/2), classY-((tH*4f)/2)-screen.lineY*2, 4f, font, center = false)
        roundedRectangle(screen.mainX, classY, cataLineWidth, screen.outlineThickness, line)

        val clazzText = "§aSelected Class§7: §$c${player.dungeons.selectedClass?.capitalizeFirst()?.colorClass}"
        val baseY = classY + screen.outlineThickness + screen.lineY
        mcText(clazzText, centerLeft-((clazzText.mcWidth*scale)/2), baseY, scale, font, center = false)
        player.dungeons.classes.entries.forEachIndexed { i, (clazz, level) ->
            val classText = "${clazz.capitalizeFirst().colorClass}§7: ${level.classLevel.round(2).colorize(50)}"
            mcText(classText, screen.mainX, baseY + ((tH*scale)*(i+1)*1.3), scale, font, center = false)
        }
    }

    fun HypixelProfileData.DungeonTypeData.floorStats(floor: String): String {
        val start = if (floor == "0") "Entrance" else "Floor $floor"
        val string = "$start: §$c${this.tierComps[floor]?.commas} " +
                "§7| §$c${this.fastestTimes[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"} " +
                "§7| §$c${this.fastestTimeS[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"} " +
                "§7| §a${this.fastestTimeSPlus[floor]?.let { secondsToMinutes(it*0.001) } ?: "§cDNF"}"
        return string
    }

    private fun drawFloors(screen: ScreenObjects, player: HypixelProfileData.MemberData, lineX: Double) {
        val lineWidth = lineX - screen.mainX - screen.lineY
        val tH = getMCTextHeight()
        val floorData = (0..7).map { "§3${player.dungeons.dungeonTypes.catacombs.floorStats(it.toString())}" }
        val mmData = (1..7).map { "§cMM ${player.dungeons.dungeonTypes.mastermode.floorStats(it.toString())}" }
        val scale = 3f

        val entryHeight = screen.mainHeight/2 - (screen.outlineThickness/2) - screen.lineY

        val itemHeight = entryHeight/floorData.size
        val mmHeight = entryHeight/mmData.size

        val x = lineX + screen.lineY + screen.outlineThickness
        val lineY = screen.mainCenterY
        roundedRectangle(x, lineY, lineWidth, screen.outlineThickness, line)

        floorData.forEachIndexed { i, it ->
            //roundedRectangle(x, screen.mainY + (itemHeight*i), lineWidth, itemHeight, line)
            mcText(it, x, (screen.mainY + (itemHeight*i)) + (tH*scale)/2, scale, font, false, false)
        }
        mmData.forEachIndexed { i, it ->
            mcText(it, x, (screen.mainCenterY + screen.lineY + (screen.outlineThickness/2) + (mmHeight*i)) + (tH*scale)/2, scale, font, false, false)
        }

    }

}