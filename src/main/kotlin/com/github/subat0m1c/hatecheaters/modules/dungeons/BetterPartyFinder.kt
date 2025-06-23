package com.github.subat0m1c.hatecheaters.modules.dungeons

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.secondsToMinutes
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.toastMessage
import com.github.subat0m1c.hatecheaters.utils.DungeonStats.displayDungeonData
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.witherImpactRegex
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfile
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.utils.noControlCodes
import me.odinmain.utils.round
import me.odinmain.utils.runIn
import me.odinmain.utils.skyblock.PlayerUtils
import me.odinmain.utils.skyblock.PlayerUtils.alert
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.partyMessage
import me.odinmain.utils.skyblock.sendCommand

object BetterPartyFinder : Module(
    name = "Better Party Finder",
    desc = "Provides stats when a player joins your party. Includes autokick functionality. /hcitems to configure important items list.",
) {
    private val defaultSounds = arrayListOf("note.pling", "random.pop", "random.orb", "random.break", "mob.guardian.land.hit", "Custom")

    private val fullPartyNotification by BooleanSetting(
        "Full Party Notification",
        default = true,
        desc = "Notifies you when your party is full."
    )
    private val joinSound by BooleanSetting(
        "Party Join Sound",
        default = false,
        desc = "Plays a sound when someone joins your party."
    )

    private val soundDropdown by DropdownSetting("Sound Dropdown")
    private val waitForKick by BooleanSetting(
        "Wait for Kick",
        default = true,
        desc = "Waits to see if the player will be kicked before playing the sound."
    ).withDependency { joinSound && soundDropdown }
    private val sound by SelectorSetting(
        "Click Sound",
        "note.pling",
        defaultSounds,
        desc = "Which sound to play"
    ).withDependency { joinSound && soundDropdown }
    private val customSound by StringSetting("Custom Click Sound", "note.pling",
        desc = "Name of a custom sound to play. This is used when Custom is selected in the Sound setting.", length = 32
    ).withDependency { sound == defaultSounds.size - 1 && joinSound && soundDropdown }
    private val volume by NumberSetting(
        "Click Volume",
        1f,
        0,
        1,
        .01f,
        desc = "Volume of the sound."
    ).withDependency { joinSound && soundDropdown }
    private val pitch by NumberSetting(
        "Click Pitch",
        2f,
        0,
        2,
        .01f,
        desc = "Pitch of the sound."
    ).withDependency { joinSound && soundDropdown }
    val reset by ActionSetting(
        "Play click sound",
        desc = "Plays the sound with the current settings.",
        false,
        ::playCustomSound
    ).withDependency { joinSound && soundDropdown }

    private val statsDisplay by BooleanSetting(
        "Stats display",
        default = true,
        desc = "Displays stats of players who join your party"
    )

    private val floors = arrayListOf("Entrance", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7")
    private val floor by SelectorSetting(
        "Floor",
        defaultSelected = "Floor 7",
        floors,
        desc = "Determines which floor to check pb."
    )
    private val mmToggle by BooleanSetting("Master Mode", true, desc = "Use master mode times")

    private val autoKickDropdwon by DropdownSetting("Auto Kick Dropdown", default = true)
    private val informkicked by BooleanSetting(
        "Inform Kicked",
        default = false,
        desc = "Informs the player why they were kicked."
    ).withDependency { autoKickDropdwon }
    private val autokicktoggle by BooleanSetting(
        "Auto Kick",
        default = false,
        desc = "Automatically kicks players who don't meet requirements."
    ).withDependency { autoKickDropdwon }
    private val timeKick by BooleanSetting(
        "Check Time",
        default = false,
        desc = "Kicks for time"
    ).withDependency { autoKickDropdwon && autokicktoggle }
    private val timeMinutes by NumberSetting(
        "Minutes",
        5,
        0,
        10,
        desc = "Time minimum in minutes.",
        unit = "m"
    ).withDependency { timeKick && autoKickDropdwon && autokicktoggle }
    private val timeSeconds by NumberSetting(
        "Seconds",
        0,
        0,
        60,
        desc = "Time minimum in seconds.",
        unit = "s"
    ).withDependency { timeKick && autoKickDropdwon && autokicktoggle }
    private inline val timeReq get() = timeMinutes * 60 + timeSeconds

    private val secretKick by BooleanSetting(
        "Check Secrets",
        default = true,
        desc = "Kicks for secrets"
    ).withDependency { autoKickDropdwon && autokicktoggle }
    private val secretsreq by NumberSetting(
        "Secrets",
        0,
        0,
        200,
        desc = "Secret minimum in thousands.",
        unit = "k"
    ).withDependency { secretKick && autoKickDropdwon && autokicktoggle }

    private val savgKick by BooleanSetting(
        "Check Secret Average",
        default = false,
        desc = "Kicks for secret average."
    ).withDependency { autoKickDropdwon && autokicktoggle }
    private val savgreq by NumberSetting(
        "Secret Average",
        0f,
        0f,
        20f,
        desc = "Secret average minimum."
    ).withDependency { savgKick && autoKickDropdwon && autokicktoggle }

    private val weighted by BooleanSetting(
        "Weighted Secrets",
        default = false,
        desc = "Uses weighted secret average. Weight has 4 blood mobs count as 1 secret."
    ).withDependency { (savgKick || secretKick) && autoKickDropdwon && autokicktoggle }

    private val checkItems by BooleanSetting(
        "Check Items",
        default = false,
        desc = "Enables checking player items."
    ).withDependency { autoKickDropdwon && autokicktoggle }
    private val apiOffKick by BooleanSetting(
        "Api Off Kick",
        default = true,
        desc = "Kicks if the player's api is off. If this setting is disabled, it will ignore the item check when players have api disabled."
    ).withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val magicalPowerKick by BooleanSetting(
        "Check Magical Power",
        default = false,
        desc = "Kicks if the player doesn't have enough magical power."
    ).withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val magicalPowerReq by NumberSetting(
        "Magical Power",
        1300,
        0,
        2000,
        increment = 20,
        desc = "Magical power minimum."
    ).withDependency { magicalPowerKick && autoKickDropdwon && autokicktoggle && checkItems }
    private val witherImpactKick by BooleanSetting(
        "Wither Impact",
        default = false,
        desc = "Kicks if the player doesn't have wither impact."
    ).withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val dragKick by BooleanSetting(
        "Gdrag/Edrag",
        default = false,
        desc = "Kicks if the player doesn't have gdrag or edrag."
    ).withDependency { autoKickDropdwon && autokicktoggle && checkItems }
    private val spiritKick by BooleanSetting(
        "Spirit Pet",
        default = false,
        desc = "Kicks if the player doesn't have spirit pet."
    ).withDependency { autoKickDropdwon && autokicktoggle && checkItems }

    private val petMap = mapOf(
        "ENDER_DRAGON" to { dragKick },
        "SPIRIT" to { spiritKick },
        "GOLDEN_DRAGON" to { dragKick },
        "JELLYFISH" to { false },
    )

    private val kickCache by BooleanSetting(
        "Kick Cache",
        default = true,
        desc = "Caches kicked players to automatically kick when they attempt to rejoin."
    )
    private val action: () -> Unit by ActionSetting(
        "Clear Cache",
        desc = "Clears the kick list cache."
    ) { kickedList.clear() }.withDependency { kickCache }

    private val pfRegex =
        Regex("^Party Finder > (?:\\[.{1,7}])? ?(.{1,16}) joined the dungeon group! \\(.*\\)$") //https://regex101.com/r/XYnAVm/2
    private val kickRegex = Regex("^(?:\\[.{1,7}])? ?(.{1,16}) has been removed from the party\\.$")

    private val kickedList = mutableListOf<String>()

    val importantItems: MutableList<String> by ListSetting("itememsmemsmsms", mutableListOf())

    init {
        onMessage(
            Regex("^Party Finder > Your dungeon group is full! Click here to warp to the dungeon!$"),
            { enabled && fullPartyNotification }) {
            alert("§eYour party is full!")
        }

        onMessage(pfRegex, { enabled && statsDisplay && !autokicktoggle }) { matchResult ->
            val name = matchResult.groupValues[1].takeUnless { it == mc.session.username } ?: return@onMessage
            if (joinSound) playCustomSound()

            launch {
                val profiles = getProfile(name).getOrElse { return@launch modMessage(it.message) }
                profiles.memberData?.let { displayDungeonData(it, profiles.name, petMap.keys, floor) }
                    ?: return@launch toastMessage(
                        "Failed to load $name",
                        "$name either doesn't exist or hasn't joined skyblock!"
                    )
            }
        }


        onMessage(pfRegex, { enabled && autokicktoggle }) { matchResult ->
            val name = matchResult.groupValues[1].takeUnless { it == mc.session.username } ?: return@onMessage
            Logger.info("$name is being searched")

            if (kickedList.contains(name) && kickCache) {
                sendCommand("party kick $name")
                modMessage("Kicked $name since they have been kicked previously.")
                return@onMessage
            }

            if (joinSound && !waitForKick) playCustomSound()

            launch {
                val kickedReasons = mutableListOf<String>()

                val profiles = getProfile(name).getOrElse { return@launch modMessage(it.message) }
                val currentProfile = profiles.memberData
                    ?: return@launch toastMessage(
                        "Failed to load $name",
                        "$name either doesn't exist or hasn't joined skyblock!"
                    )

                val dungeon = if (!mmToggle) currentProfile.dungeons.dungeonTypes.catacombs else currentProfile.dungeons.dungeonTypes.mastermode
                dungeon.fastestTimeSPlus["$floor"]?.times(0.001)?.let {
                    if (!timeKick) return@let
                    if (it > (timeReq))
                        kickedReasons.add(
                            "Did not meet time req for ${if (mmToggle) "m" else "f"}$floor: ${
                                secondsToMinutes(
                                    it
                                )
                            }/${secondsToMinutes(timeReq)}"
                        )
                } ?: kickedReasons.add("Couldn't confirm completion status for ${if (mmToggle) "m" else "f"}$floor")

                val secretCount =
                    currentProfile.dungeons.secrets.let { if (weighted) currentProfile.playerStats.bloodMobKills / 4 + it else it }
                secretCount.let {
                    if (!secretKick) return@let
                    if (it < (secretsreq * 1000)) kickedReasons.add("Did not meet secret req: ${it}/$secretsreq")
                }

                val mmComps = (currentProfile.dungeons.dungeonTypes.mastermode.tierComps.without("total")).values.sum()
                val floorComps =
                    (currentProfile.dungeons.dungeonTypes.mastermode.tierComps.without("total")).values.sum()
                ((secretCount.toDouble()/(mmComps + floorComps).toDouble()).toFloat()).let {
                    if (!savgKick) return@let
                    if (it < savgreq) kickedReasons.add("Did not meet savg req: ${it.round(2)}/$savgreq")
                }

                if (checkItems) {
                    val pets = currentProfile.pets.pets.mapNotNullTo(HashSet()) { if (it.tier != "LEGENDARY") null else it.type }

                    for (entry in petMap) if (entry.value() && entry.key !in pets) kickedReasons.add("Did not have legendary ${entry.key.formatted}")

                    if (currentProfile.inventoryApi) {
                        if (witherImpactKick && currentProfile.allItems.none { it?.lore?.any { it.noControlCodes.matches(witherImpactRegex) } == true }) kickedReasons.add("Did not have wither impact")

                        val mp = currentProfile.magicalPower
                        if (magicalPowerKick && mp < magicalPowerReq) kickedReasons.add("Did not meet mp req: ${mp}/$magicalPowerReq")
                    } else if (apiOffKick) kickedReasons.add("Inventory API is off")
                }

                if (kickedReasons.isNotEmpty()) {
                    if (informkicked) {
                        runIn(5) { sendCommand("party kick $name") }
                        partyMessage("Kicked $name for: ${kickedReasons.joinToString(", ")}")
                    } else sendCommand("party kick $name")

                    return@launch modMessage("Kicking $name for: \n${kickedReasons.joinToString(" \n")}")
                }

                if (joinSound && waitForKick) playCustomSound()
                displayDungeonData(currentProfile, profiles.name, petMap.keys, floor)
            }
        }

        onMessage(kickRegex, { kickCache && enabled }) { message ->
            message.groupValues[1].takeUnless { name -> kickedList.contains(name) }?.let { name -> kickedList.add(name) }
        }
    }

    private fun playCustomSound() = PlayerUtils.playLoudSound(if (sound == defaultSounds.size - 1) customSound else defaultSounds[sound], volume, pitch)
}