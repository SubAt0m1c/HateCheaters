package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.subat0m1c.hatecheaters.HateCheatersObject.screen
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadPlayer

val PVCommand = commodore("pv", "hcpv") {
    runs { name: String?, profile: String? ->
        loadPlayer(name, profile)
        screen = PVGui
    }
}