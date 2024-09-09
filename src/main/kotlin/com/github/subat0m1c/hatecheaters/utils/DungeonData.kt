package com.github.subat0m1c.hatecheaters.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object DungeonData {

    data class Profiles(
        @SerializedName("profiles")
        val profiles: Map<String, Profile>
    )

    data class Profile(
        @SerializedName("profile_id")
        val profileId: String,
        @SerializedName("cute_name")
        val cuteName: String,
        @SerializedName("selected")
        val selected: Boolean,
        @SerializedName("dungeons")
        val dungeons: Dungeons,
    )

    data class Dungeons(
        @SerializedName("catacombs")
        val catacombs: DungeonsData,
        @SerializedName("master_catacombs")
        val mastercatacombs: DungeonsData,
        @SerializedName("floor_completions")
        val floorCompletions: Int,
        @SerializedName("classes")
        val clazzes: Clazzes,
        @SerializedName("secrets_found")
        val secretsFound: Int
    )

    data class Clazzes(
        @SerializedName("selected_class")
        val selected: String,
        @SerializedName("classes")
        val clazzData: Map<String, ClazzData>,
        @SerializedName("experience")
        val experience: Double,
        @SerializedName("average_level")
        val averageLevel: Float,
        @SerializedName("average_level_with_progress")
        val averageLevelWithProgress: Float,
        @SerializedName("maxed")
        val maxed: Boolean
    )

    data class ClazzData(
        @SerializedName("level")
        val level: DungeonsLevel,
        @SerializedName("current")
        val current: Boolean
    )

    data class DungeonsData(
        @SerializedName("id")
        val id: String,
        @SerializedName("visited")
        val visited: Boolean,
        @SerializedName("level")
        val level: DungeonsLevel,
        @SerializedName("highest_floor")
        val highestFloor: String,
        @SerializedName("floors")
        val floors: Map<Int, Floor>,
        @SerializedName("completions")
        val completions: Long
    )

    data class DungeonsLevel(
        @SerializedName("xp")
        val xp: Double,
        @SerializedName("level")
        val level: Int,
        @SerializedName("maxLevel")
        val maxLevel: Int,
        @SerializedName("xpCurrent")
        val xpCurrent: Long,
        @SerializedName("xpForNext")
        val xpForNext: Double?,
        @SerializedName("progress")
        val progress: Int,
        @SerializedName("levelCap")
        val levelcap: Int,
        @SerializedName("uncappedLevel")
        val uncappedlevel: Int,
        @SerializedName("levelWithProgress")
        val levelWithProgress: Int,
        @SerializedName("unlockableLevelWithProgress")
        val unlockablelevel: Int,
        @SerializedName("rank")
        val rank: Int
    )

    data class Floor(
        @SerializedName("name")
        val name: String,
        @SerializedName("icon_texture")
        val iconTexture: String,
        @SerializedName("stats")
        val stats: FloorStats,
        @SerializedName("best_runs")
        val bestRuns: List<FloorRun>,
        @SerializedName("most_damage")
        val mostDamage: MostDamage
    )

    data class MostDamage(
        @SerializedName("class")
        val clazz: String,
        @SerializedName("value")
        val damage: Double,
    )

    data class FloorStats(
        @SerializedName("times_played")
        val timesPlayed: Int,
        @SerializedName("tier_completions")
        val tierCompletions: Int,
        @SerializedName("milestone_completions")
        val milestoneComps: Int,
        @SerializedName("fastest_time")
        val fastestTime: Long,
        @SerializedName("mobs_killed")
        val mobsKilled: Long,
        @SerializedName("most_mobs_killed")
        val mostMobsKilled: Int,
        @SerializedName("most_healing")
        val mostHealing: Double,
        @SerializedName("watcher_kills")
        val watcherKills: Int,
        @SerializedName("fastest_time_s")
        val fastestTimeS: Long?,
        @SerializedName("fastest_time_s_plus")
        val fastestTimeSPlus: Long?,
    )

    data class FloorRun(
        @SerializedName("timestamp")
        val timeStamp: Long,
        @SerializedName("score_exploration")
        val scoreExploration: Int,
        @SerializedName("score_speed")
        val scoreSpeed: Int,
        @SerializedName("score_skill")
        val scoreSkill: Int,
        @SerializedName("score_bonus")
        val scoreBonus: Int,
        @SerializedName("dungeon_class")
        val dungeonClass: String,
        @SerializedName("teammates")
        val teammates: List<String>,
        @SerializedName("elapsed_time")
        val elapsedTime: Long,
        @SerializedName("damage_dealt")
        val damageDealt: Double,
        @SerializedName("deaths")
        val deaths: Int,
        @SerializedName("mobs_killed")
        val mobsKilled: Int,
        @SerializedName("secrets_found")
        val secretsFound: Int,
        @SerializedName("damage_mitigated")
        val damageMitigated: Double,
        @SerializedName("ally_healing")
        val allyHealing: Double,
    )

    fun parseData(data: JsonObject): Profiles {
        val gson = Gson()
        val profilesType: Type = object : TypeToken<Map<String, Profile>>() {}.type
        val profiles = gson.fromJson<Map<String, Profile>>(data.getAsJsonObject("profiles"), profilesType)
        return Profiles(profiles)
    }

}