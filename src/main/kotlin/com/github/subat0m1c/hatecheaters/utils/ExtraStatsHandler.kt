package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.utils.noControlCodes
import me.odinmain.utils.runIn
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import me.odinmain.utils.skyblock.sendCommand
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ExtraStatsHandler {
    private var expectingStats = false
    private var receivedStats = false

    fun waitForOtherMods() {
        runIn(15, true) {
            if (receivedStats) return@runIn
            expectingStats = true
            sendCommand("showextrastats")
            runIn(5, true) { expectingStats = false }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        receivedStats = false
    }

    @SubscribeEvent
    fun onChatMessage(event: ClientChatReceivedEvent) = with(event.message.unformattedText) {
        if (!DungeonUtils.inDungeons) return
        if ((hideButDontCheck.any { it.matches(noControlCodes) } || blank.matches(this)) && expectingStats) event.isCanceled = true
        if (regexes.none { it.matches(noControlCodes) }) return@with
        if (expectingStats) event.isCanceled = true
        else receivedStats = true
    }

    private val blank = Regex("§r")
    private val hideButDontCheck = listOf(
        Regex("^\\s*Team Score: \\d+ \\(.{1,2}\\)\\s?(?:\\(NEW RECORD!\\))?\$"),
        Regex("^\\s*☠ Defeated (.+) in 0?([\\dhms ]+?)\\s*(\\(NEW RECORD!\\))?\$"),
        Regex("^\\s*Deaths: \\d+\$"),
        Regex("^▬+\$"),
        Regex("^ {7}$"),
    )

    private val regexes = listOf(
        Regex("^\\s*The Catacombs - .+ Stats\$"),
        Regex("^\\s*Master Mode Catacombs - .+ Stats\$"),
        Regex("^\\s*Master Mode The Catacombs - .+ Stats\$"),
        Regex("^\\s*Total Damage as .+: [\\d,.]+\\s?(?:\\(NEW RECORD!\\))?\$"),
        Regex("^\\s*Ally Healing: [\\d,.]+\\s?(?:\\(NEW RECORD!\\))?\$"),
        Regex("^\\s*Enemies Killed: \\d+\\s?(?:\\(NEW RECORD!\\))?\$"),
        Regex("^\\s*Secrets Found: \\d+\$"),
    )
}