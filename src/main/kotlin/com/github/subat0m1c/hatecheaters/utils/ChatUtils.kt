package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.ProfileViewer
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.getMCTextWidth
import me.odinmain.utils.skyblock.createClickStyle
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

object ChatUtils {

    /**
     * Sends a client-side message with an optional prefix.
     *
     * Taken and modified from [Odin](https://github.com/odtheking/Odin) under [BSD-3](https://github.com/odtheking/Odin/blob/main/LICENSE).
     *
     * @param message Message to be sent.
     * @param prefix If `true`, adds a prefix to the message.
     * @param chatStyle Optional chat style to be applied to the message.
     */
    fun modMessage(message: Any?, prefix: Boolean = true, chatStyle: ChatStyle? = null) {
        val chatComponent = ChatComponentText(if (prefix) "§bH§3C §8»§r $message" else message.toString())
        chatStyle?.let { chatComponent.setChatStyle(it) } // Set chat style using setChatStyle method
        logger.info("Messaged >> $message")
        try { mc.thePlayer?.addChatMessage(chatComponent) }
        catch (e: Exception) { logger.warning("Error sending message: ${e.message}")
        }
    }


    fun chatConstructor(chat: ChatDSL.() -> Unit): ChatDSL = ChatDSL().apply(chat)

    class ChatDSL {
        private val chat = ChatComponentText("")

        fun hoverText(text: String, hoverText: List<String>) {
            chat.appendSibling(ChatComponentText(text).apply { this.setChatStyle(createHoverStyle(HoverEvent.Action.SHOW_TEXT, hoverText.joinToString("\n"))) })
        }

        fun clickText(text: String, command: String, hoverText: List<String> = emptyList(), chatStyle: ClickEvent.Action = ClickEvent.Action.RUN_COMMAND) {
            chat.appendSibling(
                ChatComponentText(text).apply { this.setChatStyle(createClickStyle(chatStyle, command).setHover(hoverText)) }
            )
        }

        fun displayText(text: String = "\n", chatStyle: ChatStyle? = null) {
            chat.appendSibling(ChatComponentText(text).apply { this.setChatStyle(chatStyle) })
        }

        fun String.displayText(chatStyle: ChatStyle? = null) {
            displayText(this, chatStyle)
        }

        fun String.clickText(command: String, hoverText: List<String> = emptyList(), chatStyle: ClickEvent.Action = ClickEvent.Action.RUN_COMMAND) {
            clickText(this, command, hoverText, chatStyle)
        }

        fun String.hoverText(hoverText: List<String>) {
            hoverText(this, hoverText)
        }

        val message get() = chat

        fun print() =
            try {
                mc.thePlayer.addChatMessage(this.chat)
            } catch (e: Exception) {
                logger.warning("Error sending chat message: ${e.message}")
            }
    }

    fun ChatStyle.setHover(text: List<String>): ChatStyle =
        this.setChatHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT,  ChatComponentText(text.joinToString("\n"))))

    fun ChatStyle.setClick(value: String, clickAction: ClickEvent.Action = ClickEvent.Action.RUN_COMMAND): ChatStyle =
        this.setChatClickEvent(ClickEvent(clickAction,  value))

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

    fun secondsToMinutes(totalSeconds: Number): String {
        val minutes = floor(totalSeconds.toDouble()/60.0).toInt()
        val seconds = (totalSeconds.toDouble()%60.0).toInt()
        return String.format("%1d:%02d", minutes, seconds)
    }

    fun getCurrentDateTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(formatter)
    }


    fun String.colorizeNumber(max: Number): String {
        val number = this.replace(",", "").toDouble()
        val maxDouble = max.toDouble()
        val color = when {
            number >= maxDouble -> "§b"
            number >= (maxDouble*0.9) -> "§c"
            number >= (maxDouble*0.75) -> "§d"
            number >= (maxDouble*0.65) -> "§6"
            number >= (maxDouble*0.50) -> "§5"
            number >= (maxDouble*0.25) -> "§9"
            number >= (maxDouble*0.1) -> "§a"
            else -> "§f"
        }
        return "$color$this"
    }

    fun Number.colorize(max: Number): String {
        val double = this.toDouble()
        val maxDouble = max.toDouble()
        val color = when {
            double >= maxDouble -> "§b"
            double >= (maxDouble*0.9) -> "§c"
            double >= (maxDouble*0.75) -> "§d"
            double >= (maxDouble*0.65) -> "§6"
            double >= (maxDouble*0.50) -> "§5"
            double >= (maxDouble*0.25) -> "§9"
            double >= (maxDouble*0.1) -> "§a"
            else -> "§f"
        }
        return "$color$this"
    }

    val String.mcWidth get() = getMCTextWidth(this)

    val String.colorClass: String get() = when (this.lowercase()) {
        "archer" -> "§6$this"
        "mage" -> "§b$this"
        "berserk" -> "§c$this"
        "healer" -> "§d$this"
        "tank" -> "§7$this"
        else -> this
    }

    val Number.commas: String get() {
        return toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }

    val String.colorStat: String get() = when (this.lowercase().replace(" ", "_")) {
        "health" -> "§c$this"
        "defense" -> "§a$this"
        "walk_speed" -> "§${ProfileViewer.currentTheme.fontCode}$this"
        "strength" -> "§c$this"
        "critical_chance" -> "§9$this"
        "critical_damage" -> "§9$this"
        "attack_speed" -> "§e$this"
        "intelligence" -> "§b$this"
        else -> this
    }

    val Double.truncate: String get() = when {
        this >= 1_000_000_000 -> "${String.format("%.2f", this / 1_000_000_000)}b"
        this >= 1_000_000 -> "${(this.toInt() / 1_000_000)}m"
        this >= 1_000 -> "${(this.toInt() / 1_000)}k"
        else -> "${this.toInt()}"
    }
}