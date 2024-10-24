package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.utils.render.Color
import me.odinmain.utils.skyblock.*
import net.minecraft.item.ItemStack

object ItemUtils {

    val ItemStack?.getMagicalPower: Int get() {
        var baseMp = mpMap[getRarity(this?.lore ?: emptyList())] ?: 0
        if (this.skyblockID == "HEGEMONY_ARTIFACT") baseMp *= 2
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
}