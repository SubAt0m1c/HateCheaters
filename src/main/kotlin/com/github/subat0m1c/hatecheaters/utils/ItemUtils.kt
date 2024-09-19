package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.utils.render.Color
import me.odinmain.utils.skyblock.*
import net.minecraft.item.ItemStack

object ItemUtils {

    val ItemStack?.getMagicalPower: Int get() {
        var baseMp = mpMap[getRarity(this?.lore ?: emptyList())] ?: 0
        if (this.itemID == "HEGEMONY_ARTIFACT") baseMp *= 2
        return baseMp
    }

    private val mpMap = mapOf(
        ItemRarity.COMMON to 3,
        ItemRarity.UNCOMMON to 5,
        ItemRarity.RARE to 8,
        ItemRarity.EPIC to 12,
        ItemRarity.LEGENDARY to 16,
        ItemRarity.MYTHIC to 22,
        ItemRarity.SPECIAL to 3,
        ItemRarity.VERY_SPECIAL to 5
    )

    /**
     * previously broken in odin.
     * remaining until odin real 1.2.5.beta8 release since most beta8 users are before this was fixed.
     *
     * Taken and modified from [Odin](https://github.com/odtheking/Odin) under [BSD-3](https://github.com/odtheking/Odin/blob/main/LICENSE).
     */
    enum class ItemRarity(
        val loreName: String,
        val colorCode: String,
        val color: Color
    ) {
        COMMON("COMMON", "§f", Color.WHITE),
        UNCOMMON("UNCOMMON", "§2", Color.GREEN),
        RARE("RARE", "§9", Color.BLUE),
        EPIC("EPIC", "§5", Color.PURPLE),
        LEGENDARY("LEGENDARY", "§6", Color.ORANGE),
        MYTHIC("MYTHIC", "§d", Color.MAGENTA),
        DIVINE("DIVINE", "§b", Color.CYAN),
        SPECIAL("SPECIAL", "§c", Color.RED),
        VERY_SPECIAL("VERY SPECIAL", "§c", Color.RED);
    }

    private val rarityRegex: Regex = Regex("§l(?<rarity>${ItemRarity.entries.joinToString("|") { it.loreName }}) ?(?<type>[A-Z ]+)?(?:§[0-9a-f]§l§ka)?$")

    /**
     * Gets the rarity of an item
     *
     * Taken from [Odin](https://github.com/odtheking/Odin) under [BSD-3](https://github.com/odtheking/Odin/blob/main/LICENSE).
     *
     * @param lore Lore of an item
     * @return ItemRarity or null if not found
     */
    fun getRarity(lore: List<String>): ItemRarity? {
        // Start from the end since the rarity is usually the last line or one of the last.
        for (i in lore.indices.reversed()) {
            val match = rarityRegex.find(lore[i]) ?: continue
            val rarity: String = match.groups["rarity"]?.value ?: continue
            return ItemRarity.entries.find { it.loreName == rarity }
        }
        return null
    }
}