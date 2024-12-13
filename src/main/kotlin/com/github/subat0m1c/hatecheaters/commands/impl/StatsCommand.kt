package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder.displayDungeonData
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.apiutils.ApiUtils.memberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import me.odinmain.OdinMain.mc
import me.odinmain.utils.containsOneOf
import me.odinmain.utils.skyblock.getChatBreak

val StatsCommand = commodore("hcs", "ds", "hcstats") {
    runs { name: String? ->
        val ign = name ?: mc.thePlayer.name
        launch {
            val profiles = getSkyblockProfile(ign).getOrElse { return@launch modMessage(it.message) }
            profiles.memberData?.let { displayDungeonData(it, profiles.name) }
                ?: return@launch modMessage("""
                    ${getChatBreak()}
                    Could not find info for player $ign 
                    ${getChatBreak()}
                    """.trimIndent(), ""
                )
        }
    } suggests {
        mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } }
    }
}