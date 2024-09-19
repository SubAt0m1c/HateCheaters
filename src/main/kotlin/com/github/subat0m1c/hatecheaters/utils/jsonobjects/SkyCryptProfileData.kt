package com.github.subat0m1c.hatecheaters.utils.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SkyCryptProfileData {

    fun skyCryptToHypixel(profiles: SkyCryptProfiles, name: String, uuid: String = "SkyCryptPlayer"): HypixelProfileData.PlayerInfo {
        return HypixelProfileData.PlayerInfo(
            uuid = uuid,
            name = name,
            skyCrypt = true,
            profileData = HypixelProfileData.ProfilesData(
                profiles = profiles.profiles.entries.map {
                    HypixelProfileData.Profiles(
                        profileId = it.key,
                        gameMode = it.value.gameMode,
                        cuteName = it.value.cuteName,
                        selected = it.value.current,
                        members = mapOf(uuid to it.value.raw),
                    )
                }
            )
        )
    }

    @Serializable
    data class SkyCryptProfiles(
        @SerialName("profiles")
        val profiles: Map<String, SkyCryptProfile> = emptyMap(),
        @SerialName("error")
        val error: String? = null
    )

    @Serializable
    data class SkyCryptProfile(
        @SerialName("profile_id")
        val profileId: String,
        @SerialName("cute_name")
        val cuteName: String,
        @SerialName("game_mode")
        val gameMode: String,
        @SerialName("current")
        val current: Boolean,
        @SerialName("raw")
        val raw: HypixelProfileData.MemberData, // only using raw data so its compatible with hypixel format.
    )
}