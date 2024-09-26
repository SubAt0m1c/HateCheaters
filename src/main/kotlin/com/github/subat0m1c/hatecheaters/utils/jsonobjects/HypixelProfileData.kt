package com.github.subat0m1c.hatecheaters.utils.jsonobjects

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement

//todo all the missing data. right now its about enough for what i currently need, however more will be needed with profile viewer. (more was not needed)
object HypixelProfileData {

    data class PlayerInfo(
        val profileData: ProfilesData,
        val uuid: String,
        val name: String,
        val skyCrypt: Boolean = false
    )

    @Serializable
    data class ProfilesData(
        val cause: String? = null,
        @SerialName("profiles")
        val profiles: List<Profiles> = emptyList(),
    )

    @Serializable
    data class Profiles(
        @SerialName("profile_id")
        val profileId: String,
        @SerialName("community_upgrades")
        val communityUpgrades: CommunityUpgrades = CommunityUpgrades(),
        val members: Map<String, MemberData>,
        @SerialName("game_mode")
        val gameMode: String = "normal",
        val banking: BankingData = BankingData(),
        @SerialName("cute_name")
        val cuteName: String,
        val selected: Boolean,
    )
    @Serializable
    data class MemberData(
        val rift: RiftData = RiftData(),
        @SerialName("player_data")
        val playerData: PlayerData = PlayerData(),
        val events: EventsData = EventsData(),
        @SerialName("garden_player_data")
        val gardenPlayerData: GardenPlayerData = GardenPlayerData(),
        @SerialName("accessory_bag_storage")
        val accessoryBagStorage: AccessoryBagStorage = AccessoryBagStorage(),
        val leveling: LevelingData = LevelingData(),
        @SerialName("item_data")
        val miscItemData: MiscItemData = MiscItemData(),
        @SerialName("jacobs_contest")
        val jacobsContest: JacobsContestData = JacobsContestData(),
        val currencies: CurrencyData = CurrencyData(),
        val dungeons: DungeonsData = DungeonsData(),
        @SerialName("glacite_player_data")
        val glacitePlayerData: GlacitePlayerData = GlacitePlayerData(),
        val profile: ProfileData = ProfileData(),
        @SerialName("pets_data")
        val pets: PetsData = PetsData(),
        @SerialName("player_id")
        val playerId: String,
        @SerialName("nether_island_player_data")
        val crimsonIsle: CrimsonIsle = CrimsonIsle(),
        @SerialName("player_stats")
        val playerStats: PlayerStats = PlayerStats(),
        val inventory: Inventory = Inventory(),
    )

    @Serializable
    data class PlayerStats(
        val kills: Map<String, Int> = emptyMap(),
        val deaths: Map<String, Int> = emptyMap(),
    )

    @Serializable
    data class CrimsonIsle(
        val abiphone: Abiphone = Abiphone(),
    )

    @Serializable
    data class Abiphone(
        @SerialName("active_contacts")
        val activeContacts: List<String> = emptyList(),
    )

    @Serializable
    data class RiftData(
        @SerialName("village_plaza")
        val villagePlaza: VillagePlaza = VillagePlaza(),
        @SerialName("wither_cage")
        val witherCage: WitherCage = WitherCage(),
        @SerialName("black_lagoon")
        val blackLagoon: BlackLagoon = BlackLagoon(),
        @SerialName("dead_cats")
        val deadCats: DeadCatsData = DeadCatsData(),
        @SerialName("wizard_tower")
        val wizardTower: WizardTower = WizardTower(),
        val enigma: EnigmaData = EnigmaData(),
        val gallery: GalleryData = GalleryData(),
        //todo the rest of this
        val access: RiftAccess = RiftAccess(),
    )

    @Serializable
    data class RiftAccess(
        @SerialName("last_free")
        val lastFree: Long = 0,
        @SerialName("consumed_prism")
        val consumedPrism: Boolean = false
    )

    @Serializable
    data class GalleryData(
        @SerialName("elise_step")
        val eliseStep: Int = 0,
        @SerialName("secured_trophies")
        val securedTrophies: List<SecuredTrophy> = emptyList(),
        @SerialName("sent_trophy_dialogues")
        val sentDialogues: List<String> = emptyList(),
    )

    @Serializable
    data class SecuredTrophy(
        val type: String,
        val timestamp: Long,
        val visits: Int,
    )

    @Serializable
    data class EnigmaData(
        @SerialName("bought_cloak")
        val boughtCloak: Boolean = false,
        @SerialName("found_souls")
        val foundSouls: List<String> = emptyList(),
        @SerialName("claimed_bonus_index")
        val claimedBonusIndex: Int = 0,
    )

    @Serializable
    data class WizardTower(
        @SerialName("wizard_quest_step")
        val wizardQuestStep: Int = 0,
        @SerialName("crumbs_laid_out")
        val crumbsLaidOut: Int = 0,
    )

    @Serializable
    data class DeadCatsData(
        @SerialName("found_cats")
        val foundCats: List<String> = emptyList(),
        @SerialName("talked_to_jacquelle")
        val talkedJacquelle: Boolean = false,
        @SerialName("picked_up_detector")
        val pickedUpDetector: Boolean = false,
        @SerialName("unlocked_pet")
        val unlocked: Boolean = false,
        val montezuma: Pet = Pet()
    )

    @Serializable
    data class BlackLagoon(
        @SerialName("talked_to_edwin")
        val talkedToEdwin: Boolean = false,
        @SerialName("received_science_paper")
        val receivedSciencePaper: Boolean = false,
        @SerialName("delivered_science_paper")
        val deliveredSciencePaper: Boolean = false,
        @SerialName("completed_step")
        val completedStep: Int = 0,
    )

    @Serializable
    data class WitherCage(
        @SerialName("killed_eyes")
        val killedEyes: List<String> = emptyList(),
    )

    @Serializable
    data class BarryCenter(
        @SerialName("first_talked_to_barry")
        val talkedToBarry: Boolean = false,
        val convinced: List<String> = emptyList(),
        @SerialName("received_reward")
        val receivedReward: Boolean = false,
    )

    @Serializable
    data class VillagePlaza(
        val murder: VillageMurder = VillageMurder(),
        @SerialName("barry_center")
        val barryCenter: BarryCenter = BarryCenter(),
        val cowboy: VillageCowboy = VillageCowboy(),
        @SerialName("barter_bank")
        val barterBank: JsonElement? = null, //todo
        val lonely: VillageLonely = VillageLonely(),
        val seraphine: VillageSeraphine = VillageSeraphine(),
        @SerialName("got_scammed")
        val scammed: Boolean = false,
    )

    @Serializable
    data class VillageSeraphine(
        @SerialName("step_index")
        val stepIndex: Int = 0,
    )

    @Serializable
    data class VillageLonely(
        @SerialName("seconds_sitting")
        val secondsSitting: Int = 0,
    )

    @Serializable
    data class VillageCowboy(
        val stage: Int = 0,
        @SerialName("hay_eaten")
        val hayEaten: Long = 0,
        @SerialName("rabbit_name")
        val rabbitName: String? = null
    )

    @Serializable
    data class VillageMurder(
        @SerialName("step_index")
        val stepIndex: Int = 0,
        @SerialName("room_clues")
        val roomClues: List<String> = emptyList(),
    )

    @Serializable
    data class PetsData(
        //todo other useless things here
        val pets: List<Pet> = emptyList()
    )

    @Serializable
    data class Pet(
        val uuid: String? = null,
        val uniqueId: String? = null,
        val type: String = "",
        val exp: Double = 0.0,
        val active: Boolean = false,
        val tier: String = "",
        val heldItem: String? = null,
        val candyUsed: Int = 0,
        val skin: String? = null,
    )

    @Serializable
    data class ProfileData(
        @SerialName("first_join")
        val firstJoin: Long = 0,
        @SerialName("personal_bank_upgrade")
        val personalBankUpgrade: Int = 0,
        @SerialName("bank_account")
        val bankAccount: Double = 0.0,
        @SerialName("cookie_buff_active")
        val activeCookie: Boolean = false,
    )

    @Serializable
    data class GlacitePlayerData(
        @SerialName("corpses_looted")
        val corpsesLooted: Map<String, Int> = emptyMap(),
        @SerialName("mineshafts_entered")
        val mineshaftsEntered: Int = 0,
    )

    @Serializable
    data class DungeonsData(
        @SerialName("dungeon_types")
        val dungeonTypes: DungeonTypes = DungeonTypes(),
        @SerialName("player_classes")
        val classes: Map<String, ClassData> = emptyMap(),
        @SerialName("dungeon_journal")
        val dungeonJournal: DungeonJournal = DungeonJournal(),
        @SerialName("dungeons_blah_blah")
        val dungeonYapping: List<String> = emptyList(),
        @SerialName("selected_dungeon_class")
        val selectedClass: String? = null,
        @SerialName("daily_runs")
        val dailyRuns: DailyRunData = DailyRunData(),
        //todo val treasures: TreasureData,
        @SerialName("dungeon_hub_race_settings")
        val dungeonRaceSettings: DungeonRaceSettings = DungeonRaceSettings(),
        @SerialName("last_dungeon_run")
        val lastDungeonRun: String? = null,
        val secrets: Long = 0,
    )

    @Serializable
    data class DungeonTypes(
        val catacombs: DungeonTypeData = DungeonTypeData(),
        @SerialName("master_catacombs")
        val mastermode: DungeonTypeData = DungeonTypeData(),
    )

    @Serializable
    data class DungeonRaceSettings(
        @SerialName("selected_race")
        val selected: String? = null,
        @SerialName("selected_setting")
        val setting: String? = null,
        val runback: Boolean = false
    )

    @Serializable
    data class DailyRunData(
        @SerialName("current_day_stamp")
        val currentDayStamp: Long? = null,
        @SerialName("completed_runs_count")
        val completedRunsCount: Long = 0,
    )

    @Serializable
    data class DungeonJournal(
        @SerialName("unlocked_journals")
        val unlockedJournals: List<String> = emptyList()
    )

    @Serializable
    data class ClassData(
        val experience: Double = 0.0
    )

    @Serializable
    data class DungeonTypeData(
        @SerialName("times_played")
        val timesPlayed: Map<String, Double>? = null,
        val experience: Double = 0.0,
        @SerialName("tier_completions")
        val tierComps: Map<String, Int> = emptyMap(),
        @SerialName("milestone_completions")
        val milestoneComps: Map<String, Int> = emptyMap(),
        @SerialName("fastest_time")
        val fastestTimes: Map<String, Long> = emptyMap(),
        @SerialName("best_runs")
        val bestRuns: Map<String, List<BestRun>> = emptyMap(),
        @SerialName("best_score")
        val bestScore: Map<String, Int> = emptyMap(),
        @SerialName("mobs_killed")
        val mobsKilled: Map<String, Int> = emptyMap(),
        @SerialName("most_mobs_killed")
        val mostMobsKilled: Map<String, Int> = emptyMap(),
        @SerialName("most_damage_berserk")
        val mostDamageBers: Map<String, Double> = emptyMap(),
        @SerialName("most_healing")
        val mostHealing: Map<String, Double> = emptyMap(),
        @SerialName("watcher_kills")
        val watcherKills: Map<String, Int> = emptyMap(),
        @SerialName("highest_tier_completed")
        val highestTierComp: Int = 0,
        @SerialName("most_damage_tank")
        val mostDamageTank: Map<String, Double> = emptyMap(),
        @SerialName("most_damage_healer")
        val mostDamageHealer: Map<String, Double> = emptyMap(),
        @SerialName("fastest_time_s")
        val fastestTimeS: Map<String, Long> = emptyMap(),
        @SerialName("most_damage_mage")
        val mostDamageMage: Map<String, Double> = emptyMap(),
        @SerialName("fastest_time_s_plus")
        val fastestTimeSPlus: Map<String, Long> = emptyMap(),
        @SerialName("most_damage_Archer")
        val mostDamageArcher: Map<String, Double> = emptyMap(),
    )

    @Serializable
    data class BestRun(
        val timestamp: Long? = null,
        @SerialName("score_exploration")
        val explorationScore: Int = 0,
        @SerialName("score_speed")
        val speedScore: Int = 0,
        @SerialName("score_skill")
        val skillScore: Int = 0,
        @SerialName("score_bonus")
        val bonusScore: Int = 0,
        @SerialName("dungeon_class")
        val dungeonClass: String? = null,
        val teammates: List<String> = emptyList(),
        @SerialName("elapsed_time")
        val elapsedTime: Long = 0,
        @SerialName("damage_dealt")
        val damageDealt: Double = 0.0,
        val deaths: Int = 0,
        @SerialName("mobs_killed")
        val mobsKilled: Int = 0,
        @SerialName("secrets_found")
        val secretsFound: Int = 0,
        @SerialName("damage_mitigated")
        val damageMitigated: Double = 0.0,
        @SerialName("ally_healing")
        val allyHealing: Double = 0.0,
    )

    @Serializable
    data class CurrencyData(
        @SerialName("coin_purse")
        val coins: Double = 0.0,
        @SerialName("motes_purse")
        val motes: Double = 0.0,
        val essence: Map<String, EssenceData> = emptyMap(),
    )

    @Serializable
    data class EssenceData(
        val current: Int = 0
    )

    @Serializable
    data class JacobsContestData(
        @SerialName("medals_inv")
        val medalsInv: Map<String, Int> = emptyMap(),
        //val perks: Map<String, Int> = emptyMap(), //todo cancer
        val contests: Map<String, ContestData> = emptyMap(),
        val talked: Boolean = false,
        @SerialName("unique_brackets")
        val uniqueBrackets: Map<String, List<String>> = emptyMap(),
        val migration: Boolean = false,
        @SerialName("personal_bests")
        val personalBests: Map<String, Long> = emptyMap(),
    )

    @Serializable
    data class ContestData(
        val collected: Long = 0,
        @SerialName("claimed_rewards")
        val claimed: Boolean = false,
        @SerialName("claimed_position")
        val position: Long = 0,
        @SerialName("claimed_participants")
        val participants: Long = 0,
    )

    @Serializable
    data class MiscItemData(
        val soulflow: Long = 0,
        @SerialName("favorite_arrow")
        val favoriteArrow: String? = null,
    )

    @Serializable
    data class LevelingData(
        val experience: Long = 0,
        val completions: Map<String, Int> = emptyMap(),
        val completed: List<String> = emptyList(),
        @SerialName("migrated_completions")
        val migratedComps: Boolean = false,
        @SerialName("completed_tasks")
        val completedTasks: List<String> = emptyList(),
        @SerialName("category_expanded")
        val expanded: Boolean = false,
        @SerialName("last_viewed_tasks")
        val lastViewedTasks: List<String> = emptyList(),
        @SerialName("highest_pet_score")
        val highestPetScore: Int = 0,
        @SerialName("mining_fiesta_ores_mined")
        val fiestaOres: Long = 0,
        val migrated: Boolean = false,
        @SerialName("migrated_completions_2")
        val migratedCompletions2: Boolean = false,
        @SerialName("fishing_festival_sharks_killed")
        val sharksKilled: Long = 0,
        @SerialName("claimed_talisman")
        val talisman: Boolean = false,
        @SerialName("bop_bonus")
        val bopBonus: String = "",
        @SerialName("emblem_unlocks")
        val emblemUnlocks: List<String> = emptyList(),
        @SerialName("task_sort")
        val taskSort: String = "",
        @SerialName("selected_symbol")
        val selectedSymbol: String? = null,
    )

    @Serializable
    data class AccessoryBagStorage(
        val tuning: TuningData = TuningData(),
        @SerialName("selected_power")
        val selectedPower: String? = null,
        @SerialName("unlocked_powers")
        val unlockedPowers: List<String> = emptyList(),
        @SerialName("bag_upgrades_purchased")
        val bagUpgrades: Int = 0,
        @SerialName("highest_magical_power")
        val highestMP: Long = 0,
    )

    @Serializable
    data class TuningData(
        @SerialName("slot_0")
        val currentTunings: Map<String, Int> = emptyMap(),
        val highestUnlockedSlot: Int = 0,
    )

    @Serializable
    data class GardenPlayerData(
        val copper: Int = 0,
    )

    @Serializable
    data class EventsData(
        val easter: EasterEvent = EasterEvent()
    )

    @Serializable
    data class EasterEvent(
        //todo this nonsense //val rabbits: Map<String, Int> = emptyMap()
        @SerialName("time_tower")
        val timeTower: TimeTowerData = TimeTowerData(),
        val employees: Map<String, Int> = emptyMap(),
        @SerialName("total_chocolate")
        val totalChocolate: Long = 0,
        @SerialName("last_viewed_chocolate_factory")
        val lastViewed: Long? = null,
        @SerialName("rabbit_barn_capacity_level")
        val barnCapacity: Int = 0,
        val shop: EasterShopData = EasterShopData(),
        @SerialName("chocolate_level")
        val chocolateLevel: Int = 0,
        val chocolate: Long = 0,
        @SerialName("chocolate_since_prestige")
        val chocolateSincePrestige: Long = 0,
        @SerialName("click_upgrades")
        val clickUpgrades: Int = 0,
        @SerialName("chocolate_multiplier_upgrades")
        val chocolateMultiplierUpgrades: Int = 0,
        @SerialName("rabbit_rarity_upgrades")
        val rabbitRarityUpgrades: Int = 0,
    )

    @Serializable
    data class EasterShopData(
        val year: Int? = null,
        val rabbits: List<String> = emptyList(),
        @SerialName("rabbits_purchases")
        val rabbitsPurchases: List<String> = emptyList(),
        @SerialName("chocolate_spent")
        val chocolateSpent: Long = 0,
    )

    @Serializable
    data class TimeTowerData(
        val charges: Int = 0,
        @SerialName("activation_time")
        val activationTime: Long? = null,
        val level: Int = 0,
        @SerialName("last_charge_time")
        val lastChargeTime: Long? = null,
    )

    @Serializable
    data class PlayerData(
        @SerialName("visited_zones")
        val visitedZones: List<String> = emptyList(),
        @SerialName("last_death")
        val lastDeath: Long? = null,
        val perks: Map<String, Int> = emptyMap(), //todo this maybe? seems annoying. probably need a custom basic deserializer
        @SerialName("achievement_spawned_island_types")
        val achievementSpawnedIslandTypes: List<String> = emptyList(),
        @SerialName("active_effects")
        val activeEffects: List<ActiveEffect> = emptyList(),
        @SerialName("paused_effects")
        val pausedEffect: List<ActiveEffect> = emptyList(),
        @SerialName("temp_stat_buffs")
        val tempStatBuffs: List<TempStatBuff> = emptyList(),
        @SerialName("death_count")
        val deathCount: Long = 0,
        @SerialName("disabled_potion_effects")
        val disabledPotionEffects: List<String> = emptyList(),
        @SerialName("visited_modes")
        val vistedModes: List<String> = emptyList(),
        @SerialName("unlocked_coll_tiers")
        val unLockedCollTiers: List<String> = emptyList(),
        @SerialName("crafted_generators")
        val craftedGenerators: List<String> = emptyList(),
        @SerialName("fishing_treasure_caught")
        val fishingTreasureCaught: Long = 0,
        val experience: Map<String, Double> = emptyMap(),
        @SerialName("fastest_target_practice")
        val fastestTargetPractice: Double? = null,
    )

    @Serializable
    data class Experience( //maybe useful but accessing as a list is better for most situations.
        @SerialName("SKILL_FISHING")
        val fishing: Double = 0.0,
        @SerialName("SKILL_ALCHEMY")
        val alchemy: Double = 0.0,
        @SerialName("SKILL_RUNECRAFTING")
        val runeCrafting: Double = 0.0,
        @SerialName("SKILL_MINING")
        val mining: Double = 0.0,
        @SerialName("SKILL_FARMING")
        val farming: Double = 0.0,
        @SerialName("SKILL_ENCHANTING")
        val enchanting: Double = 0.0,
        @SerialName("SKILL_TAMING")
        val taming: Double = 0.0,
        @SerialName("SKILL_FORAGING")
        val foraging: Double = 0.0,
        @SerialName("SKILL_SOCIAL")
        val social: Double = 0.0,
        @SerialName("SKILL_CARPENTRY")
        val carpentry: Double = 0.0,
        @SerialName("SKILL_COMBAT")
        val combat: Double = 0.0,
    )

    @Serializable
    data class TempStatBuff(
        val stat: Int? = null,
        @SerialName("stat_id")
        val statId: String? = null,
        val key: String,
        val amount: Int,
        @SerialName("expire_at")
        val expireAt: Long,
    )

    @Serializable
    data class ActiveEffect(
        val effect: String,
        val level: Int,
        val modifiers: List<EffectModifier> = emptyList(),
        @SerialName("ticks_remaining")
        val ticksRemaining: Long,
        val infinite: Boolean
    )

    @Serializable
    data class EffectModifier(
        val key: String,
        val amp: Int,
    )

    @Serializable
    data class Inventory(
        @SerialName("inv_contents")
        val invContents: InventoryContents = InventoryContents(),
        @SerialName("ender_chest_contents")
        val eChestContents: InventoryContents = InventoryContents(),
        @SerialName("backpack_icons")
        val backpackIcons: Map<String, InventoryContents> = emptyMap(),
        @SerialName("bag_contents")
        val bagContents: Map<String, InventoryContents> = emptyMap(),
        @SerialName("inv_armor")
        val invArmor: InventoryContents = InventoryContents(),
        @SerialName("equipment_contents")
        val equipment: InventoryContents = InventoryContents(),
        @SerialName("wardrobe_equipped_slot")
        val wardrobeEquipped: Int? = null,
        @SerialName("backpack_contents")
        val backpackContents: Map<String, InventoryContents> = emptyMap(),
        @SerialName("sacks_counts")
        val sacks: Map<String, Long> = emptyMap(),
        @SerialName("personal_vault_contents")
        val personalVault: InventoryContents = InventoryContents(),
        @SerialName("wardrobe_contents")
        val wardrobeContents: InventoryContents = InventoryContents()
    )

    @Serializable
    data class BankingData(
        val balance: Double = 0.0,
        val transactions: List<BankTransactions> = emptyList()
    )

    @Serializable
    data class BankTransactions(
        val amount: Double,
        val timestamp: Long,
        val action: String,
        @SerialName("initiator_name")
        val initiator: String,
    )


    @Serializable
    data class InventoryContents(
        val type: Int? = null,
        val data: String = ""
    )

    @Serializable
    data class CommunityUpgrades(
        @SerialName("upgrade_states")
        val upgradeStates: List<CommunityUpgrade> = emptyList(),
    )

    @Serializable
    data class CommunityUpgrade(
        val upgrade: String,
        val tier: Int,
        @SerialName("started_ms")
        val startedMs: Long,
        @SerialName("started_by")
        val startedBy: String,
        @SerialName("claimed_ms")
        val claimedMs: Long,
        @SerialName("claimed_by")
        val claimedBy: String,
        @SerialName("fasttracked")
        val fasttracked: Boolean,
    )
}