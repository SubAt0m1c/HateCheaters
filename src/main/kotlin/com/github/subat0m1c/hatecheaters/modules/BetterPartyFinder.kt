package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.scope
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.cataLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.classAverage
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.classLevel
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.magicalPower
import com.github.subat0m1c.hatecheaters.utils.ApiUtils.itemStacks
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.chatConstructor
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getSkyblockProfile
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.utils.capitalizeFirst
import me.odinmain.utils.noControlCodes
import me.odinmain.utils.round
import me.odinmain.utils.skyblock.*

object BetterPartyFinder : Module(
    name = "Better Party Finder",
    description = "Provides stats when a player joins your party. Includes autokick functionality. /hcitems to configure important items list.sk",
    category = Category.DUNGEON
) {
    private val statsDisplay: Boolean by BooleanSetting("Stats display", default = true, description = "Displays stats of players who join your party")

    private val floors = arrayListOf("Entrance", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7")
    private val floor: Int by SelectorSetting("Floor", defaultSelected = "Floor 7", floors, false, description = "Determines which floor to check pb.")
    private val mmToggle: Boolean by BooleanSetting("Master Mode", true, description = "Use master mode times")

    private val autoKickDropdwon: Boolean by DropdownSetting("Auto Kick Dropdown", default = true)
    private val autokicktoggle: Boolean by BooleanSetting("Auto Kick", default = false, description = "Automatically kicks players who don't meet requirements.").withDependency { autoKickDropdwon }
    private val timeKick: Boolean by BooleanSetting("Check Time", default = false, description = "Kicks for time").withDependency { autoKickDropdwon && autokicktoggle }
    private val timereq: Int by NumberSetting("time", 0, 0, 500, description = "Time minimum in seconds.").withDependency { autoKickDropdwon && autokicktoggle }

    private val secretKick: Boolean by BooleanSetting("Check Secrets", default = true, description = "Kicks for secrets").withDependency { autoKickDropdwon && autokicktoggle }
    private val secretsreq: Int by NumberSetting("secrets", 0, 0, 200, description = "Secret minimum in thousands.").withDependency { secretKick && autoKickDropdwon && autokicktoggle }

    private val savgKick: Boolean by BooleanSetting("Check Secret Average", default = false, description = "Kicks for secret average.").withDependency { autoKickDropdwon && autokicktoggle }
    private val savgreq: Float by NumberSetting("Secret Average", 0f, 0f, 20f, description = "Secret average minimum.").withDependency { savgKick && autoKickDropdwon && autokicktoggle }

    private val kickCache: Boolean by BooleanSetting("Kick Cache", default = true, description = "Caches kicked players to automatically kick when they attempt to rejoin.")
    private val action: () -> Unit by ActionSetting("Clear Cache", description = "Clears the kick list cache.") { kickedList.clear() }.withDependency { kickCache }

    private val pfRegex = Regex("Party Finder > (?:\\[.{1,7}])? ?(.{1,16}) joined the dungeon group! \\(.*\\)") //https://regex101.com/r/XYnAVm/1
    private val kickRegex = Regex("(?:\\[.{1,7}])? ?(.{1,16}) has been removed from the party\\.")

    private val kickedList = mutableListOf<String>()

    val importantItems: MutableList<String> by ListSetting("itememsmemsmsms", mutableListOf())

    init {
        onMessage(pfRegex, { enabled && statsDisplay && !autokicktoggle}) {
            val name = pfRegex.find(it)?.groupValues?.get(1).toString()
            if (name == mc.session.username) return@onMessage

            scope {
                val profiles = getSkyblockProfile(name).getOrElse { return@scope modMessage(it.message) }
                profiles.profileData.profiles
                    .find { it.selected }?.members?.get(profiles.uuid)
                    ?.let { displayDungeonData(it, profiles.name) }
                    ?: return@scope modMessage("""
                            ${getChatBreak()}
                            Could not find info for player $name
                            ${getChatBreak()}
                            """.trimIndent(), ""
                    )
            }
        }


        onMessage(pfRegex, { enabled && autokicktoggle}) {
            val name = pfRegex.find(it)?.groupValues?.get(1).toString()
            if (name == mc.session.username) return@onMessage

            logger.info("$name is being searched")

            if (kickedList.contains(name) && kickCache) {
                sendCommand("party kick $name")
                modMessage("Kicked $name since they have been kicked previously.")
                return@onMessage
            }

            scope {
                val kickedReasons = mutableListOf<String>()

                val profiles = getSkyblockProfile(name).getOrElse { return@scope modMessage(it.message) }
                val currentProfile = profiles.profileData.profiles.find { it.selected }?.members?.get(profiles.uuid)
                    ?: return@scope modMessage("""
                        ${getChatBreak()}
                        Could not find info for player $name
                        ${getChatBreak()}
                        """.trimIndent(), ""
                    )

                val dungeon = if (!mmToggle) currentProfile.dungeons.dungeonTypes.catacombs else currentProfile.dungeons.dungeonTypes.mastermode
                dungeon.fastestTimeSPlus["$floor"]?.times(0.001)?.let {
                    if (!timeKick) return@let
                    if (it > (timereq)) {
                        kickedReasons.add("Did not meet time req: ${secondsToMinutes(it)}/${secondsToMinutes(it)}")
                    }
                } ?: kickedReasons.add("Couldn't confirm completion status!")

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
                        kickedReasons.add("Did not meet savg req: $it/${savgreq}")
                    }
                }

                if (kickedReasons.isNotEmpty()) {
                    sendCommand("party kick $name")
                    modMessage("Kicked $name for:\n${kickedReasons.joinToString("\n")}")
                    return@scope
                }

                displayDungeonData(currentProfile, profiles.name)
            }
        }

        onMessage(kickRegex, { kickCache && enabled }) { kickRegex.find(it)?.groupValues?.get(1)?.let { name -> kickedList.add(it).takeUnless { kickedList.contains(name) } } }
    }

    private val witherImpactRegex = Regex("(?:⦾ )?Ability: Wither Impact {2}RIGHT CLICK")

    suspend fun displayDungeonData(currentProfile: HypixelProfileData.MemberData, name: String): Unit = withContext(Dispatchers.Default) {
        val catacombs = currentProfile.dungeons

        val profileKills = currentProfile.playerStats.kills

        val fpbs = (1..7).map { it to catacombs.dungeonTypes.catacombs.fastestTimeSPlus[it.toString()] }
        val mmpbs =  (1..7).map { it to catacombs.dungeonTypes.mastermode.fastestTimeSPlus[it.toString()] }

        val allItems = (currentProfile.inventory.invContents.itemStacks + currentProfile.inventory.eChestContents.itemStacks + currentProfile.inventory.backpackContents.flatMap { it.value.itemStacks })

        val armor = currentProfile.inventory.invArmor.itemStacks.filterNotNull().reversed()

        val items = importantItems.toSet().map { Pair(it, it.replace(" ", "_").uppercase() in allItems.map { it.skyblockID } ) }

        val mmComps = (catacombs.dungeonTypes.mastermode.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()
        val floorComps = (catacombs.dungeonTypes.catacombs.tierComps.toMutableMap().apply { this.remove("total") }).values.sum()

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
            
                §3| ${if (allItems.isNotEmpty()) "§eMagical power: ${currentProfile.magicalPower.colorize(1697)} §7(${currentProfile.accessoryBagStorage.selectedPower})" else "§o§4Inventory Api Disabled!"}
            
                §3| §5Wither Impact: ${if (allItems.any { it?.lore?.any { it.noControlCodes.matches(witherImpactRegex) } == true }) "§l§2Found!" else "§o§4Missing!"}
                """.trimIndent()
            )

            displayText()

            armor.forEach {
                hoverText(
                    "\n§3| ${it.displayName}",
                    (listOf(it.displayName) + it.lore)
                )
            }

            if (armor.isNotEmpty()) displayText()

            hoverText(
                "\n§3| §7Personal Bests  §e§lHOVER",
                fpbs.map { "§aFloor ${it.first} §7| §2${it.second?.let { secondsToMinutes(it * 0.001) } ?: "§o§4None!"}" }
            )

            hoverText(
                "\n§3| §4§lMM §8Personal Bests  §e§lHOVER",
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