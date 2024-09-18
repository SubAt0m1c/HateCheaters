package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import me.odinmain.OdinMain.mc
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.EnumChatFormatting
import kotlin.math.floor

object ChatUtils {

    /**
     * hippity hoppity odins modmessage is now my property and slightly modified
     */
    fun modMessage(message: Any?, prefix: Boolean = true, chatStyle: ChatStyle? = null) {
        val chatComponent = ChatComponentText(if (prefix) "§bH§3C §8»§r $message" else message.toString())
        chatStyle?.let { chatComponent.setChatStyle(it) } // Set chat style using setChatStyle method
        try { mc.thePlayer?.addChatMessage(chatComponent) }
        catch (e: Exception) { println("Error sending message: ${e.message}")
        }
    }

    fun createHoverStyle(action: HoverEvent.Action, text: String): ChatStyle {
        return ChatStyle().apply {
            chatHoverEvent = HoverEvent(
                action, ChatComponentText(text)
            )
        }
    }

    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar(Char::titlecase)
    }

    val Double.short: Double get() = String.format("%.2f", this).toDouble()

    fun secondsToMinutes(totalSeconds: Number): String {
        val minutes = floor(totalSeconds.toDouble()/60.0).toInt()
        val seconds = (totalSeconds.toDouble()%60.0).toInt()
        return String.format("%1d:%02d", minutes, seconds)
    }

    fun ChatComponentText.add(text: String, event: ChatStyle? = null) {
        val componentText = ChatComponentText(text)
        event?.let { componentText.setChatStyle(it) }
        this.appendSibling(componentText)
    }

    fun ChatComponentText.addHoverText(text: String, hoverText: String) {
        val componentText = ChatComponentText(text)
        componentText.setChatStyle(createHoverStyle(HoverEvent.Action.SHOW_TEXT, hoverText))
        this.appendSibling(componentText)
    }

    fun ChatComponentText.print() {
        try {
            mc.thePlayer.addChatMessage(this)
        } catch (e: Exception) {
            modMessage("Error sending chat message: ${e.message}")
        }
    }
}