package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.CheckUpdate
import com.github.subat0m1c.hatecheaters.utils.WebUtils.testQue
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import net.minecraft.client.gui.GuiScreen

val DevCommand = Commodore("hcdev") {

    literal("checkupdate").runs {
        modMessage("Checking for updates...")
        CheckUpdate.lookForUpdates()
    }

    literal("apitest") {
        runs { name: String, skipCache: Boolean? ->
            launch {
                getSkyblockProfile(name, skipCache ?: false)
                    .getOrElse { return@launch modMessage(it.message) }.memberData ?: return@launch modMessage("Could not find player data.")
                modMessage("Succeeded!")
            }
        }

        literal("que").runs {
            launch {
                modMessage("testing que...")
                testQue()
            }
        }
    }

    literal("writetoclipboard").runs { text: GreedyString ->
        GuiScreen.setClipboardString(text.string)
        modMessage("Copied \"${text.string}\"!")
    }
}