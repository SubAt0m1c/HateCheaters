package com.github.subat0m1c.hatecheaters.modules.skyblock

import com.github.subat0m1c.hatecheaters.utils.networkutils.CheckUpdate
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastRenderer
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.SelectorSetting
import me.odinmain.features.settings.impl.StringSetting

@AlwaysActive
object HateCheatersModule : Module(
    name = "Hate Cheaters",
    desc = "Hate cheaters nonsense",
) {
    val toasts by BooleanSetting("Toasts", default = false, desc = "Enable toasts for various notifications.")
    val toastCorner by SelectorSetting(
        "Toast Corner",
        "Bottom Right",
        ArrayList(ToastRenderer.ToastCorner.entries.map { it.displayName }),
        desc = "The corner the toasts will be in."
    ).withDependency { toasts }

    // val logJson by BooleanSetting("Log Json", default = false, desc = "Logs requested json data to Config/hatecheaters/json_logs")
    val debugMessages by BooleanSetting(
        "Debug messages",
        default = false,
        desc = "Prints debug messages in your chat instead of needing to open logs."
    )
    private val checkUpdates by BooleanSetting(
        "Update Checker",
        default = true,
        desc = "Checks GitHub for latest HateCheaters releases and notifies you if you are on an old version."
    )
    val server by StringSetting(
        "Api Server",
        default = "default",
        hidden = false,
        desc = "Server to be used to connect to the api. set to \"default\" to use the default. Only change if you know what you're doing. format: \"subdomain.domain.tld\""
    )

    init {
        execute(250) {
            if (!checkUpdates) destroyExecutor()
            mc.thePlayer?.let {
                CheckUpdate.lookForUpdates()
                destroyExecutor()
            }
        }
    }

    // ignore please thnakjs
//    @SubscribeEvent
//    fun onPacket(event: PacketEvent.Send) {
//        val buf = PacketBuffer(Unpooled.buffer())
//        val packetId = try {
//            EnumConnectionState.PLAY.getPacketId(EnumPacketDirection.SERVERBOUND, event.packet)
//        } catch (e: NullPointerException) {
//            println("${event.packet::class.java.simpleName} failed to get packet id")
//            null
//        }
//
//        val test = EntityZombie
//
//        packetId?.let {
//            buf.writeVarIntToBuffer(it)
//        }
//        event.packet.writePacketData(buf)
//
//
//        val testString = StringBuilder()
//        for (i in 0 until buf.readableBytes()) {
//            testString.append(String.format("%02X ", buf.getByte(i)))
//        }
//
//        //println("sent packet: ${event.packet::class.java.simpleName} with id: ${packetId}: $testString")
//    }
//
//    @SubscribeEvent
//    fun onPacketReceive(event: PacketEvent.Receive) {
//        val packet = event.packet as? S38PacketPlayerListItem ?: return
//        modMessage(
//            """
//            Player List Packet:
//            Action: ${packet.action}
//            Entries: ${
//                packet.entries.joinToString("\n") {
//                    """
//                    {
//                        name: ${it?.profile?.name},
//                        id: ${it?.profile?.id},
//                        gameMode: ${it?.gameMode},
//                        ping: ${it?.ping},
//                        displayName: ${it?.displayName}
//                        other: ${
//                        it?.profile?.properties?.entries()?.joinToString("\n") { (key, value) ->
//                            """
//                                {
//                                    key: $key
//                                    property: {
//                                        name: ${value.name}
//                                        value: ${value.value}
//                                        sig: ${value.signature}
//                                    }
//                                }
//                            """.trimIndent()
//                        }
//                    }
//                    }
//                """.trimIndent()
//                }
//            }
//        """.trimIndent()
//        )
//    }

//        if (event.packet !is S3BPacketScoreboardObjective && event.packet !is S3CPacketUpdateScore && event.packet !is S3EPacketTeams) return
//        if (event.packet is S3BPacketScoreboardObjective) {
//            val packet = event.packet as? S3BPacketScoreboardObjective ?: return
//
//            LogHandler.Logger.info("objective packet: scoreboard name: ${packet.func_149339_c()}, object value: ${packet.func_149337_d()}")
//        }
//
//        if (event.packet is S3CPacketUpdateScore) {
//            val packet = event.packet as? S3CPacketUpdateScore ?: return
//
//            LogHandler.Logger.info("Update score packet: score: ${packet.objectiveName} text: ${packet.playerName}")
//        }
//
//        if (event.packet is S3EPacketTeams) {
//            val packet = event.packet as? S3EPacketTeams ?: return
//
//            LogHandler.Logger.info("""
//                Teams packet:
//                team name: ${packet.name},
//                display name: ${packet.displayName}
//                suffix: ${packet.suffix},
//                prefix: ${packet.prefix},
//                action: ${packet.action},
//                friendly fire: ${packet.friendlyFlags},
//                name tag visibility: ${packet.nameTagVisibility},
//                players: ${packet.players},
//                color: ${packet.color}
//            """.trimIndent())
//        }

//        val packet = event.packet as? S47PacketPlayerListHeaderFooter ?: return
//        println("header: ${packet.header}, footer: ${packet.footer}")
//        if (packet.action != S38PacketPlayerListItem.Action.ADD_PLAYER) return
//
//        val buf = PacketBuffer(Unpooled.buffer())
//        val packetId = try {
//            EnumConnectionState.PLAY.getPacketId(EnumPacketDirection.CLIENTBOUND, packet)
//        } catch (e: NullPointerException) {
//            println("${event.packet::class.java.simpleName} failed to get packet id")
//            null
//        }
//
//        packetId?.let {
//            buf.writeVarIntToBuffer(it)
//        }
//        packet.writePacketData(buf)
//
//        val payload = PacketBuffer(Unpooled.buffer())
//        payload.writeVarIntToBuffer(buf.readableBytes())
//        payload.writeBytes(buf)
//        println("readable bytes: ${payload.readableBytes()}")
//
//        val testString = StringBuilder()
//        for (i in 0 until payload.readableBytes()) {
//            testString.append(String.format("%02X ", payload.getByte(i)))
//        }
//
//        println("packet: $packet")
//
//        println("Recieved and made a packet: $testString")
}