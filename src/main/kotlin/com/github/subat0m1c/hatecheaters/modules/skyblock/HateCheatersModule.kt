package com.github.subat0m1c.hatecheaters.modules.skyblock

import com.github.subat0m1c.hatecheaters.utils.networkutils.CheckUpdate
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastRenderer
import io.netty.buffer.Unpooled
import me.odinmain.events.impl.PacketEvent
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.SelectorSetting
import me.odinmain.features.settings.impl.StringSetting
import me.odinmain.utils.skyblock.modMessage
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityPig
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove
import net.minecraft.network.play.server.S20PacketEntityProperties
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraft.util.ChatComponentScore
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

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
//        val packet = event.packet as? S2FPacketSetSlot ?: return
//        val windowId = packet.func_149175_c()
//        val slot = packet.func_149173_d()
//        val itemStack = packet.func_149174_e()
//
//        val newPacket = S2FPacketSetSlot(
//            windowId,
//            slot ,
//            itemStack
//        )
//
//        val buf = PacketBuffer(Unpooled.buffer())
//        val packetId = try {
//            EnumConnectionState.PLAY.getPacketId(EnumPacketDirection.SERVERBOUND, event.packet)
//        } catch (e: NullPointerException) {
//            println("${event.packet::class.java.simpleName} failed to get packet id")
//            null
//        }
//
//        packetId?.let {
//            buf.writeVarIntToBuffer(it)
//        }
//        newPacket.writePacketData(buf)
//
//        val testString = StringBuilder()
//        for (i in 0 until buf.readableBytes()) {
//            testString.append(String.format("%02X ", buf.getByte(i)))
//        }
//
//        println("Recieved and made a packet: $testString")
//    }
}