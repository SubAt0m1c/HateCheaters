package com.github.subat0m1c.hatecheaters.utils

import com.github.subat0m1c.hatecheaters.pvgui.PVGui.font
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.capitalizeWords
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.DungeonsData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.DungeonTypes
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.ClassData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.InventoryContents
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.MemberData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.PlayerData
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelProfileData.Pet
import com.github.subat0m1c.hatecheaters.utils.ItemUtils.getMagicalPower
import me.odinmain.utils.render.Color
import me.odinmain.utils.skyblock.*
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemSkull
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.floor

object ApiUtils {

    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     */
    @OptIn(ExperimentalEncodingApi::class)
    val InventoryContents.itemStacks: List<ItemStack?> get() = data.let {
        if (it.isEmpty()) return emptyList()
        val itemNBTList = CompressedStreamTools.readCompressed(Base64.decode(it).inputStream()).getTagList("i", Constants.NBT.TAG_COMPOUND)
        (0).rangeUntil(itemNBTList.tagCount()).map { itemNBTList.getCompoundTagAt(it).takeUnless { it.hasNoTags() }?.let { ItemStack.loadItemStackFromNBT(it) } }
    }

    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     */
    val MemberData.magicalPower: Int get() =
        inventory.bagContents["talisman_bag"]?.itemStacks?.filterNotNull()
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

    private val petItemRegex = Regex("(?:PET_ITEM_)?([A-Z_]+?)(?:_(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC))?")

    val Pet.petItem: String? get() {
        val (heldItem, rarity) = petItemRegex.matchEntire(heldItem ?: return null)?.destructured ?: return null
        return "${getRarityColor(rarity)}${heldItem.lowercase().replace("_", " ").capitalizeWords()}"
    }

    val Pet.colorName: String get() {
        return (getRarityColor(this.tier) + this.type.replace("_", " ").lowercase().capitalizeWords())
    }

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

    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     *
     * Includes overflow formula from [SoopyV2](https://github.com/Soopyboo32/SoopyV2) under [GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
     */
    fun getLevelWithProgress(experience: Double, values: Array<Long>, slope: Long = 0): Double {
        var xp = experience
        var level = 0
        val maxLevelExperience = values.last()


        for (i in values.indices) {
            val toRemove = values[i]

            if (xp < toRemove) {
                val progress = xp / toRemove
                return level+progress
            }

            xp -= toRemove
            level++
        }

        if (xp > 0 && slope <= 0) {
            level += (xp / maxLevelExperience).toInt()
            val progress = xp % maxLevelExperience / maxLevelExperience
            return level+progress
        } else {
            var reqSlope = slope
            var requiredXp = maxLevelExperience.toDouble() + reqSlope

            while (xp > requiredXp) {
                level++
                xp -= requiredXp
                requiredXp += reqSlope
                if (level % 10 == 0) reqSlope *= 2
            }

            if (xp < requiredXp) {
                val progress = xp / requiredXp
                return level + progress
            }
        }

        return level.toDouble()
    }

    val DungeonsData.classAverage: Double get() =
        classes.values.sumOf { it.classLevel }/classes.size
    val DungeonTypes.cataLevel: Double get() =
        getLevelWithProgress(catacombs.experience, dungeonsLevels)
    val ClassData.classLevel: Double get() =
        getLevelWithProgress(experience, dungeonsLevels)

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

    val PlayerData.skillAverage: Double get() {
        val nonCosmeticSkills = experience.toMutableMap().apply {
            this.remove("SKILL_SOCIAL")
            this.remove("SKILL_RUNECRAFTING")
        }
        return nonCosmeticSkills.values.sumOf { getSkillLevel(it) }/nonCosmeticSkills.size
    }
    val PlayerData.cappedSkillAverage: Double get() {
        val nonCosmeticSkills = experience.toMutableMap().apply {
            this.remove("SKILL_SOCIAL")
            this.remove("SKILL_RUNECRAFTING")
        }
        return nonCosmeticSkills.entries.sumOf { getSkillLevel(it.value).coerceAtMost(getSkillCap(it.key.lowercase().substringAfter("skill_")).toDouble()) }/nonCosmeticSkills.size
    }

    fun getSkillLevel(exp: Double): Double {
        //modMessage(skillLevels.sum())
        return getLevelWithProgress(exp, skillLevels, 600000)
    }

    fun getSkillCap(skill: String): Int {
        return when (skill) {
            "taming" -> 60
            "mining" -> 60
            "foraging" -> 50
            "enchanting" -> 60
            "carpentry" -> 50
            "farming" -> 60
            "combat" -> 60
            "fishing" -> 50
            "alchemy" -> 50
            "runecrafting" -> 25
            "social" -> 20
            else -> -1
        }
    }

    fun getSkillColor(skill: String): Color {
        return when (skill) {
            "taming" -> font
            "mining" -> Color.GRAY
            "foraging" -> Color.DARK_GREEN
            "enchanting" -> Color.MAGENTA
            "carpentry" -> Color("A52A2AFF")
            "farming" -> Color.GREEN
            "combat" -> Color.RED
            "fishing" -> Color.BLUE
            "alchemy" -> Color.YELLOW
            "runecrafting" -> Color.PURPLE
            "social" -> Color.GREEN
            else -> font
        }
    }

    private val skillLevels: Array<Long> = arrayOf(
        50, 125, 200, 300, 500, 750, 1000, 1500,
        2000, 3500, 5000, 7500, 10000, 15000, 20000,
        30000, 50000, 75000, 100000, 200000, 300000,
        400000, 500000, 600000, 700000, 800000, 900000,
        1000000, 1100000, 1200000, 1300000, 1400000,
        1500000, 1600000, 1700000, 1800000, 1900000,
        2000000, 2100000, 2200000, 2300000, 2400000,
        2500000, 2600000, 2750000, 2900000, 3100000,
        3400000, 3700000, 4000000, 4300000, 4600000,
        4900000, 5200000, 5500000, 5800000, 6100000,
        6400000, 6700000, 7000000
    )

    fun getSlayerSkillLevel(exp: Double, slayer: String): Double {
        return (if (slayer != "vampire") getLevelWithProgress(exp, slayerLevels) else getLevelWithProgress(exp, vampireLevels)).coerceAtMost(getSlayerCap(slayer).toDouble())
    }

    private val slayerLevels: Array<Long> = arrayOf(
        5, 15, 200, 1000, 5000, 20000, 100000, 400000,
        1000000
    )

    private val vampireLevels: Array<Long> = arrayOf(
        20, 75, 240, 840, 2400
    )

    fun getSlayerCap(slayer: String): Int {
        return when (slayer) {
            "blaze" -> 7
            "vampire" -> 5
            else -> 9
        }
    }
}