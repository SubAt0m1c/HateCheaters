package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.short
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.ItemUtils.getMagicalPower
import me.odinmain.utils.floor
import me.odinmain.utils.skyblock.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraftforge.common.util.Constants
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.floor

object ApiUtils {

    /**
     * Taken and modified from Skytils under AGPL-3.0
     */
    @OptIn(ExperimentalEncodingApi::class)
    val HypixelApiStats.InventoryContents.toMCItems: List<ItemStack?> get() = data.let {
        if (it.isEmpty()) return emptyList()
        val itemNBTList = CompressedStreamTools.readCompressed(Base64.decode(it).inputStream()).getTagList("i", Constants.NBT.TAG_COMPOUND)
        (0).rangeUntil(itemNBTList.tagCount()).map { itemNBTList.getCompoundTagAt(it).takeUnless { it.hasNoTags() }?.let { ItemStack.loadItemStackFromNBT(it) } }
    }

    /**
     * Taken and modified from Skytils under AGPL-3.0
     */
    /**val HypixelApiStats.MemberData.magicalPower: Int get() =
        inventory.bagContents["talisman_bag"]?.toMCItems?.filterNotNull()?.map { tali ->
            val id = tali.itemID
                .let { it.takeUnless { it.startsWith("PARTY_HAT_") } ?: "PARTY_HAT" }
                .takeUnless { tali.lore.any { it.matches(Regex("§7§4☠ §cRequires §5.+§c.")) } }
                ?: Pair(tali.itemID, 0)

            val mp = mpMap[getRarity(tali.lore)] ?: 0

            Pair(id, mp + when (id) {
                "HEGEMONY_ARTIFACT" -> mp
                "ABICASE" -> (crimsonIsle.abiphone.activeContacts.size / 2).floor().toInt()
                else -> 0
            })
        }?.groupBy { it.first }?.mapValues { entry ->
            entry.value.maxBy { it.second }
        }?.values?.fold(0) { acc, pair ->
            acc + pair.second
        }?.apply { (this + 11).takeIf { rift.access.consumedPrism } } ?: 0 */

    val HypixelApiStats.MemberData.magicalPower: Int get() {
        return inventory.bagContents["talisman_bag"]?.toMCItems?.filterNotNull()
            ?.filterNot { it.lore.any { it.matches(Regex("§7§4☠ §cRequires §5.+§c.")) }}
            ?.map {
                var mp = it.getMagicalPower
                if (it.itemID == "ABICASE") mp += floor(crimsonIsle.abiphone.activeContacts.size/2.0).toInt()
                val itemId = it.itemID.takeUnless { it.startsWith("PARTY_HAT") || it.startsWith("BALLOON_HAT") } ?: "PARTY_HAT"
                itemId to mp
            }?.groupBy { it.first }?.mapValues { entry ->
                entry.value.maxBy { it.second }
            }?.values?.fold(0) { acc, pair ->
                acc + pair.second
            }?.apply { (this+11).takeIf { rift.access.consumedPrism } } ?: 0
    }

    /**
     * taken and modified from skytils under AGPL-3.0
     */
    fun getLevelWithProgress(experience: Double, values: Array<Long>): Double {
        var xp = experience
        var level = 0
        val maxLevelExperience = if (values.size > 50) values[50] else 0

        for (i in values.indices) {
            val toRemove = values[i]

            if (xp < toRemove) {
                val progress = xp / toRemove
                return level+progress
            }

            xp -= toRemove
            level++
        }

        if (xp > 0) {
            level += (xp / maxLevelExperience).toInt()
            val progress = xp % maxLevelExperience / maxLevelExperience
            return level+progress
        }

        return level.toDouble()
    }

    val HypixelApiStats.DungeonsData.classAverage: Double get() =
        classes.values.sumOf { it.classLevel }/5
    val HypixelApiStats.DungeonTypes.cataLevel: Double get() =
        getLevelWithProgress(catacombs.experience, dungeonsLevels)
    val HypixelApiStats.ClassData.classLevel: Double get() =
        getLevelWithProgress(experience, dungeonsLevels).short

    private val dungeonsLevels: Array<Long> = arrayOf(
        50, 75, 110, 160, 230, 330, 470, 670, 950, 1340,
        1890, 2665, 3760, 5260, 7380, 10300, 14400, 20000,
        27600, 38000, 52500, 71500, 97000, 132000, 180000,
        243000, 328000, 445000, 600000, 800000, 1065000,
        1410000, 1900000, 2500000, 3300000, 4300000, 5600000,
        7200000, 9200000, 12000000, 15000000, 19000000,
        24000000, 30000000, 38000000, 48000000, 60000000,
        75000000, 93000000, 116250000, 200000000
    )

}