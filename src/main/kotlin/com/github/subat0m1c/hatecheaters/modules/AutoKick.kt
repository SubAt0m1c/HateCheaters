package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getDungeonProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.odinmain.OdinMain.scope
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.ActionSetting
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.NumberSetting
import me.odinmain.features.settings.impl.SelectorSetting
import me.odinmain.utils.skyblock.sendCommand
import kotlin.math.floor

object AutoKick : Module(
    name = "Auto Kick",
    description = "Automatically kicks players who don't meet certain requirements.",
    category = Category.DUNGEON
) {
    private val timeKick: Boolean by BooleanSetting("Check Time", default = false, description = "Kicks for time")
    private val floors = arrayListOf("Entrance", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7")
    private val floor: Int by SelectorSetting("Floor", defaultSelected = "Floor 7", floors, false, description = "Determines which floor to check pb.").withDependency { timeKick }
    private val mmToggle: Boolean by BooleanSetting("Master Mode", true, description = "Use master mode times").withDependency { timeKick }
    private val timereq: Int by NumberSetting("time", 0, 0, 500, description = "Time minimum in seconds.")

    private val secretKick: Boolean by BooleanSetting("Check Secrets", default = true, description = "Kicks for secrets")
    private val secretsreq: Int by NumberSetting("secrets", 0, 0, 200, description = "Secret minimum in thousands.").withDependency { secretKick }

    private val savgKick: Boolean by BooleanSetting("Check Secret Average", default = false, description = "Kicks for secret average.")
    private val savgreq: Float by NumberSetting("Secret Average", 0f, 0f, 20f, description = "Secret average minimum.").withDependency { savgKick }

    //private val pingKick: Boolean by BooleanSetting("Check Ping", default = false, description = "attempts to get player's pings and kicks if outside given range.")
    //private val pingMin: Int by NumberSetting("Ping Min", 1, 0, 500, description = "Minimum ping to avoid a kick")
    //private val pingMax: Int by NumberSetting("Ping Max", 1, 0, 500, description = "Maximum ping to avoid a kick")
    private val kickCache: Boolean by BooleanSetting("Kick Cache", default = true, description = "Caches kicked players to automatically kick when they attempt to rejoin.")
    private val action: () -> Unit by ActionSetting("Clear Cache", description = "Clears the kicklist cache.") { kickedList.clear() }.withDependency { kickCache }

    private val pfRegex = Regex("Party Finder > (?:\\[.{1,7}])? ?(.{1,16}) joined the dungeon group! \\((?:.*)\\)") //https://regex101.com/r/XYnAVm/1
    private val kickRegex = Regex("(?:\\[.{1,7}])? ?(.{1,16}) has been removed from the party\\.")

    private val kickedList = mutableListOf<String>()

    init {
        onMessage(pfRegex) {
            val name = pfRegex.find(it)?.groupValues?.get(1).toString()
            if (name == mc.thePlayer.name) return@onMessage

            modMessage("$name is being searched")

            if (kickedList.contains(name) && kickCache) {
                sendCommand("party kick $name")
                modMessage("Kicked $name since they have been kicked previously.")
                return@onMessage
            }

            scope.launch(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    modMessage("scope launched")
                    val kickedReasons = mutableListOf<String>()

                    val currentProfile = getDungeonProfile(name)

                    val dungeon = if (!mmToggle) currentProfile?.dungeons?.catacombs else currentProfile?.dungeons?.masterCatacombs
                    dungeon?.floors?.get(floor)?.stats?.fastestTimeSPlus?.times(0.001)?.let {
                        if (!timeKick) return@let
                        if (it > (timereq)) {
                            modMessage("$it | $timereq}")
                            kickedReasons.add("Did not meet time req: ${secondsToMinutes(it)}/${secondsToMinutes(it)}")
                        } else modMessage("$it | $timereq")
                    }

                    val secretCount = currentProfile?.dungeons?.secretsFound
                    secretCount?.let {
                        if (!secretKick) return@let
                        if (it < (secretsreq * 1000)) {
                            modMessage("$it | ${secretsreq * 1000}")
                            kickedReasons.add("Did not meet secret req: ${it}/${secretsreq}")
                        } else modMessage("$it | ${secretsreq * 1000}")
                    }

                    val floorComps = (currentProfile?.dungeons?.floorCompletions ?: 0)
                    ((secretCount?.toDouble() ?: 0.0)/floorComps.toDouble()).toFloat().let {
                        if (!savgKick) return@let
                        if (it < savgreq) {
                            modMessage("$it | $savgreq | $floorComps")
                            kickedReasons.add("Did not meet savg req: $it/${savgreq}")
                        } else modMessage("$it | $savgreq | $floorComps")
                    }

                    if (kickedReasons.isNotEmpty()) {
                        sendCommand("party kick $name")
                        modMessage("Kicked $name for:\n${kickedReasons.joinToString("\n")}")
                    }
                }
            }
        }

        onMessage(kickRegex, { kickCache && enabled }) { kickRegex.find(it)?.groupValues?.get(1)?.let { name -> kickedList.add(it).takeUnless { kickedList.contains(name) } } }
    }

    fun secondsToMinutes(totalSeconds: Number): String {
        val minutes = floor(totalSeconds.toDouble()/60.0).toInt()
        val seconds = (totalSeconds.toDouble()%60.0).toInt()
        return String.format("%1d:%02d", minutes, seconds)
    }
}