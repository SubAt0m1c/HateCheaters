package com.github.subat0m1c.hatecheaters.utils

import me.odinmain.utils.skyblock.ItemRarity
import me.odinmain.utils.skyblock.getRarity
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.skyblockID
import net.minecraft.item.ItemStack

object ItemUtils {

    inline val ItemStack.getMagicalPower: Int get() {
        val baseMp = (mpMap[getRarity(this.lore)] ?: 0)
        return if (skyblockID == "HEGEMONY_ARTIFACT") baseMp * 2 else baseMp
    }

    val mpMap = mapOf(
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