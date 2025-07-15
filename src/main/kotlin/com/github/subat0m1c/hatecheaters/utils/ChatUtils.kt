package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule.debugMessages
import com.github.subat0m1c.hatecheaters.modules.skyblock.HateCheatersModule.toasts
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Text
import com.github.subat0m1c.hatecheaters.utils.toasts.ToastManager.toaster
import me.odinmain.OdinMain.mc
import me.odinmain.utils.runOnMCThread
import me.odinmain.utils.skyblock.createClickStyle
import me.odinmain.utils.toFixed
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import me.odinmain.utils.skyblock.modMessage as odinModMessage

object ChatUtils {

    fun toastMessage(title: String, message: Any?, includeTitle: Boolean = false) {
        if (toasts) toaster(title, message.toString())
        else modMessage(message?.let { if (includeTitle) "${title}§7: $it" else it })
    }

    fun modMessage(message: Any?, prefix: String = "§bH§3C §8»§r ", chatStyle: ChatStyle? = null) {
        odinModMessage(message, prefix, chatStyle)
        Logger.log.info("Messaged >> $message")
    }

    fun debug(message: Any?) {
        if (debugMessages) modMessage(message, "§bH§3C§1Debug §8::§r ")
    }

    fun chatConstructor(chat: ChatDSL.() -> Unit): ChatDSL = ChatDSL().apply(chat)

    class ChatDSL {
        private val chat = ChatComponentText("")
        val message get() = chat

        fun hoverText(text: String, hoverText: List<String>): IChatComponent =
            chat.appendSibling(ChatComponentText(text).apply { chatStyle.setHover(hoverText) })

        fun clickText(text: String, command: String, hoverText: List<String> = emptyList(), chatStyle: ClickEvent.Action = ClickEvent.Action.RUN_COMMAND): IChatComponent =
            chat.appendSibling(ChatComponentText(text).apply { setChatStyle(createClickStyle(chatStyle, command).setHover(hoverText)) })

        fun displayText(text: String = "\n", chatStyle: ChatStyle? = null): IChatComponent =
            chat.appendSibling(ChatComponentText(text).apply { setChatStyle(chatStyle) })

        fun print() = runOnMCThread { mc.thePlayer?.addChatMessage(chat) }
    }

    fun ChatStyle.setHover(text: List<String>): ChatStyle =
        this.setChatHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT,  ChatComponentText(text.joinToString("\n"))))

    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar(Char::titlecase)
    }

    fun secondsToMinutes(totalSeconds: Number): String {
        val minutes = floor(totalSeconds.toDouble()/60.0).toInt()
        val seconds = (totalSeconds.toDouble()%60.0).toInt()
        return String.format("%1d:%02d", minutes, seconds)
    }

    fun getCurrentDateTimeString(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))

    fun String.colorizeNumber(max: Number): String {
        val number = this.replace(",", "").toDoubleOrNull() ?: return this
        return "${number.colorCode(max)}$this"
    }

    fun Number.colorCode(max: Number): String {
        val number = this.toDouble()
        val maxDouble = max.toDouble()

        return when {
            number >= maxDouble        -> "§b"
            number >= maxDouble * 0.9  -> "§c"
            number >= maxDouble * 0.75 -> "§d"
            number >= maxDouble * 0.65 -> "§6"
            number >= maxDouble * 0.50 -> "§5"
            number >= maxDouble * 0.25 -> "§9"
            number >= maxDouble * 0.10 -> "§a"
            else -> "§f"
        }
    }

    fun Number.colorize(max: Number): String = "${this.colorCode(max)}$this"

    inline val String.textWidth get() = Text.getWidth(this)

    inline val String.colorClass: String get() =
        when (this.lowercase()) {
            "berserk" -> "§c$this"
            "archer"  -> "§6$this"
            "healer"  -> "§d$this"
            "mage"    -> "§b$this"
            "tank"    -> "§7$this"
            else      -> this
        }

    inline val Number.commas: String get() =
        this.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

    inline val String.colorStat: String get() = when (this.lowercase().replace(" ", "_")) {
        "health"          -> "§c$this"
        "defense"         -> "§a$this"
        "walk_speed" -> "§f$this"
        "strength"        -> "§c$this"
        "critical_chance" -> "§9$this"
        "critical_damage" -> "§9$this"
        "attack_speed"    -> "§e$this"
        "intelligence"    -> "§b$this"
        else              -> this
    }

    fun Double.truncate(decimals: Int = 0) = when {
        this >= 1_000_000_000 -> "${(this / 1_000_000_000).toFixed(decimals)}b"
        this >= 1_000_000 -> "${(this / 1_000_000).toFixed(decimals)}m"
        this >= 1_000 -> "${(this / 1_000).toFixed(decimals)}k"
        else -> this.toFixed(decimals)
    }

    inline val Double.truncate: String get() = when {
        this >= 1_000_000_000 -> "${(this / 1_000_000_000).toFixed(2)}b"
        this >= 1_000_000     -> "${(this.toInt() / 1_000_000)}m"
        this >= 1_000         -> "${(this.toInt() / 1_000)}k"
        else                  -> "${this.toInt()}"
    }
}
