package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.Pet
import me.odinmain.utils.skyblock.ItemRarity
import me.odinmain.utils.skyblock.getRarity
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.skyblockID
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import java.util.*

object ItemUtils {
    val petItemRegex = Regex("(?:PET_ITEM_)?([A-Z_]+?)(?:_(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC))?")
    val witherImpactRegex = Regex("(?:⦾ )?Ability: Wither Impact {2}RIGHT CLICK")

    inline val maxMagicalPower get() = 1800

    inline val Pet.petItem: String? get() =
        heldItem?.let { petItemRegex.matchEntire(it)?.destructured?.let { (heldItem, rarity) -> "${getRarityColor(rarity)}${heldItem.lowercase().replace("_", " ").capitalizeWords()}" } }

    inline val Pet.colorName: String get() = (getRarityColor(tier) + type.formatted)

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

    fun createCustomSkull(
        name: String,
        lore: List<String>,
        texture: String,
        stackSize: Int = 1
    ): ItemStack = ItemStack(Items.skull, stackSize, 3).apply {
        tagCompound = NBTTagCompound().apply {
            setTag("SkullOwner", NBTTagCompound().apply {
                setString("Id", UUID.randomUUID().toString())
                setString("Name", name)
                setTag("Properties", NBTTagCompound().apply {
                    setTag("textures", NBTTagList().apply {
                        appendTag(NBTTagCompound().apply {
                            setString("Value", texture)
                        })
                    })
                })
            })
            setTag("display", NBTTagCompound().apply {
                setString("Name", name)
                setTag("Lore", NBTTagList().apply {
                    lore.forEach { line ->
                        appendTag(NBTTagString(line))
                    }
                })
            })
        }
    }
}