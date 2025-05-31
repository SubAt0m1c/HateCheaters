package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.toastMessage
import com.github.subat0m1c.hatecheaters.utils.DungeonStats.displayDungeonData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.cachedPlayers
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfile
import me.odinmain.OdinMain.mc
import me.odinmain.utils.containsOneOf

val StatsCommand = Commodore("hcs", "ds", "hcstats") {
    executable {
        param("name").suggests {
            mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } } + cachedPlayers
        }

        param("floor").suggests { setOf("1", "2", "3", "4", "5", "6", "7") }

        runs { name: String?, floor: Int? ->
            val ign = name ?: mc.thePlayer.name
            launch {
                val profiles = getProfile(ign).getOrElse { return@launch modMessage(it.message) }
                profiles.memberData?.let { displayDungeonData(it, profiles.name, floor = floor ?: 7) }
                    ?: return@launch toastMessage(
                        "Failed to load $name",
                        "$name either doesn't exist or hasn't joined skyblock!"
                    )
            }
        }
    }
}