package com.github.subat0m1c.hatecheaters.utils.apiutils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object SkyCryptData {

    fun skyCryptToHypixel(profiles: SkyCryptProfiles, name: String, uuid: String = "SkyCryptPlayer") = HypixelData.PlayerInfo(
        uuid = uuid,
        name = name,
        skyCrypt = true,
        profileData = HypixelData.ProfilesData(
            profiles = profiles.profiles.entries.map {
                HypixelData.Profiles(
                    profileId = it.key,
                    gameMode = it.value.gameMode,
                    cuteName = it.value.cuteName,
                    selected = it.value.current,
                    members = mapOf(uuid to it.value.raw),
                )
            }
        )
    )

    val dummyPlayer = HypixelData.PlayerInfo(
        uuid = "BAH",
        name = "???",
        profileData = HypixelData.ProfilesData()
    )

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
        val raw: HypixelData.MemberData, // only using raw data so its compatible with hypixel format.
    )
}