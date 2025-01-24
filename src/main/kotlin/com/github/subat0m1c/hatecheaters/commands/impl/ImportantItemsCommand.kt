package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder.importantItems
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import me.odinmain.config.Config

val ItemCommand = Commodore("hcitems") {
    literal("add").runs { item: GreedyString ->
        val name = item.string.lowercase().replace("_", " ").capitalizeWords()
        if (name in importantItems) return@runs modMessage("$name is already in the list!")
        importantItems.add(name)
        modMessage("$name has been added to the list!")
        Config.save()
    }

    literal("remove").runs { item: GreedyString ->
        val name = item.string.lowercase().replace("_", " ").capitalizeWords()
        if (importantItems.remove(name)) modMessage("$name has been removed from the list!")
        else modMessage("$name is not in the list!")
        Config.save()
    }

    literal("list").runs {
        if (importantItems.isEmpty()) modMessage("Item list is empty!")
        else modMessage("items:\n${importantItems.joinToString("\n")}")
    }

    literal("clear").runs {
        importantItems.clear()
        Config.save()
        modMessage("Cleared the items list!")
    }
}