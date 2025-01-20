package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launchDeferred
import com.github.subat0m1c.hatecheaters.events.impl.LoadDungeonPlayers
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.chatConstructor
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.apiutils.ParseUtils.getSecrets
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.utils.equalsOneOf
import me.odinmain.utils.runIn
import me.odinmain.utils.skyblock.dungeon.DungeonPlayer
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ClearSecrets : Module(
    "Clear Secrets",
    description = "Displays each team members secrets on run complete.",
    category = Category.DUNGEON
) {
    private val secretMap = hashMapOf<Teammate, Deferred<Long>>()
    private inline val teammates get() = DungeonUtils.dungeonTeammates.map { it.asTeammate() }

    data class Teammate(val name: String, val uuid: String?, var dungeonPlayer: DungeonPlayer) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Teammate) return false

            return name == other.name && uuid == other.uuid
        }

        override fun hashCode(): Int = 31 * name.hashCode() + (uuid?.hashCode() ?: 0)
    }

    init {
        onMessage(Regex("^\\s*(Master Mode)? ?(?:The)? Catacombs - (Entrance|Floor .{1,3})\$")) {
            secretMap.keys.retainAll(teammates.toSet())
            runIn(30, true) { // bm waits a bit, figure I should as well? not sure.
                launch {
                    secretMap.entries.map { (k, v) ->
                        val new = async {
                            getSecrets(k.name, k.uuid)
                                .onFailure { modMessage("Failed to get secrets for ${k.name}: ${it.message}") }
                                .getOrDefault(-1)
                        }.also { new -> secretMap[k] = new }.await()
                        val old = v.await()

                        val dif = (new - old).takeUnless { (-1L).equalsOneOf(old, new) }
                        "\n§bH§3C §7|| §d${k.name} §7-> §fSecrets: §6${dif ?: "§c§l???§r"}§7, §fDeaths: §c${k.dungeonPlayer.deaths}"
                    }.let {
                        chatConstructor {
                            displayText("§bH§3C §7|| §fClear Secrets:")
                            it.forEach { text ->
                                clickText(text, "/hcdev writetoclipboard $text", listOf("§e§lCLICK §r§7to copy!"))
                            }
                        }.print()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onDungeonLoad(event: LoadDungeonPlayers)  {
        if (event.teammates.isEmpty()) return
        event.teammates.forEach {
            secretMap.computeIfAbsent(it.asTeammate()) { teammate ->
                launchDeferred {
                    getSecrets(teammate.name, teammate.uuid)
                        .onFailure { modMessage("Failed to get secrets for ${teammate.name}: ${it.message}") }
                        .getOrDefault(-1)
                }
            }
        }
    }

    private fun DungeonPlayer.asTeammate() = Teammate(name, entity?.uniqueID?.toString(), this)
}