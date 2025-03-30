package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.chatConstructor
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorStat
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.apiutils.DataUtils.colorName
import com.github.subat0m1c.hatecheaters.utils.apiutils.DataUtils.maxMagicalPower
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.apiutils.LevelUtils.classLevel
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSkyblockProfile
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.utils.*
import me.odinmain.utils.skyblock.*
import me.odinmain.utils.skyblock.PlayerUtils.alert
import kotlin.collections.HashSet
import kotlin.math.ceil

object BetterPartyFinder : Module(
    name = "Better Party Finder",
    description = "Provides stats when a player joins your party. Includes autokick functionality. /hcitems to configure important items list.sk",
    category = Category.DUNGEON
) {
    private val fullPartyNotification by BooleanSetting("Full Party Notification", default = true, description = "Notifies you when your party is full.")

    private val statsDisplay by BooleanSetting("Stats display", default = true, description = "Displays stats of players who join your party")

    private val floors = arrayListOf("Entrance", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7")
    private val floor by SelectorSetting("Floor", defaultSelected = "Floor 7", floors, false, description = "Determines which floor to check pb.")
    private val mmToggle by BooleanSetting("Master Mode", true, description = "Use master mode times")

    private val autoKickDropdwon by DropdownSetting("Auto Kick Dropdown", default = true)
    private val informkicked by BooleanSetting("Inform Kicked", default = false, description = "Informs the player why they were kicked.").withDependency { autoKickDropdwon }
    private val autokicktoggle by BooleanSetting("Auto Kick", default = false, description = "Automatically kicks players who don't meet requirements.").withDependency { autoKickDropdwon }
    private val timeKick by BooleanSetting("Check Time", default = false, description = "Kicks for time").withDependency { autoKickDropdwon && autokicktoggle }
    private val timeMinutes by NumberSetting("Minutes", 5, 0, 10, description = "Time minimum in minutes.", unit = "m").withDependency { autoKickDropdwon && autokicktoggle }
    private val timeSeconds by NumberSetting("Seconds", 0, 0, 60, description = "Time minimum in seconds.", unit = "s").withDependency { autoKickDropdwon && autokicktoggle }
    private inline val timeReq get() = timeMinutes * 60 + timeSeconds

    private val secretKick by BooleanSetting("Check Secrets", default = true, description = "Kicks for secrets").withDependency { autoKickDropdwon && autokicktoggle }
    private val secretsreq by NumberSetting("Secrets", 0, 0, 200, description = "Secret minimum in thousands.", unit = "k").withDependency { secretKick && autoKickDropdwon && autokicktoggle }

    private val savgKick by BooleanSetting("Check Secret Average", default = false, description = "Kicks for secret average.").withDependency { autoKickDropdwon && autokicktoggle }
    private val savgreq by NumberSetting("Secret Average", 0f, 0f, 20f, description = "Secret average minimum.").withDependency { savgKick && autoKickDropdwon && autokicktoggle }

    private val checkItems by BooleanSetting("Check Items", default = false, description = "Enables checking player items.").withDependency { autoKickDropdwon && autokicktoggle }
    private val apiOffKick by BooleanSetting("Api Off Kick", default = true, description = "Kicks if the player's api is off. If this setting is disabled, it will ignore the item check when players have api disabled.").withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val magicalPowerKick by BooleanSetting("Check Magical Power", default = false, description = "Kicks if the player doesn't have enough magical power.").withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val magicalPowerReq by NumberSetting("Magical Power", 1300, 0, 2000, increment = 20, description = "Magical power minimum.").withDependency { magicalPowerKick && autoKickDropdwon && autokicktoggle && checkItems }
    private val witherImpactKick by BooleanSetting("Wither Impact", default = false, description = "Kicks if the player doesn't have wither impact.").withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val dragKick by BooleanSetting("Gdrag/Edrag", default = false, description = "Kicks if the player doesn't have gdrag or edrag.").withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val spiritKick by BooleanSetting("Spirit Pet", default = false, description = "Kicks if the player doesn't have spirit pet.").withDependency { autoKickDropdwon && autokicktoggle && checkItems }

    private val petMap = mapOf(
        "ENDER_DRAGON" to { dragKick },
        "SPIRIT" to { spiritKick },
        "GOLDEN_DRAGON" to { dragKick },
        "JELLYFISH" to { false },
    )

    private val kickCache by BooleanSetting("Kick Cache", default = true, description = "Caches kicked players to automatically kick when they attempt to rejoin.")
    private val action: () -> Unit by ActionSetting("Clear Cache", description = "Clears the kick list cache.") { kickedList.clear() }.withDependency { kickCache }

    private val pfRegex = Regex("Party Finder > (?:\\[.{1,7}])? ?(.{1,16}) joined the dungeon group! \\(.*\\)") //https://regex101.com/r/XYnAVm/1
    private val kickRegex = Regex("(?:\\[.{1,7}])? ?(.{1,16}) has been removed from the party\\.")

    private val kickedList = mutableListOf<String>()

    val importantItems: MutableList<String> by ListSetting("itememsmemsmsms", mutableListOf())

    init {
        onMessage(Regex("Party Finder > Your dungeon group is full! Click here to warp to the dungeon!"), {enabled && fullPartyNotification}) {
            alert("§eYour party is full!")
        }

        onMessage(pfRegex, { enabled && statsDisplay && !autokicktoggle}) {
            val name = it.groupValues[1].takeUnless { it == mc.session.username } ?: return@onMessage

            launch {
                val profiles = getSkyblockProfile(name).getOrElse { return@launch modMessage(it.message) }
                profiles.profileData.profiles
                    .find { it.selected }?.members?.get(profiles.uuid)
                    ?.let { displayDungeonData(it, profiles.name) }
                    ?: return@launch modMessage("""
                            ${getChatBreak()}
                            Could not find info for player $name
                            ${getChatBreak()}
                            """.trimIndent(), ""
                    )
            }
        }


        onMessage(pfRegex, { enabled && autokicktoggle}) {
            val name = it.groupValues[1].takeUnless { it == mc.session.username } ?: return@onMessage

            Logger.info("$name is being searched")

            if (kickedList.contains(name) && kickCache) {
                sendCommand("party kick $name")
                modMessage("Kicked $name since they have been kicked previously.")
                return@onMessage
            }

            launch {
                val kickedReasons = mutableListOf<String>()

                val profiles = getSkyblockProfile(name).getOrElse { return@launch modMessage(it.message) }
                val currentProfile = profiles.memberData
                    ?: return@launch modMessage("""
                        ${getChatBreak()}
                        Could not find info for player $name
                        ${getChatBreak()}
                        """.trimIndent(), ""
                    )

                val dungeon = if (!mmToggle) currentProfile.dungeons.dungeonTypes.catacombs else currentProfile.dungeons.dungeonTypes.mastermode
                dungeon.fastestTimeSPlus["$floor"]?.times(0.001)?.let {
                    if (!timeKick) return@let
                    if (it > (timeReq)) {
                        kickedReasons.add("Did not meet time req for ${if (mmToggle) "m" else "f"}${floor}: ${secondsToMinutes(it)}/${secondsToMinutes(timeReq)}")
                    }
                } ?: kickedReasons.add("Couldn't confirm completion status for ${if (mmToggle) "m" else "f"}${floor}")

                val secretCount = currentProfile.dungeons.secrets
                secretCount.let {
                    if (!secretKick) return@let
                    if (it < (secretsreq * 1000)) {
                        kickedReasons.add("Did not meet secret req: ${it}/${secretsreq}")
                    }
                }

                val mmComps = (currentProfile.dungeons.dungeonTypes.mastermode.tierComps.entries.sumOf { entry ->  entry.value.takeUnless { entry.key == "total" } ?: 0 })
                val floorComps = (currentProfile.dungeons.dungeonTypes.catacombs.tierComps.entries.sumOf { entry ->  entry.value.takeUnless { entry.key == "total" } ?: 0 })
                ((secretCount.toDouble()/(mmComps + floorComps).toDouble()).toFloat()).let {
                    if (!savgKick) return@let
                    if (it < savgreq) {
                        kickedReasons.add("Did not meet savg req: ${it.round(2)}/${savgreq}")
                    }
                }

                if (checkItems) {
                    val pets = currentProfile.pets.pets.mapNotNullTo(HashSet()) { if (it.tier != "LEGENDARY") null else it.type }

                    for (entry in petMap) if (entry.value() && entry.key !in pets) kickedReasons.add("Did not have legendary ${entry.key.formatted}")

                    if (currentProfile.inventoryApi) {
                        if (witherImpactKick && currentProfile.allItems.none { it?.lore?.any { it.noControlCodes.matches(witherImpactRegex) } == true }) kickedReasons.add("Did not have wither impact")

                        val mp = currentProfile.magicalPower
                        if (magicalPowerKick && mp < magicalPowerReq) kickedReasons.add("Did not meet mp req: ${mp}/${magicalPowerReq}")
                    } else if (apiOffKick) kickedReasons.add("Inventory API is off")
                }

                if (kickedReasons.isNotEmpty()) {
                    if (informkicked) {
                        runIn(5) { sendCommand("party kick $name") }
                        partyMessage("Kicked $name for: ${kickedReasons.joinToString(", ")}")
                    } else sendCommand("party kick $name")

                    return@launch modMessage("Kicking $name for: \n${kickedReasons.joinToString(" \n")}")
                }

                displayDungeonData(currentProfile, profiles.name)
            }
        }

        onMessage(kickRegex, { kickCache && enabled }) { message ->
            message.groupValues[1].takeUnless { name -> kickedList.contains(name) }?.let { name -> kickedList.add(name) }
        }
    }

    private val witherImpactRegex = Regex("(?:⦾ )?Ability: Wither Impact {2}RIGHT CLICK")

    fun displayDungeonData(currentProfile: HypixelData.MemberData, name: String) {
        val catacombs = currentProfile.dungeons

        val profileKills = currentProfile.playerStats.kills

        val fpbs = (1..7).map { it to catacombs.dungeonTypes.catacombs.fastestTimeSPlus[it.toString()] }
        val mmpbs =  (1..7).map { it to catacombs.dungeonTypes.mastermode.fastestTimeSPlus[it.toString()] }

        val allItems = currentProfile.allItems

        val armor = currentProfile.inventory.invArmor.itemStacks.filterNotNull().reversed()

        val pets = buildMap {
            currentProfile.pets.pets.forEachIndexed { i, pet ->
                if (pet.type in petMap.keys) put(pet.colorName, pet.heldItem?.formatted)
            }
        }

        val items = importantItems.toSet().map { Pair(it, it.replace(" ", "_").uppercase() in allItems.map { it.skyblockID } ) }

        val mmComps = (catacombs.dungeonTypes.mastermode.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
        val floorComps = (catacombs.dungeonTypes.catacombs.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()

        val hype = allItems.find { it?.lore?.any { it.noControlCodes.matches(witherImpactRegex) } == true }

        val tunings = currentProfile.accessoryBagStorage.tuning.currentTunings.map { "${it.key.replace("_", " ").capitalizeWords().colorStat}§7: ${it.value.colorize(ceil(currentProfile.magicalPower /10.0))}" }

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
                "\n§3| §7Personal Bests  §e§lHOVER §7(F${floor}: ${fpbs.getSafe(floor-1)?.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"})",
                fpbs.map { "§aFloor ${it.first} §7| §2${it.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"}" }
            )

            hoverText(
                "\n§3| §4§lMM §8Personal Bests  §e§lHOVER §7(M${floor}: ${mmpbs.getSafe(floor-1)?.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"})",
                mmpbs.map { "§cFloor ${it.first} §7| §2${it.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"}" }
            )

            if (importantItems.isNotEmpty() && allItems.isNotEmpty()) hoverText(
                "\n\n§3| §5Important Items  §e§lHOVER",
                items.map { "§b${it.first.capitalizeWords()} §7-> ${if (it.second) "§a✔" else "§4§l✖"}" }
            )

            displayText(getChatBreak())
        }.print()
    }
}