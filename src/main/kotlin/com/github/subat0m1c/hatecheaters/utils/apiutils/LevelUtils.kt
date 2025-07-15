package com.github.subat0m1c.hatecheaters.utils.apiutils

import com.github.subat0m1c.hatecheaters.modules.render.ProfileViewer
import com.github.subat0m1c.hatecheaters.pvgui.v2.utils.Utils.without
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.ClassData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.DungeonTypes
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.DungeonsData
import com.github.subat0m1c.hatecheaters.utils.apiutils.HypixelData.PlayerData
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Color
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.Colors
import com.github.subat0m1c.hatecheaters.utils.odinwrappers.hc

object LevelUtils {
    /**
     * Taken and modified from [Skytils](https://github.com/Skytils/SkytilsMod) under [AGPL-3.0](https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md).
     *
     * Includes overflow formula from [SoopyV2](https://github.com/Soopyboo32/SoopyV2) under [GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
     */
    private fun getLevelWithProgress(experience: Double, values: Array<Long>, slope: Long = 0): Double {
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
            return level + (xp % maxLevelExperience / maxLevelExperience)
        } else {
            var reqSlope = slope
            var requiredXp = maxLevelExperience.toDouble() + reqSlope

            while (xp > requiredXp) {
                level++
                xp -= requiredXp
                requiredXp += reqSlope
                if (level % 10 == 0) reqSlope *= 2
            }

            if (xp < requiredXp) return level + ( xp / requiredXp)
        }

        return level.toDouble()
    }

    inline val DungeonsData.classAverage: Double get() =
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

    inline val PlayerData.skillAverage: Double get() =
        experience.without("SKILL_SOCIAL", "SKILL_SOCIAL", "SKILL_DUNGEONEERING").let { skills -> skills.values.sumOf { getSkillLevel(it) }/skills.size }

    inline val PlayerData.cappedSkillAverage: Double get() =
        experience.without("SKILL_SOCIAL", "SKILL_SOCIAL", "SKILL_DUNGEONEERING").let { skills -> skills.entries.sumOf { getSkillLevel(it.value).coerceAtMost(
            getSkillCap(it.key.lowercase().substringAfter("skill_")).toDouble()) }/skills.size }

    fun getSkillLevel(exp: Double): Double =
        getLevelWithProgress(exp, skillLevels, 600000)

    fun getSkillCap(skill: String): Int =
        when (skill) {
            "taming"       -> 60
            "mining"       -> 60
            "foraging"     -> 50
            "enchanting"   -> 60
            "carpentry"    -> 50
            "farming"      -> 60
            "combat"       -> 60
            "fishing"      -> 50
            "alchemy"      -> 50
            "runecrafting" -> 25
            "social"       -> 20
            else           -> -1
        }

    fun getSkillColor(skill: String): Color = when (skill) {
        "taming" -> ProfileViewer.currentTheme.font.hc()
        "mining" -> Colors.GRAY
        "foraging" -> Colors.DARKGREEN
        "enchanting"   -> Color(170, 0, 170)
        "carpentry"    -> Color("A52A2AFF")
        "farming" -> Colors.GREEN
        "combat" -> Colors.RED
        "fishing" -> Colors.BLUE
        "alchemy" -> Colors.YELLOW
        "runecrafting" -> Colors.MAGENTA
        "social" -> Colors.GREEN
        else -> ProfileViewer.currentTheme.font.hc()
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

    fun getSlayerSkillLevel(exp: Double, slayer: String): Double =
        (if (slayer != "vampire") getLevelWithProgress(exp, slayerLevels) else getLevelWithProgress(exp, vampireLevels)).coerceAtMost(
            getSlayerCap(slayer).toDouble())

    fun getSlayerColor(slayer: String): Color = when (slayer) {
        "wolf" -> ProfileViewer.currentTheme.font.hc()
        "zombie" -> Colors.GREEN
        "enderman" -> Color(170, 0, 170)
        "vampire" -> Colors.RED
        "blaze" -> Colors.ORANGE
        "spider"   -> Colors.BLACK
        else -> ProfileViewer.currentTheme.font.hc()
    }

    private val slayerLevels: Array<Long> = arrayOf(5, 10, 185, 800, 4000, 15000, 80000, 300000, 600000)

    private val vampireLevels: Array<Long> = arrayOf(20, 55, 165, 600, 1560)

    fun getSlayerCap(slayer: String): Int = if (slayer == "vampire") 5 else 9
}