package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.formatted
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.InventoryContents
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.MemberData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.Pet
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerInfo
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.Profiles
import me.odinmain.utils.skyblock.lore
import me.odinmain.utils.skyblock.skyblockID
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraftforge.common.util.Constants
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.floor

object ApiUtils {

    fun PlayerInfo.profileOrSelected(profileName: String? = null): Profiles? =
        this.profileData.profiles.find { it.cuteName.lowercase() == profileName?.lowercase() } ?: this.profileData.profiles.find { it.selected }

    inline val PlayerInfo.memberData get() = this.profileData.profiles.find { it.selected }?.members?.get(uuid)

    val PlayerInfo.profileList: List<Pair<String, String>> get() = this.profileData.profiles.map { it.cuteName to it.gameMode }

    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     */
    @OptIn(ExperimentalEncodingApi::class)
    val InventoryContents.itemStacks: List<ItemStack?> get() = with(data) {
        if (isEmpty()) return emptyList()
        val itemNBTList = CompressedStreamTools.readCompressed(Base64.decode(this).inputStream()).getTagList("i", Constants.NBT.TAG_COMPOUND)
        (0).rangeUntil(itemNBTList.tagCount()).map { itemNBTList.getCompoundTagAt(it).takeUnless { it.hasNoTags() }?.let { ItemStack.loadItemStackFromNBT(it) } }
    }

    val MemberData.assumedMagicalPower: Int get() = magicalPower.takeUnless { it == 0 } ?: (accessoryBagStorage.tuning.currentTunings.values.sum() * 10)

    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     */
    val MemberData.magicalPower: Int get() = inventory.bagContents["talisman_bag"]?.itemStacks?.mapNotNull {
        if (it.lore.any { it.matches(Regex("§7§4☠ §cRequires §5.+§c.")) } || it == null) return@mapNotNull null
        val mp = it.getMagicalPower + (if (it.skyblockID == "ABICASE") floor(crimsonIsle.abiphone.activeContacts.size/2.0).toInt() else 0)
        val itemId = it.skyblockID.takeUnless { it.startsWith("PARTY_HAT") || it.startsWith("BALLOON_HAT") } ?: "PARTY_HAT"
        itemId to mp
    }?.groupBy { it.first }?.mapValues { entry ->
        entry.value.maxBy { it.second }
    }?.values?.fold(0) { acc, pair ->
        acc + pair.second
    }?.let { it + if (rift.access.consumedPrism) 11 else 0 } ?: 0

    private val petItemRegex = Regex("(?:PET_ITEM_)?([A-Z_]+?)(?:_(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC))?")

    val Pet.petItem: String? get() =
        heldItem?.let { petItemRegex.matchEntire(it)?.destructured?.let { (heldItem, rarity) -> "${getRarityColor(rarity)}${heldItem.lowercase().replace("_", " ").capitalizeWords()}" } }

    val Pet.colorName: String get() = (getRarityColor(this.tier) + this.type.formatted)

    fun getRarityColor(rarity: String): String {
        return when (rarity) {
            "COMMON" -> "§f"
            "UNCOMMON" -> "§a"
            "RARE" -> "§9"
            "EPIC" -> "§5"
            "LEGENDARY" -> "§6"
            "MYTHIC" -> "§d"
            else -> "§r"
        }
    }
}