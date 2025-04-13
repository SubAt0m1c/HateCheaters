package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer.pvCommand
import com.github.subat0m1c.hatecheaters.modules.render.launchPV
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import me.odinmain.OdinMain.mc
import me.odinmain.utils.containsOneOf

val PVCommand = Commodore("pv") {
    executable {
        param("name") {
            suggests { mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } } }
        }

        runs { name: String? ->
            if (!pvCommand) return@runs modMessage("This command has been disabled! Restart your game to fully remove this command and allow neu's pv.")
            launchPV(name)
        }
    }

    literal("profile").runs { name: String?, profile: String? ->
        if (!pvCommand) return@runs modMessage("This command has been disabled! Restart your game to fully remove this command and allow neu's pv.")
        launchPV(name, profile)
    }
}

val HCPVCommand = Commodore("hcpv") {
    executable {
        param("name") {
            suggests { mc.netHandler.playerInfoMap.mapNotNull { it.gameProfile.name.lowercase().takeUnless { it.containsOneOf("!", " ") } } }
        }

        runs { name: String? ->
            launchPV(name)
        }
    }

    literal("profile").runs { name: String?, profile: String? ->
        launchPV(name, profile)
    }
}