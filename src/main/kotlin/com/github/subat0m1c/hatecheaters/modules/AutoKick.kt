package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.WebUtils.getCurrentProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.NumberSetting
import me.odinmain.features.settings.impl.SelectorSetting
import me.odinmain.utils.skyblock.sendCommand

object AutoKick : Module(
    name = "Auto Kick",
    description = "Automatically kicks players who dont meet certain stats.",
    category = Category.DUNGEON
) {
    //private val useRating: Boolean by BooleanSetting("Use Rating", default = false, description = "Takes collected data, assigns values to them, and adds them to get a rating. Uses this rating number to kick, higher is better.")
    //private val rating: Double by NumberSetting("rating", 0.0, 0.0, Double.MAX_VALUE, hidden = true, description = "rating")

    private val timeKick: Boolean by BooleanSetting("Check Time", default = false, description = "Kicks for time")
    private val floors = arrayListOf("entrance", "floor_1", "floor_2", "floor_3", "floor_4", "floor_5", "floor_6", "floor_7")
    private val floor: Int by SelectorSetting("Floor", defaultSelected = "floor_7", floors, false, description = "Floor to use").withDependency { timeKick }
    private val mmToggle: Boolean by BooleanSetting("Master Mode", true, description = "Use master mode times").withDependency { timeKick }
    private val timereq: Int by NumberSetting("time", 0, 0, 500, description = "Time minimum in seconds")

    private val secretKick: Boolean by BooleanSetting("Check Secrets", default = true, description = "Kicks for secrets")
    private val secretsreq: Int by NumberSetting("secrets", 0, 0, 200, description = "Secret minimum in thousands.").withDependency { secretKick }

    private val savgKick: Boolean by BooleanSetting("Check Secret Average", default = false, description = "Kicks for secret average.")
    private val savgreq: Float by NumberSetting("Secret Average", 0f, 0f, 20f, description = "Secret average minimum.").withDependency { savgKick }

    private val pfRegex = Regex("Party Finder > (?:\\[.{1,7}])? ?(.{1,16}) joined the dungeon group! \\((?:.*)\\)")

    init {
        onMessage(pfRegex) {
            val name = pfRegex.find(it)?.groupValues?.get(1).toString()
            GlobalScope.launch(Dispatchers.IO) {
                var kicked = false

                val currentProfile = getCurrentProfile(name)

                val dungeon = if (!mmToggle) currentProfile?.dungeons?.catacombs else currentProfile?.dungeons?.mastercatacombs
                val fastestTime = dungeon?.floors?.get(floor)?.stats?.fastestTimeSPlus
                fastestTime?.let {
                    if (timeKick && it < (timereq * 1000)) {
                        modMessage("$it | ${timereq * 1000}")
                        kicked = true
                    } else modMessage("$it | ${timereq * 1000}")
                }

                val secretCount = currentProfile?.dungeons?.secretsFound
                secretCount?.let {
                    if (secretKick && it < (secretsreq * 1000)) {
                        modMessage("$it | ${secretsreq * 1000}")
                        kicked = true
                    } else modMessage("$it | ${secretsreq * 1000}")
                }

                val floorComps = (currentProfile?.dungeons?.floorCompletions ?: 0)
                val savg = (secretCount?.toDouble() ?: 0.0)/floorComps.toDouble()
                if (savg.toFloat() < savgreq) {
                    modMessage("${savg.toFloat()} | $savgreq | $floorComps")
                    kicked = true
                } else modMessage("${savg.toFloat()} | $savgreq | $floorComps")

                if (kicked) sendCommand("party kick $name")
            }
        }
    }
}