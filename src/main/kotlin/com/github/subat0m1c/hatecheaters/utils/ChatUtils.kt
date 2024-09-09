package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.OdinMain.mc
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle

object ChatUtils {

    /**
     * hippity hoppity odins modmessage is now my property and slightly modified
     */
    fun modMessage(message: Any?, prefix: Boolean = true, chatStyle: ChatStyle? = null) {
        val chatComponent = ChatComponentText(if (prefix) "§bHC §8»§r $message" else message.toString())
        chatStyle?.let { chatComponent.setChatStyle(it) } // Set chat style using setChatStyle method
        try { mc.thePlayer?.addChatMessage(chatComponent) }
        catch (e: Exception) { println("Error sending message: ${e.message}")
        }
    }
}