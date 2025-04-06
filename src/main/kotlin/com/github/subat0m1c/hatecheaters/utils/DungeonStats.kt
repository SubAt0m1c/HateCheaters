package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.BetterPartyFinder.importantItems
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.chatConstructor
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.witherImpactRegex
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.maxMagicalPower
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classLevel
import me.odinmain.utils.capitalizeFirst
import me.odinmain.utils.getSafe
import me.odinmain.utils.noControlCodes
import me.odinmain.utils.round
import me.odinmain.utils.skyblock.getChatBreak
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.skyblockID

object DungeonStats {
    private inline val petSet get() = setOf("ENDER_DRAGON", "SPIRIT", "GOLDEN_DRAGON", "JELLYFISH")

    fun displayDungeonData(currentProfile: HypixelData.MemberData, name: String, petMap: Set<String> = petSet, floor: Int) {
        val catacombs = currentProfile.dungeons

        val profileKills = currentProfile.playerStats.kills

        val fpbs = (1..7).map { it to catacombs.dungeonTypes.catacombs.fastestTimeSPlus[it.toString()] }
        val mmpbs =  (1..7).map { it to catacombs.dungeonTypes.mastermode.fastestTimeSPlus[it.toString()] }

        val allItems = currentProfile.allItems

        val armor = currentProfile.inventory.invArmor.itemStacks.filterNotNull().reversed()

        val pets = buildMap {
            currentProfile.pets.pets.forEachIndexed { i, pet ->
                if (pet.type in petMap) put(pet.colorName, pet.heldItem?.formatted)
            }
        }

        val items = importantItems.toSet().map { Pair(it, it.replace(" ", "_").uppercase() in allItems.map { it.skyblockID } ) }

        val mmComps = catacombs.dungeonTypes.mastermode.tierComps.without("total").values.sum()
        val floorComps = catacombs.dungeonTypes.catacombs.tierComps.without("total").values.sum()

        val hype = allItems.find { it?.lore?.any { it.noControlCodes.matches(witherImpactRegex) } == true }

        val tunings = currentProfile.tunings

        chatConstructor {
            displayText(getChatBreak())

            clickText(
                "\n§3| §2Player: §b$name",
                "/pv $name",
                listOf("§e§lCLICK §r§ato open profile viewer for §b$name")
            )

            displayText("\n§3| §4Cata Level: §f${catacombs.dungeonTypes.cataLevel.round(2).colorize(50)} §8: ")

            hoverText(
                "§dClass Avg: §6${catacombs.classAverage.round(2).colorize(50)}\n",
                catacombs.classes.entries.map { "§e${it.key.capitalizeFirst()} §7| ${it.value.classLevel.round(2).colorize(50)}" }
            )

            displayText("""
                §3| §bSecrets: §f${catacombs.secrets.colorize(100000)} §8: §bAverage: §f${(catacombs.secrets.toDouble()/(mmComps + floorComps)).round(2).colorize(15.0)}
                §3| §cBlood mobs: §f${(profileKills["watcher_summon_undead"] ?: 0) + (profileKills["master_watcher_summon_undead"] ?: 0)}
                """.trimIndent()
            )

            displayText()

            if (currentProfile.inventoryApi) {
                hoverText(
                    "\n§3| §5Magical Power: §f${currentProfile.magicalPower.colorize(maxMagicalPower)}",
                    listOf("Tunings: ") + tunings
                )

                displayText()

                hype?.let {
                    hoverText("\n§3| §5Wither Impact: §l§2Found!", (listOf(it.displayName) + it.lore))
                } ?: displayText("\n§3| §5Wither Impact: §o§4Missing!")
            } else {
                hoverText(
                    "\n§3| §5Assumed Magical Power: ${currentProfile.assumedMagicalPower.colorize(maxMagicalPower)}",
                    listOf("Assumed using the following tunings:") + tunings
                )

                displayText()

                displayText("\n§3| §o§4Inventory API Disabled!")
            }

            displayText()

            armor.forEach {
                hoverText(
                    "\n§3| ${it.displayName}",
                    (listOf(it.displayName) + it.lore)
                )
            }

            if (armor.isNotEmpty()) displayText()

            if (pets.isNotEmpty()) { // this isnt string built to allow different hover texts
                displayText("\n§3| ")
                pets.entries.forEachIndexed { i, (pet, item) ->
                    hoverText("${pet}${if (i != pets.entries.size - 1) ", " else "\n"}", listOf("§7Held Item: ${item ?: "§o§4None!"}"))
                }
            }

            hoverText(
                "\n§3| §7Personal Bests  §e§lHOVER §7(F$floor: ${fpbs.getSafe(floor -1)?.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"})",
                fpbs.map { "§aFloor ${it.first} §7| §2${it.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"}" }
            )

            hoverText(
                "\n§3| §4§lMM §8Personal Bests  §e§lHOVER §7(M$floor: ${mmpbs.getSafe(floor -1)?.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"})",
                mmpbs.map { "§cFloor ${it.first} §7| §2${it.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"}" }
            )

            if (importantItems.isNotEmpty() && currentProfile.inventoryApi) hoverText(
                "\n\n§3| §5Important Items  §e§lHOVER",
                items.map { "§b${it.first.capitalizeWords()} §7-> ${if (it.second) "§a✔" else "§4§l✖"}" }
            )

            displayText(getChatBreak())
        }.print()
    }
}