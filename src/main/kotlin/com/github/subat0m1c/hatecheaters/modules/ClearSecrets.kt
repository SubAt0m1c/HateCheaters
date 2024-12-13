package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSecrets
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.utils.runIn
import me.odinmain.utils.skyblock.dungeon.DungeonPlayer
import me.odinmain.utils.skyblock.dungeon.DungeonUtils.dungeonTeammates
import me.odinmain.utils.skyblock.dungeon.DungeonUtils.dungeonTeammatesNoSelf

object ClearSecrets : Module(
    "Clear Secrets",
    description = "Displays each team members secrets on run complete.",
    category = Category.DUNGEON
) {
    private var secretMap = arrayListOf<Teammate>()

    data class Teammate(val name: String, val uuid: String?, var dungeonPlayer: DungeonPlayer, var secrets: Long = -1) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Teammate) return false

            return name == other.name && uuid == other.uuid
        }

        override fun hashCode(): Int = 31 * name.hashCode() + (uuid?.hashCode() ?: 0)
    }

    init {
        onMessage(Regex("^\\s*(Master Mode)? ?(?:The)? Catacombs - (Entrance|Floor .{1,3})\$")) {
            Logger.message(it)
            runIn(30, true) { // bm waits a bit, figure I should as well? not sure.
                launch {
                    secretMap.map {
                        val new = getSecrets(it.name, it.uuid)
                            .onFailure { modMessage("Failed to get secrets for $name: ${it.message}") }
                            .getOrDefault(-1)

                        val dif = new - it.secrets
                        it.secrets = new

                        it to dif
                    }.joinToString("\n") { (it, dif) ->
                        "§bH§3C §7|| §d${it.name} §7-> §fSecrets: §6${dif}§7, §fDeaths: §c${it.dungeonPlayer.deaths}"
                    }.let { modMessage(it, "") }
                }
            }
        }

        onMessage(Regex("\\[NPC] Mort: Here, I found this map when I first entered the dungeon\\.")) {
            Logger.message(it)
            modMessage("test")
            runIn(1, true) { //runin to give odin time to grab data
                if (dungeonTeammates.isEmpty()) return@runIn modMessage("Couldn't load dungeon teammates.")
                val teammates = dungeonTeammatesNoSelf.map { Teammate(it.name, it.entity?.uniqueID?.toString(), it) }
                secretMap = secretMap.updateList(teammates)
                launch {
                    secretMap.forEach { teammate ->
                        if (teammate.secrets == -1L) teammate.secrets = getSecrets(teammate.name, teammate.uuid).onFailure { modMessage("Failed to get secrets for $name: ${it.message}") }.getOrDefault(-1)
                        teammates.find { it == teammate }?.dungeonPlayer?.let { teammate.dungeonPlayer = it }
                    }
                }
            }
        }
    }

    private fun <T, L : Collection<T>> ArrayList<T>.updateList(other: L): ArrayList<T> {
        return ArrayList(other.map { element ->
            this.find { it == element } ?: element
        })
    }
}