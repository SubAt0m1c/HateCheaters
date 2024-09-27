package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.c
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.addHoverText
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.colorize
import com.github.subat0m1c.hatecheaters.utils.LogHandler.logger
import com.ibm.icu.text.DecimalFormat
import me.odinmain.OdinMain.mc
import me.odinmain.utils.render.getMCTextWidth
import me.odinmain.utils.round
import me.odinmain.utils.skyblock.createClickStyle
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
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
        catch (e: Exception) { logger.warn("Error sending message: ${e.message}")
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

    fun ChatComponentText.addClickTextWithHover(text: String, hoverText: String, command: String) {
        val componentText = ChatComponentText(text)
        componentText.setChatStyle(createHoverStyle(HoverEvent.Action.SHOW_TEXT, hoverText))
        componentText.setChatStyle(createClickStyle(ClickEvent.Action.RUN_COMMAND, command))
        this.appendSibling(componentText)
    }

    fun getCurrentDateTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(formatter)
    }


    fun ChatComponentText.print() {
        try {
            mc.thePlayer.addChatMessage(this)
        } catch (e: Exception) {
            logger.error("Error sending chat message: ${e.message}")
        }
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
        "walk_speed" -> "§${c}$this"
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