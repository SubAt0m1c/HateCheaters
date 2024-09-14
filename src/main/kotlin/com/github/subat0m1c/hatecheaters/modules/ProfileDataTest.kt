package com.github.subat0m1c.hatecheaters.modules

import com.github.subat0m1c.hatecheaters.utils.ChatUtils.modMessage
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.checkIfPlayerExists
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.getPlayerProfile
import com.github.subat0m1c.hatecheaters.utils.JsonParseUtils.parsedPlayerList
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.Serializers
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.Serializers.getMappedGemstones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.odinmain.OdinMain.scope
import me.odinmain.features.Category
import me.odinmain.features.Module

object ProfileDataTest : Module(
    name = "Profile Data Test",
    description = "Type \"api test username\" in chat to get random profile data",
    category = Category.SKYBLOCK
) {

    val testRegex = Regex("(?s).*api test (.*)")

    init {
        onMessage(testRegex) {
            if (it.contains("list size")) {
                modMessage(parsedPlayerList.size)
                return@onMessage
            }
            val name = testRegex.find(it)?.groupValues?.get(1) ?: ""

            scope.launch(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    val profile = getPlayerProfile(name)
                    if (profile == null) {
                        if (checkIfPlayerExists(name)) modMessage("player exists! Either they haven't been cached yet or they haven't made any skyblock profiles!")
                        else modMessage("Player doesnt exist!")
                        return@withContext
                    }
                    modMessage("current area: " + profile.data.userData.currentArea.currentArea)
                    val profileKills = profile.data.kills?.kills
                    val bloodMobs = (profileKills?.find { it.entityId == "watcher_summon_undead" }?.amount ?: 0) + (profileKills?.find { it.entityId == "master_watcher_summon_undead" }?.amount ?: 0)
                    modMessage("bloodmobs: $bloodMobs")
                    modMessage("secrets: " + profile.data.dungeons?.secretsFound)
                    modMessage("magic power: " + profile.data.accessories?.magicalPower?.total)
                    modMessage(profile.raw.accessoryBagStorage?.tuning)
                    val tuningData = profile.raw.accessoryBagStorage?.tuning?.currentTunings?.entries?.map { "${it.key} | ${it.value}" }
                    modMessage("tunings:\n${tuningData?.joinToString("\n")}")
                    val weaponGemstones = getMappedGemstones(profile.data.items.weapons.highestPriorityWeapon?.tag?.extraAttributes?.gems)
                    val weaponsNormalized = Pair(weaponGemstones.first.map { "${it.key} | ${it.value}" }, weaponGemstones.second.map { "${it.key} | ${it.value.name} = ${it.value.tier}" })
                    modMessage("Unmapped Gems:\n${weaponsNormalized.first.joinToString("\n")}\nMapped Gems:\n${weaponsNormalized.second.joinToString("\n")}")
                }
            }
        }
    }
}