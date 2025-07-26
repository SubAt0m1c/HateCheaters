package com.github.subat0m1c.hatecheaters.modules.dungeons

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launchDeferred
import com.github.subat0m1c.hatecheaters.events.impl.LoadDungeonPlayers
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.chatConstructor
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.ExtraStatsHandler
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getSecrets
import com.github.subat0m1c.hatecheaters.utils.networkutils.WebUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import me.odinmain.clickgui.settings.impl.BooleanSetting
import me.odinmain.features.Module
import me.odinmain.utils.noControlCodes
import me.odinmain.utils.runIn
import me.odinmain.utils.skyblock.dungeon.DungeonPlayer
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ClearSecrets : Module(
    "Clear Secrets",
    description = "Displays each team members secrets on run complete.",
) {
    private val compactMessage by BooleanSetting(
        "Compact Message",
        false,
        desc = "Shows teammate data in one line instead of multiple lines."
    )

    private inline val teammates get() = DungeonUtils.dungeonTeammates.map { it.asTeammate() }.toSet()
    private val teammateList = hashSetOf<Teammate>()
    private var ownSecrets: Long? = null

    init {
        onMessage(Regex("^\\s*(Master Mode)? ?(?:The)? Catacombs - (Entrance|Floor .{1,3})\$")) {
            teammateList.retainAll(teammates)
            runIn(30, true) { // bm waits a bit, figure I should as well? not sure.
                launch {
                    teammateList.map {
                        val secrets = it.calcSecrets()
                        if (compactMessage) "§d${it.name}: §6${secrets ?: "§c§l???§7"}"
                        else "§bH§3C §7|| §d${it.name} §7-> §fSecrets: §6${secrets ?: "§c§l???§r"}"
                    }.let {
                        chatConstructor {
                            it.forEachIndexed { i, text ->
                                if (compactMessage) clickText("${if (i == 0) "§bH§3C §7|| " else ", "}$text", "/hcdev writetoclipboard ${text.noControlCodes}", listOf("§e§lCLICK §r§7to copy!"))
                                else clickText("${if (i != 0) "\n" else ""}$text", "/hcdev writetoclipboard ${text.noControlCodes}", listOf("§e§lCLICK §r§7to copy!"))
                            }
                        }.print()
                    }
                }
            }
        }

        onMessage(Regex(" {29}> EXTRA STATS <")) { ExtraStatsHandler.waitForOtherMods() }

        onMessage(Regex("^\\s*Secrets Found: (\\d+)\$")) { ownSecrets = it.groupValues[1].toLongOrNull() ?: 0 }
    }

    @SubscribeEvent
    fun onDungeonLoad(event: LoadDungeonPlayers)  {
        if (event.teammates.isEmpty()) return
        event.teammates.forEach {
            val teammate = it.asTeammate()
            if (teammate in teammateList) return@forEach
            teammate.dungeonPlayer.entity?.uniqueID?.let { uuid ->
                WebUtils.UuidCache.addToCache(teammate.name, uuid.toString())
            }
            teammateList.add(teammate.apply { if (it.name != mc.thePlayer.name) pullSecrets() })
        }
    }

    private fun DungeonPlayer.asTeammate() = Teammate(name, entity?.uniqueID?.toString(), this)

    data class Teammate(val name: String, val uuid: String?, var dungeonPlayer: DungeonPlayer) {
        var secrets: Deferred<Long>? = null

        fun pullSecrets() {
            secrets = launchDeferred {
                getSecrets(name, uuid)
                    .onFailure { modMessage("Failed to get secrets for ${name}: ${it.message}") }
                    .getOrDefault(-1)
            }
        }

        suspend fun calcSecrets(): Long? {
            if (name == mc.session.username) return ownSecrets.also { ownSecrets = null }
            val old = secrets?.await() ?: -1
            val new = CompletableDeferred<Long>().apply {
                complete(
                    getSecrets(name, uuid)
                        .onFailure { modMessage("Failed to get secrets for ${name}: ${it.message}") }
                        .getOrDefault(-1)
                )
            }.also { new -> secrets = new }.await()
            return if (old == -1L || new == -1L) null else new - old
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Teammate) return false

            return name == other.name && uuid == other.uuid
        }

        override fun hashCode(): Int = 31 * name.hashCode() + (uuid?.hashCode() ?: 0)
    }
}