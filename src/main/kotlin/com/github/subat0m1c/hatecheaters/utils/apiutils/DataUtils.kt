package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.Pet

object DataUtils {
    inline val maxMagicalPower get() = 1730

    val petItemRegex = Regex("(?:PET_ITEM_)?([A-Z_]+?)(?:_(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC))?")

    inline val Pet.petItem: String? get() =
        heldItem?.let { petItemRegex.matchEntire(it)?.destructured?.let { (heldItem, rarity) -> "${getRarityColor(rarity)}${heldItem.lowercase().replace("_", " ").capitalizeWords()}" } }

    inline val Pet.colorName: String get() = (getRarityColor(tier) + type.formatted)

    fun getRarityColor(rarity: String): String {
        return when (rarity) {
            "COMMON"    -> "§f"
            "UNCOMMON"  -> "§a"
            "RARE"      -> "§9"
            "EPIC"      -> "§5"
            "LEGENDARY" -> "§6"
            "MYTHIC"    -> "§d"
            else        -> "§r"
        }
    }
}