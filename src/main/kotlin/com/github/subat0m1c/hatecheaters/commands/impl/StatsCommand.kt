package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.HateCheatersObject.scope
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder.displayDungeonData
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getSkyblockProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.odinmain.OdinMain.mc
import me.odinmain.utils.skyblock.getChatBreak

val StatsCommand = commodore("hcs", "ds", "hcstats") {
    runs { name: String? ->
        val ign = name ?: mc.thePlayer.name
        scope.launch(Dispatchers.IO) {
            val profiles = getSkyblockProfile(ign, false)
            profiles?.profileData?.profiles
                ?.find { it.selected }?.members?.get(profiles.uuid)
                ?.let { displayDungeonData(it, profiles.name) }
                ?: run {
                    modMessage("""
                            ${getChatBreak()}
                            Could not find info for player $ign 
                            ${getChatBreak()}
                            """.trimIndent(), false
                    )
                }
        }
    }
}