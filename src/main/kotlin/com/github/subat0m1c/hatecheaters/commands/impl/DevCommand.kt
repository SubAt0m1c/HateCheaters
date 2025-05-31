package com.github.subat0m1c.hatecheaters.commands.impl

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.launch
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.networkutils.CheckUpdate
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelApi.getProfile
import com.github.subat0m1c.hatecheaters.utils.toasts.Toast
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastManager
import me.odinmain.OdinMain.mc
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.EntityTracker
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.ChatComponentText
import net.minecraft.util.Vec3
import java.util.UUID

val DevCommand = Commodore("hcdev") {
    literal("toast").runs { scale: Float?, message: GreedyString? ->
        ToastManager.addToast(
            Toast(
                "Test Toast",
                message?.string ?: "",
                textScale = scale ?: 1f
            )
        )
    }

    literal("checkupdate").runs {
        modMessage("Checking for updates...")
        CheckUpdate.lookForUpdates()
    }

    literal("apitest") {
        runs { name: String ->
            launch {
                getProfile(name)
                    .getOrElse { return@launch modMessage(it.message) }.memberData ?: return@launch modMessage("Could not find player data.")
                modMessage("Succeeded!")
            }
        }
    }

    literal("writetoclipboard").runs { text: GreedyString ->
        GuiScreen.setClipboardString(text.string)
        modMessage("Copied \"${text.string}\"!")
    }
}