package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.HateCheatersObject.screen
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.pvCommand
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadPlayer
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage

val PVCommand = commodore("pv") {
    runs { name: String?, profile: String? ->
        if (!pvCommand) return@runs modMessage("This command has been disabled! Restart your game to fully remove this command and allow neu's pv.")
        loadPlayer(name, profile)
        screen = PVGui
    }
}

val HCPVCommand = commodore("hcpv") {
    runs { name: String?, profile: String? ->
        loadPlayer(name, profile)
        screen = PVGui
    }
}