package com.github.subat0m1c.hatecheaters.utils.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object DungeonData {

    @Serializable
    data class Profiles(
        @SerialName("profiles")
        val profiles: Map<String, Profile> = emptyMap(),
        @SerialName("error")
        val error: String? = null
    )

    @Serializable
    data class Profile(
        @SerialName("profile_id")
        val profileId: String,
        @SerialName("cute_name")
        val cuteName: String,
        @SerialName("selected")
        val selected: Boolean,
        @SerialName("dungeons")
        val dungeons: Dungeons? = null,
    )

    @Serializable
    data class Dungeons(
        @SerialName("catacombs")
        val catacombs: DungeonsData,
        @SerialName("master_catacombs")
        val mastercatacombs: DungeonsData? = null,
        @SerialName("floor_completions")
        val floorCompletions: Int,
        @SerialName("classes")
        val clazzes: Clazzes,
        @SerialName("secrets_found")
        val secretsFound: Int
    )

    @Serializable
    data class Clazzes(
        @SerialName("selected_class")
        val selected: String,
        @SerialName("classes")
        val clazzData: Map<String, ClazzData>,
        @SerialName("experience")
        val experience: Double,
        @SerialName("average_level")
        val averageLevel: Float? = null,
        @SerialName("average_level_with_progress")
        val averageLevelWithProgress: Float? = null,
        @SerialName("maxed")
        val maxed: Boolean
    )

    @Serializable
    data class ClazzData(
        @SerialName("level")
        val level: DungeonsLevel,
        @SerialName("current")
        val current: Boolean
    )

    @Serializable
    data class DungeonsData(
        @SerialName("id")
        val id: String? = null,
        @SerialName("visited")
        val visited: Boolean? = null,
        @SerialName("level")
        val level: DungeonsLevel? = null,
        @SerialName("highest_floor")
        val highestFloor: String? = null,
        @SerialName("floors")
        val floors: Map<Int, Floor> = emptyMap(),
        @SerialName("completions")
        val completions: Long? = null,
    )

    @Serializable
    data class DungeonsLevel(
        @SerialName("xp")
        val xp: Double,
        @SerialName("level")
        val level: Int,
        @SerialName("maxLevel")
        val maxLevel: Int,
        @SerialName("xpCurrent")
        val xpCurrent: Long,
        @SerialName("xpForNext")
        val xpForNext: Double? = null,
        @SerialName("progress")
        val progress: Double,
        @SerialName("levelCap")
        val levelcap: Double,
        @SerialName("uncappedLevel")
        val uncappedlevel: Int,
        @SerialName("levelWithProgress")
        val levelWithProgress: Double,
        @SerialName("unlockableLevelWithProgress")
        val unlockablelevel: Double,
        @SerialName("rank")
        val rank: Int? = null
    )

    @Serializable
    data class Floor(
        @SerialName("name")
        val name: String,
        @SerialName("icon_texture")
        val iconTexture: String,
        @SerialName("stats")
        val stats: FloorStats,
        @SerialName("best_runs")
        val bestRuns: List<FloorRun>,
        @SerialName("most_damage")
        val mostDamage: MostDamage? = null,
    )

    @Serializable
    data class MostDamage(
        @SerialName("class")
        val clazz: String,
        @SerialName("value")
        val damage: Double,
    )

    @Serializable
    data class FloorStats(
        @SerialName("times_played")
        val timesPlayed: Int? = 0,
        @SerialName("tier_completions")
        val tierCompletions: Int? = 0,
        @SerialName("milestone_completions")
        val milestoneComps: Int? = 0,
        @SerialName("fastest_time")
        val fastestTime: Long? = 0,
        @SerialName("mobs_killed")
        val mobsKilled: Long? = 0,
        @SerialName("most_mobs_killed")
        val mostMobsKilled: Int? = 0,
        @SerialName("most_healing")
        val mostHealing: Double? = 0.0,
        @SerialName("watcher_kills")
        val watcherKills: Int? = 0,
        @SerialName("fastest_time_s")
        val fastestTimeS: Long? = null,
        @SerialName("fastest_time_s_plus")
        val fastestTimeSPlus: Long? = null,
    )

    @Serializable
    data class FloorRun(
        @SerialName("timestamp")
        val timeStamp: Long,
        @SerialName("score_exploration")
        val scoreExploration: Int,
        @SerialName("score_speed")
        val scoreSpeed: Int,
        @SerialName("score_skill")
        val scoreSkill: Int,
        @SerialName("score_bonus")
        val scoreBonus: Int,
        @SerialName("dungeon_class")
        val dungeonClass: String,
        @SerialName("teammates")
        val teammates: List<String>,
        @SerialName("elapsed_time")
        val elapsedTime: Long,
        @SerialName("damage_dealt")
        val damageDealt: Double,
        @SerialName("deaths")
        val deaths: Int,
        @SerialName("mobs_killed")
        val mobsKilled: Int? = 0,
        @SerialName("secrets_found")
        val secretsFound: Int? = 0,
        @SerialName("damage_mitigated")
        val damageMitigated: Double? = 0.0,
        @SerialName("ally_healing")
        val allyHealing: Double? = 0.0,
    )
}