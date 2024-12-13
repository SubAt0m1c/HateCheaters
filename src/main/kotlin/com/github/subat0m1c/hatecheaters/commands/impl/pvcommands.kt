package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.pvCommand
import com.github.subat0m1c.hatecheaters.modules.launchPV
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import me.odinmain.OdinMain.mc
import me.odinmain.utils.containsOneOf


val PVCommand = commodore("pv") {
    runs { name: String? ->
        if (!pvCommand) return@runs modMessage("This command has been disabled! Restart your game to fully remove this command and allow neu's pv.")
        launchPV(name)
    } suggests {
        mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } }
    }

    literal("profile").runs { name: String?, profile: String? ->
        if (!pvCommand) return@runs modMessage("This command has been disabled! Restart your game to fully remove this command and allow neu's pv.")
        launchPV(name, profile)
    }
}

val HCPVCommand = commodore("hcpv") {
    runs { name: String? ->
        launchPV(name)
    } suggests {
        mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } }
    }

    literal("profile").runs { name: String?, profile: String? ->
        launchPV(name, profile)
    }
}