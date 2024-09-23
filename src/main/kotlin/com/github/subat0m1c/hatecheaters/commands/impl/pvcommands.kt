package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.utils.GreedyString
import com.github.subat0m1c.hatecheaters.HateCheatersObject.screen
import com.github.subat0m1c.hatecheaters.commands.commodore
import com.github.subat0m1c.hatecheaters.modules.ProfileViewer.petsList
import com.github.subat0m1c.hatecheaters.pvgui.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.PVGui.loadPlayer
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import me.odinmain.OdinMain.mc
import me.odinmain.config.Config

val PVCommand = commodore("pv", "hcpv") {
    runs { name: String? ->
        loadPlayer(name)
        screen = PVGui
        logger.info("Trying to display pvgui")
    }

    literal("pets") {
        literal("add").runs { name: GreedyString ->
            val pet = name.string.lowercase().replace("_", " ").capitalizeWords()
            if (pet in petsList) return@runs modMessage("$pet is already in the pets list!")
            petsList.add(pet)
            modMessage("$pet added to pets list!")
            Config.save()
        }

        literal("remove").runs { name: GreedyString ->
            val pet = name.string.lowercase().replace("_", " ").capitalizeWords()
            if (petsList.remove(pet)) modMessage("$pet removed from pets list!")
            else modMessage("$pet is not in the pets list!")
            Config.save()
        }

        literal("list").runs {
            modMessage("Pets list:\n${petsList.joinToString("\n")}")
        }

        literal("clear").runs {
            petsList.clear()
            modMessage("Pets list has been cleared!")
            Config.save()
        }
    }
}