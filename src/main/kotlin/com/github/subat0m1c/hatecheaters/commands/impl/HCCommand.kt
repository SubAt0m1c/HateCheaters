package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage

val HCCommand = Commodore("hc", "hatecheaters") {
    runs {
        modMessage("""
            §bHate§3Cheaters §fmodules are accessible through the odin config menu §8(§7/odin§8)§f.
            
            §fCommands List:
            §3| §f/hc §7: §8Main command.
            §3| §f/hcpv <name>  §8|§f /pv <name> §7: §8Opens the profile viewer. Will use your own ign if name is left blank.
            §3| §f/hcs <name> §8|§f /ds <name> §7: §8Displays dungeon stats about the ign. Will use your own ign if name is left blank.
            §3| §f/hcitems <itemname> §7: §8Manages a list of important items to be displays with the stats command.
            """.trimIndent()
        )
    }
}