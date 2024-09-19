package com.github.subat0m1c.hatecheaters.utils.jsonobjects.skycrypt

import com.github.subat0m1c.hatecheaters.utils.jsonobjects.HypixelApiStats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object ProfileData {

    //dummy hypixel data to parse into

    fun skyCryptToHypixel(profiles: Profiles, name: String, uuid: String = "SkyCryptPlayer"): HypixelApiStats.PlayerInfo {
        return HypixelApiStats.PlayerInfo(
            uuid = uuid,
            name = name,
            skyCrypt = true,
            profileData = HypixelApiStats.ProfilesData(
                profiles = profiles.profiles.entries.map {
                    HypixelApiStats.Profiles(
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
        @SerialName("game_mode")
        val gameMode: String,
        @SerialName("current")
        val current: Boolean,
        @SerialName("raw")
        val raw: HypixelApiStats.MemberData, // only gonna use rawdata so its perfectly compatable with hypixel format. (i think)
        //@SerialName("data")
        //val data: ProfileData,
    )

    /**@Serializable
    data class ProfileRaw(
        @SerialName("accessory_bag_storage")
        val accessoryBagStorage: AccessoryBagStorage? = null
    )

    @Serializable
    data class AccessoryBagStorage(
        @SerialName("tuning")
        val tuning: AccessoryBagTuning? = null,
        @SerialName("selected_power")
        val selectedPower: String? = null,
        @SerialName("unlocked_powers")
        val unlockedPowers: List<String>? = null,
        @SerialName("bag_upgrades_purchased")
        val bagUpgradesPurchased: Int = 0,
        @SerialName("highest_magical_power")
        val highestMagicalPower: Int = 0,
    )

    @Serializable
    data class AccessoryBagTuning(
        @SerialName("highest_unlocked_slot")
        val highestUnlockedSlot: Int? = null,
        @SerialName("slot_0")
        val currentTunings: Map<String, Int>? = null
    )

    @Serializable
    data class ProfileData(
        @SerialName("stats")
        val stats: Map<String, Double>,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("rank_prefix")
        val rankPrefix: String,
        @SerialName("uuid")
        val uuid: String,
        @SerialName("skin_data")
        val skinData: SkinData,
        @SerialName("profile")
        val profile: ProfileInfo,
        @SerialName("profiles")
        val profiles: Map<String, ProfileInfo>,
        @SerialName("members")
        val members: List<Member>,
        @SerialName("social")
        val social: Map<String, String>,
        @SerialName("visited_zones")
        val visitedZones: List<String>,
        @SerialName("visited_modes")
        val visitedModes: List<String>,
        @SerialName("perks")
        val perks: Map<String, Int>,
        @SerialName("harp_quest")
        val harpQuest: HarpQuest,
        @SerialName("fairy_souls")
        val fairySouls: FairySouls? = null,
        @SerialName("slayer")
        val slayer: Slayer? = null,
        @SerialName("kills")
        val kills: ProfileKills? = null,
        @SerialName("deaths")
        val deaths: ProfileDeaths? = null,
        @SerialName("minions")
        val minions: Minions,
        @SerialName("bestiary")
        val bestiary: Bestiary? = null,
        @SerialName("fishing")
        val fishing: Fishing? = null,
        @SerialName("farming")
        val farming: Farming? = null,
        @SerialName("enchanting")
        val enchanting: Enchanting? = null,
        @SerialName("crimson_isle")
        val crimsonIsle: CrimsonIsle? = null,
        @SerialName("misc")
        val misc: Misc? = null,
        @SerialName("bingo")
        val bingo: Bingo? = null,
        @SerialName("user_data")
        val userData: UserData,
        @SerialName("currencies")
        val currencies: Moneys,
        @SerialName("temp_stats")
        val tempStats: TempStats,
        @SerialName("rift")
        val rift: Rift? = null,
        @SerialName("mining")
        val mining: Mining,
        @SerialName("skyblock_level")
        val skyblockLevel: Level,
        @SerialName("dungeons")
        val dungeons: Dungeons? = null,
        @SerialName("skills")
        val skills: Skills,
        @SerialName("weight")
        val weight: Weight,
        @SerialName("collections")
        val collections: Collections? = null,
        @SerialName("items")
        val items: ItemData,
        @SerialName("accessories")
        val accessories: AccessoryData? = null,
        @SerialName("pets")
        val pets: PetsData? = null,
        @SerialName("networth")
        val networth: Networth,
        @SerialName("errors")
        val errors: Map<String, JsonElement>? = null,
    )

    @Serializable
    data class AccessoryData(
        @SerialName("missing")
        val missing: List<MissingTalisman>,
        @SerialName("upgrades")
        val upgrades: List<MissingTalisman>,
        @SerialName("unique")
        val unique: Int,
        @SerialName("total")
        val total: Int,
        @SerialName("recombobulated")
        val recombobulated: Int,
        @SerialName("total_recombobulated")
        val totalRecombobulated: Int,
        @SerialName("magical_power")
        val magicalPower: AccessoryMagicPower,
    )

    @Serializable
    data class AccessoryMagicPower(
        @SerialName("accessories")
        val accessories: Int,
        @SerialName("abiphone")
        val abiphone: Int,
        @SerialName("rift_prism")
        val riftPrism: Int,
        @SerialName("hegemony")
        val hegemony: Int,
        @SerialName("total")
        val total: Int,
        @SerialName("rarities")
        val rarities: Map<String, AccessoryMPRarities>
    )

    @Serializable
    data class AccessoryMPRarities(
        @SerialName("amount")
        val amount: Int,
        @SerialName("magical_power")
        val magicalPower: Int,
    )

    @Serializable
    data class MissingTalisman(
        @SerialName("_id")
        val idString: String,
        @SerialName("id")
        val idLong: Long,
        @SerialName("category")
        val category: String,
        @SerialName("damage")
        val damage: Int,
        @SerialName("item_id")
        val itemId: Int,
        @SerialName("material")
        val material: String,
        @SerialName("name")
        val name: String,
        @SerialName("texture")
        val texture: String? = null,
        @SerialName("tier")
        val tier: String,
        @SerialName("texture_path")
        val texturePath: String? = null,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("rarity")
        val rarity: String,
        @SerialName("extra")
        val extra: MissingExtra,
        @SerialName("tag")
        val tag: MissingTag,
        @SerialName("soulbound")
        val soulbound: String? = null,
        @SerialName("stats")
        val stats: Map<String, Double?> = emptyMap(),
        @SerialName("requirements")
        val requirements: List<MissingRequirements>? = null,
        @SerialName("npc_sell_price")
        val npcSellPrice: Long? = null,
    )

    @Serializable
    data class MissingRequirements(
        @SerialName("type")
        val type: String,
        @SerialName("slayer_boss_type")
        val slayerBossType: String? = null,
        @SerialName("reward")
        val reward: String? = null,
        @SerialName("level")
        val level: Int? = null,
        @SerialName("tier")
        val tier: Int? = null,
    )

    @Serializable
    data class MissingTag(
        @SerialName("display")
        val display: MissingDisplay? = null,
        @SerialName("ExtraAttributes")
        val extraAttributes: MissingExtraAttributes,
    )

    @Serializable
    data class MissingExtraAttributes(
        val id: String
    )

    @Serializable
    data class MissingDisplay(
        @Serializable(with = Serializers.LoreEntry.LoreSerializer::class)
        @SerialName("Lore")
        val lore: Serializers.LoreEntry? = null
    )

    @Serializable
    data class MissingExtra(
        @SerialName("price")
        val price: Double,
    )

    @Serializable
    data class Networth(
        @SerialName("noInventory")
        val noInventory: Boolean,
        @SerialName("networth")
        val networth: Double,
        @SerialName("unsoulboundNetworth")
        val unsoulboundNetworth: Double,
        @SerialName("purse")
        val purse: Double,
        @SerialName("bank")
        val bank: Double,
        @SerialName("types")
        val types: Map<String, NetworthEntry>
    )

    @Serializable
    data class NetworthEntry(
        @SerialName("total")
        val total: Double,
        @SerialName("unsoulboundTotal")
        val unsoulboundTotal: Double
    )

    @Serializable
    data class PetsData(
        @SerialName("pets")
        val pets: List<PetEntry>,
        @SerialName("missing")
        val missing: List<PetEntry>,
        @SerialName("pet_score")
        val petScore: PetScore
    )

    @Serializable
    data class PetScore(
        @SerialName("total")
        val total: Int,
        @SerialName("amount")
        val amount: Int,
        @SerialName("bonus")
        val bonus: Map<String, JsonElement?>? = null,
    )

    @Serializable
    data class PetEntry(
        @SerialName("uuid")
        val uuid: String? = null,
        @SerialName("uniqueId")
        val uniqueId: String? = null,
        @SerialName("type")
        val type: String,
        @SerialName("exp")
        val exp: Double,
        @SerialName("active")
        val active: Boolean,
        @SerialName("tier")
        val tier: String,
        @SerialName("heldItem")
        val heldItem: String? = null,
        @SerialName("candyUsed")
        val candyUsed: Int,
        @SerialName("skin")
        val skin: String? = null,
        @SerialName("level")
        val level: PetLevel,
        @SerialName("xpMax")
        val xpMax: Long? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("id")
        val id: String? = null,
        @SerialName("price")
        val price: Double? = null,
        @SerialName("base")
        val base: Double? = null,
        @SerialName("calculation")
        val calculation: List<PetCalc>? = null,
        @SerialName("soulbound")
        val soulbound: Boolean? = null,
        @SerialName("stats")
        val stats: Map<String, Double?>,
        @SerialName("texture_path")
        val texturePath: String? = null,
        @SerialName("ref")
        val ref: PetReference,
        @SerialName("tag")
        val tag: PetNBTTag,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("emoji")
        val emoji: String,
        @SerialName("extra")
        val extra: Map<String, JsonElement?>? = null,
    )

    @Serializable
    data class PetNBTTag(
        @SerialName("display")
        val display: ItemDisplay
    )

    @Serializable
    data class PetReference(
        @SerialName("rarity")
        val rarity: Int,
        @SerialName("level")
        val level: Int,
        @SerialName("profile")
        val profile: JsonElement? = null,
        @SerialName("extra")
        val extra: Map<String, JsonElement?>? = null,
    )

    @Serializable
    data class PetCalc(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: String,
        @SerialName("price")
        val price: Int,
        @SerialName("count")
        val count: Int,
    )

    @Serializable
    data class PetLevel(
        @SerialName("level")
        val level: Int,
        @SerialName("xpCurrent")
        val xpCurrent: Double? = null,
        @SerialName("xpForNext")
        val xpForNext: Double,
        @SerialName("progress")
        val progress: Double,
        @SerialName("xpMaxLevel")
        val xpMaxLevel: Long
    )

    @Serializable
    data class ItemData(
        @SerialName("armor")
        val armor: ArmorData,
        @SerialName("equipment")
        val equipment: Map<String, List<ItemEntry>>,
        @SerialName("wardrobe")
        val wardrobe: List<List<ItemEntry?>> = emptyList(),
        @SerialName("wardrobe_inventory")
        val wardrobeInventory: List<ItemEntry>,
        @SerialName("inventory")
        val inventory: List<ItemEntry>,
        @SerialName("enderchest")
        val enderchest: List<ItemEntry>,
        @SerialName("accessory_bag")
        val accessoryBag: List<ItemEntry>,
        @SerialName("fishing_bag")
        val fishingBag: List<ItemEntry>,
        @SerialName("quiver")
        val quiver: List<ItemEntry>,
        @SerialName("potion_bag")
        val potionBag: List<ItemEntry>,
        @SerialName("personal_vault")
        val personalVault: List<ItemEntry>,
        @SerialName("storage")
        val storage: List<ItemEntry>,
        @SerialName("hotm")
        val hotm: List<ItemEntry>,
        @SerialName("candy_bag")
        val candyBag: List<ItemEntry>,
        @SerialName("museumItems")
        val museumItems: List<ItemEntry>,
        @SerialName("museum")
        val museum: List<ItemEntry>,
        @SerialName("bingo_card")
        val bingoCard: List<ItemEntry>,
        @SerialName("accessories")
        val accessories: Accessories,
        @SerialName("weapons")
        val weapons: Weapons,
        @SerialName("farming_tools")
        val farmingTools: toolList,
        @SerialName("mining_tools")
        val miningTools: toolList,
        @SerialName("fishing_tools")
        val fishingTools: toolList,
        @SerialName("pets")
        val pets: List<PetEntry>,
        @SerialName("disabled")
        val disabled: Disabled,
        @SerialName("allItems")
        val allItems: List<ItemEntry>,
    )

    @Serializable
    data class ArmorData(
        @SerialName("armor")
        val armor: List<ItemEntry>,
        @SerialName("set_rarity")
        val setRarity: String? = null,
    )

    @Serializable
    data class Disabled(
        @SerialName("inventory")
        val inventory: Boolean,
        @SerialName("personal_vault")
        val personalVault: Boolean
    )

    @Serializable
    data class toolList(
        @SerialName("tools")
        val tools: List<ItemEntry>,
        @SerialName("highest_priority_tool")
        val highestPriorityTool: ItemEntry? = null,
    )

    @Serializable
    data class Weapons(
        @SerialName("weapons")
        val weapons: List<ItemEntry>,
        @SerialName("highest_priority_weapon")
        val highestPriorityWeapon: ItemEntry? = null,
    )

    @Serializable
    data class Accessories(
        @SerialName("accessories")
        val accessories: List<ItemEntry>,
        @SerialName("accessory_ids")
        val accessoryIds: List<AccessoryId>,
        @SerialName("accessory_rarities")
        val accessoryRarities: AccessoryRarities
    )

    @Serializable
    data class AccessoryRarities(
        @SerialName("common")
        val common: Int,
        @SerialName("uncommon")
        val uncommon: Int,
        @SerialName("rare")
        val rare: Int,
        @SerialName("epic")
        val epic: Int,
        @SerialName("legendary")
        val legendary: Int,
        @SerialName("mythic")
        val mythic: Int,
        @SerialName("special")
        val special: Int,
        @SerialName("very_special")
        val verySpecial: Int,
        @SerialName("hegemony")
        val hegemony: Map<String, String>? = null,
        @SerialName("abicase")
        val abicase: Map<String, String>? = null,
        @SerialName("rift_prism")
        val riftPrism: Boolean? = null,
    )

    @Serializable
    data class AccessoryId(
        @SerialName("id")
        val id: String,
        @SerialName("rarity")
        val rarity: String,
    )

    @Serializable
    data class ItemEntry(
        @SerialName("id")
        val id: Int? = null,
        @SerialName("count")
        val count: Int? = null,
        @SerialName("tag")
        val tag: ItemNBTTag? = null,
        @SerialName("damage")
        val damage: Int? = null,
        @SerialName("containsItems")
        val containsItems: List<ItemEntry>? = null,
        @SerialName("extra")
        val extra: ItemExtra? = null,
        @SerialName("display_name")
        val displayName: String? = null,
        @SerialName("texture_path")
        val texturePath: String? = null,
        @SerialName("rarity")
        val rarity: String? = null,
        @SerialName("categories")
        val categories: List<String?> = emptyList(),
        @SerialName("recombobulated")
        val recombobulated: Boolean? = null,
        @SerialName("dungeon")
        val dungeon: Boolean? = null,
        @SerialName("shiny")
        val shiny: Boolean? = null,
        @SerialName("item_index")
        val itemIndex: Long? = null,
        @SerialName("itemId")
        val itemId: String,
        @SerialName("glowing")
        val glowing: Boolean? = null,
        @SerialName("base_name")
        val baseName: String? = null,
        @SerialName("enrichment")
        val enrichment: String? = null,
        @SerialName("inBackpack")
        val inBackpack: Boolean? = null,
        @SerialName("backpackIndex")
        val backpackIndex: Int? = null,
    )

    @Serializable
    data class ItemExtra(
        @SerialName("hpbs")
        val hpbs: Int? = null,
        @SerialName("recombobulated")
        val recombobulated: Boolean? = null,
        @Serializable(with = Serializers.Timestamp.TimestampSerializer::class)
        @SerialName("timestamp")
        val timestamp: Serializers.Timestamp = Serializers.Timestamp.TimeList(emptyList()),
        @SerialName("reforge")
        val reforge: String? = null,
        @SerialName("source")
        val source: String,
        @SerialName("other")
        val other: Map<String, JsonElement>? = null,
    )

    @Serializable
    data class ItemNBTTag(
        @SerialName("ench")
        val ench: List<ItemEnch>? = null,
        @SerialName("CustomPotionEffects")
        val customPotionEffects: List<CustomPotionEffect>? = null,
        @SerialName("unbreakable")
        val unbreakable: Int? = null,
        @SerialName("HideFlags")
        val hideFlags: Int? = null,
        @SerialName("SkullOwner")
        val skullOwner: SkullOwner? = null,
        @SerialName("display")
        val display: ItemDisplay,
        @SerialName("ExtraAttributes")
        val extraAttributes: ExtraAttributes? = null,
    )

    @Serializable
    data class CustomPotionEffect(
        @SerialName("Ambient")
        val ambient: Int,
        @SerialName("Duration")
        val duration: Int,
        @SerialName("Id")
        val id: Int,
        @SerialName("Amplifier")
        val amplifier: Int,
    )

    @Serializable
    data class ItemEnch(
        @SerialName("lvl")
        val lvl: Int,
        @SerialName("id")
        val id: Int,
    )

    @Serializable
    data class ExtraAttributes(
        @SerialName("rarity_upgrades")
        val rarityUpgrades: Int? = null,
        @SerialName("runes")
        val runes: Map<String, Int>? = null,
        @SerialName("baseStatBoostPercentage")
        val baseStatBoostPercentage: Int = -1,
        @SerialName("modifier")
        val modifier: String? = null,
        @SerialName("upgrade_level")
        val upgrade_level: Int? = null,
        @SerialName("dungeon_item_level")
        val dungeonItemLevel: Int? = null,
        @SerialName("id")
        val id: String? = null,
        @SerialName("enchantments")
        val enchantments: Map<String, Int>? = null,
        @SerialName("uuid")
        val uuid: String? = null,
        @SerialName("dye_item")
        val dyeItem: String? = null,
        @SerialName("hot_potato_count")
        val hotPotatoCount: Int? = null,
        @SerialName("attributes")
        val attributes: Map<String, Int>? = null,
        @SerialName("boss_tier")
        val bossTier: Int? = null,
        @SerialName("artOfPeaceApplied")
        val artOfPeaceApplied: Int? = null,
        @SerialName("donated_museum")
        val donatedMuseum: Int? = null,
        @SerialName("originTag")
        val originTag: String? = null,
        @SerialName("gems")
        val gems: Map<String, @Serializable(with = Serializers.Gemstone.GemStoneSerializer::class) Serializers.Gemstone>? = null,
        @SerialName("anvil_uses")
        val anviUses: Int? = null,
    )

    @Serializable
    data class GemstoneData(
        val gems: Map<String, String> = emptyMap(),
        @SerialName("unlocked_slots")
        val unlockedSlots: List<String>? = null,
    )

    @Serializable
    data class ItemDisplay(
        @Serializable(with = Serializers.LoreEntry.LoreSerializer::class)
        @SerialName("Lore")
        val lore: Serializers.LoreEntry? = null,
        @SerialName("color")
        val color: Int? = null,
        @SerialName("name")
        val name: String? = null,
    )

    @Serializable
    data class SkullOwner(
        @SerialName("Id")
        val id: String,
        @SerialName("Properties")
        val properties: SkullOwnerProperties,
    )

    @Serializable
    data class SkullOwnerProperties(
        @SerialName("textures")
        val textures: List<TextureValue>,
    )

    @Serializable
    data class TextureValue(
        @SerialName("Value")
        val value: String
    )

    @Serializable
    data class Collections(
        @SerialName("farming")
        val farming: CollectionData,
        @SerialName("mining")
        val mining: CollectionData,
        @SerialName("combat")
        val combat: CollectionData,
        @SerialName("foraging")
        val foraging: CollectionData,
        @SerialName("fishing")
        val fishing: CollectionData,
        @SerialName("rift")
        val rift: CollectionData,
        @SerialName("BOSS")
        val boss: CollectionData,
        @SerialName("totalCollections")
        val totalCollections: Int,
        @SerialName("maxedCollections")
        val maxedCollections: Int,
    )

    @Serializable
    data class CollectionData(
        @SerialName("name")
        val name: String,
        @SerialName("collections")
        val collections: List<CollectionItem>,
        @SerialName("totalTiers")
        val totalTiers: Int,
        @SerialName("maxTiers")
        val maxTiers: Int,
    )

    @Serializable
    data class CollectionItem(
        @SerialName("name")
        val name: String,
        @SerialName("id")
        val id: String? = null,
        @SerialName("texture")
        val texture: String,
        @SerialName("amount")
        val amount: Long,
        @SerialName("totalAmount")
        val totalAmount: Long? = null,
        @SerialName("tier")
        val tier: Int,
        @SerialName("maxTier")
        val maxTier: Int,
        @SerialName("rewards")
        val rewards: List<RewardEntry>? = null,
        @SerialName("amounts")
        val amounts: List<CollectionCollector>? = null,
    )

    @Serializable
    data class RewardEntry(
        @SerialName("name")
        val name: String,
        @SerialName("required")
        val required: Int,
    )

    @Serializable
    data class CollectionCollector(
        @SerialName("username")
        val username: String,
        @SerialName("amount")
        val amount: Long,
    )

    @Serializable
    data class Misc(
        @SerialName("races")
        val races: Races? = null,
        @SerialName("gifts")
        val gifts: Gifts? = null,
        @SerialName("winter")
        val winter: Winter? = null,
        @SerialName("dragons")
        val dragons: Dragons? = null,
        @SerialName("endstone_protector")
        val endstoneProtector: EndstoneProtector? = null,
        @SerialName("damage")
        val damage: MiscDamage? = null,
        @SerialName("pet_milestones")
        val petMilestones: Map<String, PetMilestoneData> = emptyMap(),
        @SerialName("mythological_event")
        val mythologicalEvent: MythosEvent? = null,
        @SerialName("effects")
        val effects: Effects,
        @SerialName("profile_upgrades")
        val profileUpgrades: Map<String, Int> = emptyMap(),
        @SerialName("auctions")
        val auctions: Auctions? = null,
        @SerialName("claimed_items")
        val claimedItems: Map<String, Long> = emptyMap(),
        @SerialName("uncategorized")
        val uncategorized: Map<String, UncategorizedData> = emptyMap(),
    )

    @Serializable
    data class Weight(
        @SerialName("senither")
        val senither: SenitherWeight,
        @SerialName("lily")
        val lily: LilyWeight,
        @SerialName("farming")
        val farming: FarmingWeight,
    )

    @Serializable
    data class FarmingWeight(
        @SerialName("weight")
        val weight: Int,
        @SerialName("crops")
        val crops: JsonElement? = null, //todo get crop data
        @SerialName("bonuses")
        val bonuses: Map<String, Map<String, Int>>,
        @SerialName("crop_weight")
        val cropWeight: Int,
        @SerialName("bonus_weight")
        val bonusWeight: Int,
    )

    @Serializable
    data class LilyWeight(
        @SerialName("total")
        val total: Double,
        @SerialName("skill")
        val skill: LilySkill,
        @SerialName("catacombs")
        val catacombs: LilyCatacombs,
        @SerialName("slayer")
        val slayer: Double
    )

    @Serializable
    data class LilyCatacombs(
        @SerialName("completion")
        val completion: LilyCataComps,
        @SerialName("experience")
        val experience: Double
    )

    @Serializable
    data class LilyCataComps(
        @SerialName("base")
        val base: Double,
        @SerialName("master")
        val master: Double,
    )

    @Serializable
    data class LilySkill(
        @SerialName("base")
        val base: Double,
        @SerialName("overflow")
        val overflow: Double,
    )

    @Serializable
    data class SenitherWeight(
        @SerialName("overall")
        val overall: Double,
        @SerialName("dungeon")
        val dungeon: SenitherDungeon,
        @SerialName("skill")
        val skill: SenitherSkills,
        @SerialName("slayer")
        val slayer: SenitherSlayers,
    )

    @Serializable
    data class SenitherSkills(
        @SerialName("total")
        val total: Double,
        @SerialName("skills")
        val skills: Map<String, SenitherWeightSkill>,
    )

    @Serializable
    data class SenitherSlayers(
        @SerialName("total")
        val total: Double,
        @SerialName("slayers")
        val slayers: Map<String, SenitherWeightSkill>,
    )

    @Serializable
    data class SenitherDungeon(
        @SerialName("total")
        val total: Double,
        @SerialName("dungeons")
        val dungeons: Map<String, SenitherWeightItem>,
        @SerialName("classes")
        val classes: Map<String, SenitherWeightItem>,
    )

    @Serializable
    data class SenitherWeightItem(
        @SerialName("weight")
        val weight: Double,
        @SerialName("weight_overflow")
        val weightOverflow: Double,
        @SerialName("total_weight")
        val totalWeight: Double? = null,
    )

    @Serializable
    data class SenitherWeightSkill(
        @SerialName("weight")
        val weight: Double,
        @SerialName("overflow_weight")
        val overflowWeight: Double,
        @SerialName("total_weight")
        val totalWeight: Double? = null,
    )

    @Serializable
    data class Skills(
        @SerialName("skills")
        val skills: Map<String, Level>,
        @SerialName("averageSkillLevel")
        val averageSkillLevel: Float,
        @SerialName("averageSkillLevelWithoutProgress")
        val averageSkillLevelWithoutProgress: Float,
        @SerialName("totalSkillXp")
        val totalSkillXp: Double
    )

    @Serializable
    data class Mining(
        @SerialName("commissions")
        val commissions: CommissionData,
        @SerialName("forge")
        val forge: ForgeData,
        @SerialName("core")
        val core: CoreData? = null,
    )

    @Serializable
    data class CoreData(
        @SerialName("level")
        val level: Level,
        @SerialName("tokens")
        val tokens: CoreTokenData,
        @SerialName("selected_pickaxe_ability")
        val selectedPickaxeAbility: String? = null,
        @SerialName("powder")
        val powder: Map<String, PowderData>,
        @SerialName("crystal_nucleus")
        val crystalNucleus: CrystalNucleus,
        @SerialName("daily_ores")
        val dailyOres: DailyOres,
        @SerialName("hotm_last_reset")
        val hotmLastReset: Long,
        @SerialName("crystal_hollows_last_access")
        val lastCHAccess: Long,
        @SerialName("daily_effect")
        val dailyEffect: DailyEffect,
        @SerialName("nodes")
        val nodes: Map<String, JsonElement>,
    )

    @Serializable
    data class DailyEffect(
        @SerialName("effect")
        val effect: String? = null,
        @SerialName("last_changes")
        val lastChanges: Int? = null,
    )

    @Serializable
    data class DailyOres(
        @SerialName("ores")
        val ores: Map<String, OreData>,
        @SerialName("mined")
        val mined: Int? = null,
        @SerialName("day")
        val day: Int? = null,
    )

    @Serializable
    data class OreData(
        @SerialName("day")
        val day: Int? = null,
        @SerialName("count")
        val count: Int? = null,
    )

    @Serializable
    data class CrystalNucleus(
        @SerialName("times_completed")
        val timesCompleted: Int,
        @SerialName("crystals")
        val crystals: Map<String, CrystalData>,
        @SerialName("precursor")
        val precursor: PrecursorData? = null, //todo get precursor data
    )

    @Serializable
    data class PrecursorData(
        @SerialName("parts_delivered")
        val partsDelivered: List<String> = emptyList(),
        @SerialName("talked_to_professor")
        val talkedToProfessor: Boolean? = null
    )

    @Serializable
    data class CrystalData(
        @SerialName("state")
        val state: String? = null,
        @SerialName("total_placed")
        val totalPlaced: Int? = null,
        @SerialName("total_found")
        val totalFound: Int? = null,
    )

    @Serializable
    data class PowderData(
        @SerialName("total")
        val total: Long,
        @SerialName("spent")
        val spent: Long,
        @SerialName("available")
        val available: Long,
    )

    @Serializable
    data class CoreTokenData(
        @SerialName("total")
        val total: Int,
        @SerialName("spent")
        val spent: Int,
        @SerialName("available")
        val available: Int,
    )

    @Serializable
    data class ForgeData(
        @SerialName("processes")
        val processes: List<JsonElement?> = emptyList() //todo find forge content data
    )

    @Serializable
    data class CommissionData(
        @SerialName("milestone")
        val milestone: Int,
        @SerialName("completions")
        val completions: Int,
    )

    @Serializable
    data class Rift(
        @SerialName("motes")
        val motes: MotesData,
        @SerialName("enigma")
        val enigma: EnigmaData,
        @SerialName("wither_cage")
        val witherCage: WitherCage,
        @SerialName("timecharms")
        val timeCharms: TimeCharms,
        @SerialName("dead_cats")
        val deadCats: DeadCats,
        @SerialName("castle")
        val castle: RiftCastleData
    )

    @Serializable
    data class RiftCastleData(
        @SerialName("grubber_stacks")
        val grubberStacks: Int,
        @SerialName("max_burgers")
        val maxBurgers: Int
    )

    @Serializable
    data class DeadCats(
        @SerialName("montezuma")
        val montezuma: Montezuma,
        @SerialName("found_cats")
        val foundCats: List<String> = emptyList()
    )

    @Serializable
    data class Montezuma(
        @SerialName("uuid")
        val uuid: String? = null,
        @SerialName("uniqueId")
        val uniqueId: String? = null,
        @SerialName("type")
        val type: String? = null,
        @SerialName("exp")
        val exp: Double? = null,
        @SerialName("active")
        val active: Boolean? = null,
        @SerialName("tier")
        val tier: String? = null,
        @SerialName("heldItem")
        val heldItem: String? = null,
        @SerialName("candyUsed")
        val candyUsed: Int? = null,
        @SerialName("skin")
        val skin: String? = null,
        @SerialName("level")
        val level: PetLevel? = null,
        @SerialName("xpMax")
        val xpMax: Long? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("id")
        val id: String? = null,
        @SerialName("price")
        val price: Double? = null,
        @SerialName("base")
        val base: Double? = null,
        @SerialName("calculation")
        val calculation: List<PetCalc>? = null,
        @SerialName("soulbound")
        val soulbound: Boolean? = null,
        @SerialName("stats")
        val stats: Map<String, Double?> = emptyMap(),
        @SerialName("texture_path")
        val texturePath: String? = null,
        @SerialName("ref")
        val ref: PetReference? = null,
        @SerialName("tag")
        val tag: PetNBTTag? = null,
        @SerialName("display_name")
        val display_name: String? = null,
        @SerialName("emoji")
        val emoji: String? = null,
        @SerialName("extra")
        val extra: Map<String, JsonElement?>? = null,
    )

    @Serializable
    data class TimeCharms(
        @SerialName("timecharms")
        val timeCharms: List<TimeCharm>,
        @SerialName("obtained_timecharms")
        val obtainedTimeCharms: Int,
    )

    @Serializable
    data class TimeCharm(
        @SerialName("name")
        val name: String,
        @SerialName("type")
        val type: String,
        @SerialName("id")
        val id: Int,
        @SerialName("damage")
        val damage: Int,
        @SerialName("unlocked_at")
        val unlockedAt: Long? = null,
    )

    @Serializable
    data class WitherCage(
        @SerialName("killed_eyes")
        val killedEyes: List<KilledEye>
    )

    @Serializable
    data class KilledEye(
        @SerialName("name")
        val name: String,
        @SerialName("texture")
        val texture: String,
        @SerialName("unlocked")
        val unlocked: Boolean? = null,
    )

    @Serializable
    data class EnigmaData(
        @SerialName("souls")
        val souls: Int,
        @SerialName("total_souls")
        val totalSouls: Int,
    )


    @Serializable
    data class MotesData(
        @SerialName("purse")
        val purse: Double,
        @SerialName("lifetime")
        val lifetime: Double,
        @SerialName("orbs")
        val orbs: Int,
    )

    @Serializable
    data class TempStats(
        @SerialName("century_cakes")
        val centuryCakes: List<CenturyCake>
    )

    @Serializable
    data class CenturyCake(
        @SerialName("stat")
        val stat: String,
        @SerialName("amount")
        val amount: Int,
    )

    @Serializable
    data class Moneys(
        @SerialName("bank")
        val bank: Double,
        @SerialName("purse")
        val purse: Double,
    )

    @Serializable
    data class UserData(
        @SerialName("first_join")
        val firstJoin: UnixTimeData,
        @SerialName("current_area")
        val currentArea: CurrentArea
    )

    @Serializable
    data class CurrentArea(
        @SerialName("current_area")
        val currentArea: String = "missing",
        @SerialName("current_area_updated")
        val currentAreaUpdated: Boolean? = null,
    )

    @Serializable
    data class Bingo(
        @SerialName("profiles")
        val profiles: Int,
        @SerialName("points")
        val points: Int,
        @SerialName("completed_goals")
        val completedGoals: Int,
    )

    @Serializable
    data class UncategorizedData(
        @SerialName("raw")
        val raw: JsonElement? = null,
        @SerialName("formatted")
        val formatted: JsonElement? = null,
        @SerialName("maxed")
        val maxed: Boolean? = true,
    )

    @Serializable
    data class Auctions(
        @SerialName("highest_bid")
        val highestBid: Long = 0,
        @SerialName("fees")
        val fees: Long = 0,
        @SerialName("won")
        val won: Int = 0,
        @SerialName("no_bids")
        val noBids: Int = 0,
        @SerialName("created")
        val created: Int = 0,
        @SerialName("gold_spent")
        val goldSpent: Long = 0,
        @SerialName("gold_earned")
        val goldEarned: Long = 0,
        @SerialName("total_bought")
        val totalBought: Map<String, Int> = emptyMap(),
        @SerialName("completed")
        val completed: Int = 0,
        @SerialName("total_sold")
        val totalSold: Map<String, Int> = emptyMap(),
    )

    @Serializable
    data class Effects(
        @SerialName("active")
        val active: List<EffectData>,
        @SerialName("paused")
        val paused: List<EffectData>,
        @SerialName("disabled")
        val disabled: List<String>,
    )

    @Serializable
    data class EffectData(
        @SerialName("effect")
        val effect: String,
        @SerialName("level")
        val level: Int,
        @SerialName("modifiers")
        val modifiers: List<EffectModifier>,
        @SerialName("ticks_remaining")
        val ticksRemaining: Long,
        @SerialName("infinite")
        val infinite: Boolean,
    )

    @Serializable
    data class EffectModifier(
        @SerialName("key")
        val key: String,
        @SerialName("amp")
        val amp: Int,
    )

    @Serializable
    data class MythosEvent(
        @SerialName("burrows_dug_treasure")
        val burrowsDugTreasure: BurrowsDugData? = null,
        @SerialName("burrows_chains_complete")
        val burrowsChainsComplete: BurrowsDugData? = null,
        @SerialName("kills")
        val kills: Int = 0,
        @SerialName("burrows_dug_combat")
        val burrowsDugCombat: BurrowsDugData? = null,
        @SerialName("burrows_dug_next")
        val burrowsDugNext: BurrowsDugData? = null,
    )

    @Serializable
    data class BurrowsDugData(
        @SerialName("total")
        val total: Int? = 0,
        @SerialName("UNCOMMON")
        val uncommon: Int? = 0,
        @SerialName("none")
        val none: Int? = 0,
        @SerialName("COMMON")
        val common: Int? = 0,
        @SerialName("LEGENDARY")
        val legendary: Int? = 0,
    )

    @Serializable
    data class PetMilestoneData(
        @SerialName("amount")
        val amount: Long,
        @SerialName("rarity")
        val rarity: String,
        @SerialName("total")
        val total: Long,
        @SerialName("progress")
        val progress: Double
    )

    @Serializable
    data class MiscDamage(
        @SerialName("highest_critical_damage")
        val highestCriticalDamage: Double
    )

    @Serializable
    data class EndstoneProtector(
        @SerialName("kills")
        val kills: Int,
        @SerialName("deaths")
        val deaths: Int,
    )

    @Serializable
    data class Dragons(
        @SerialName("ender_crystals_destroyed")
        val enderCrystalsDestroyed: Int? = 0,
        @SerialName("most_damage")
        val mostDamage: Map<String, Double> = emptyMap(),
        @SerialName("fastest_kill")
        val fastestKill: Map<String, Int> = emptyMap(),
        @SerialName("kills")
        val kills: Map<String, Int>,
        @SerialName("deaths")
        val deaths: DragonDeaths,
    )

    @Serializable
    data class DragonDeaths(
        @SerialName("total")
        val total: Int,
    )

    @Serializable
    data class Winter(
        @SerialName("most_snowballs_hit")
        val mostSnowballsHit: Int,
        @SerialName("most_damage_dealt")
        val mostDamageDealt: Int,
        @SerialName("most_magma_damage_dealt")
        val mostMagmaDamageDealt: Int,
        @SerialName("most_cannonballs_hit")
        val mostCannonballsHit: Int,
    )

    @Serializable
    data class Gifts(
        @SerialName("given")
        val given: Long,
        @SerialName("received")
        val received: Long,
    )

    @Serializable
    data class Races(
        @SerialName("other")
        val other: OtherRace? = null,
        @SerialName("giant_mushroom")
        val giantMushroom: RaceTypeData? = null,
        @SerialName("precursor_ruins")
        val precursorRuins: RaceTypeData? = null,
        @SerialName("crystal_core")
        val crystalCore: RaceTypeData? = null,
    )

    @Serializable
    data class RaceTypeData(
        @SerialName("name")
        val name: String,
        @SerialName("races")
        val races: Map<String, Map<String, OtherRaceData>>,
    )

    @Serializable
    data class DungeonRaceStuff(
        @SerialName("anything")
        val anything: OtherRaceData
    )

    @Serializable
    data class OtherRace(
        @SerialName("name")
        val name: String,
        @SerialName("races")
        val races: Map<String, OtherRaceData>
    )

    @Serializable
    data class OtherRaceData(
        @SerialName("name")
        val name: String,
        @SerialName("time")
        val time: String,
    )

    @Serializable
    data class CrimsonIsle(
        @SerialName("factions")
        val factions: IsleFactions,
        @SerialName("kuudra")
        val kuudra: Kuudra,
        @SerialName("dojo")
        val dojo: Dojo? = null,
        @SerialName("trophy_fish")
        val trophyFish: TrophyFish? = null,
        @SerialName("abiphone")
        val abiphone: Abiphone,
    )

    @Serializable
    data class Abiphone(
        @SerialName("contacts")
        val contacts: Map<String, ContactData>,
        @SerialName("active")
        val active: Int,
    )

    @Serializable
    data class ContactData(
        @SerialName("incoming_calls_count")
        val incomingCallsCount: Int? = null,
        @SerialName("talked_to")
        val talkedTo: Boolean? = null,
        @SerialName("last_call_incoming")
        val lastCallIncoming: Long? = null,
        @SerialName("completed_quest")
        val completedQuest: Boolean? = null,
        @SerialName("specific")
        val specific: Map<String, JsonElement>? = null,
    )

    @Serializable
    data class TrophyFish(
        @SerialName("fish")
        val fish: List<TrophyFishData>,
        @SerialName("total_caught")
        val totalCaught: Long,
        @SerialName("maxed")
        val maxed: Boolean,
        @SerialName("stage")
        val stage: String? = null,
    )

    @Serializable
    data class TrophyFishData(
        @SerialName("total")
        val total: Int,
        @SerialName("bronze")
        val bronze: Int,
        @SerialName("silver")
        val silver: Int,
        @SerialName("gold")
        val gold: Int,
        @SerialName("diamond")
        val diamond: Int,
        @SerialName("highest_tier")
        val highestTier: String? = null,
        @SerialName("texture")
        val texture: String? = null,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("description")
        val description: String,
        @SerialName("textures")
        val textures: TrophyFishTextures? = null,
    )

    @Serializable
    data class TrophyFishTextures(
        @SerialName("bronze")
        val bronze: String,
        @SerialName("silver")
        val silver: String,
        @SerialName("gold")
        val gold: String,
        @SerialName("diamond")
        val diamond: String,
    )

    @Serializable
    data class Dojo(
        @SerialName("dojo")
        val dojo: Map<String, DojoTypeData>,
        @SerialName("total_points")
        val totalPoints: Int,
    )

    @Serializable
    data class DojoTypeData(
        @SerialName("name")
        val name: String,
        @SerialName("id")
        val id: Int,
        @SerialName("damage")
        val damage: Int,
        @SerialName("points")
        val points: Int,
        @SerialName("time")
        val time: Int,
    )

    @Serializable
    data class Kuudra(
        @SerialName("tiers")
        val tiers: Map<String, KuudraTierData>,
        @SerialName("total")
        val total: Int,
    )

    @Serializable
    data class KuudraTierData(
        @SerialName("name")
        val name: String,
        @SerialName("head")
        val head: String,
        @SerialName("completions")
        val completions: Int,
    )

    @Serializable
    data class IsleFactions(
        @SerialName("selected_faction")
        val selectedFaction: String,
        @SerialName("mages_reputation")
        val magesReputation: Int,
        @SerialName("barbarians_reputation")
        val barbariansReputation: Int,
    )

    @Serializable
    data class Enchanting(
        @SerialName("unlocked")
        val unlocked: Boolean,
        @SerialName("experiments")
        val experiments: ExperimentData
    )

    @Serializable
    data class ExperimentData(
        @SerialName("simon")
        val simon: SimonExperiment,
        @SerialName("numbers")
        val numbers: NumbersExperiment,
        @SerialName("pairings")
        val pairings: PairingsExperiment,
    )

    @Serializable
    data class PairingsExperiment(
        @SerialName("name")
        val name: String,
        @SerialName("stats")
        val stats: ExperimentStats,
        @SerialName("tiers")
        val tiers: Map<Int, PairingsTierData>
    )

    @Serializable
    data class PairingsTierData(
        @SerialName("name")
        val name: String,
        @SerialName("icon")
        val icon: String,
        @SerialName("claims")
        val claims: Int,
        @SerialName("best_score")
        val bestScore: Int? = null,
    )

    @Serializable
    data class NumbersExperiment(
        @SerialName("name")
        val name: String,
        @SerialName("stats")
        val stats: ExperimentStats,
        @SerialName("tiers")
        val tiers: Map<Int, ExperimentTierData>
    )

    @Serializable
    data class SimonExperiment(
        @SerialName("name")
        val name: String,
        @SerialName("stats")
        val stats: ExperimentStats,
        @SerialName("tiers")
        val tiers: Map<Int, ExperimentTierData>
    )

    @Serializable
    data class ExperimentTierData(
        @SerialName("name")
        val name: String,
        @SerialName("icon")
        val icon: String,
        @SerialName("attempts")
        val attempts: Int,
        @SerialName("claims")
        val claims: Int,
        @SerialName("best_score")
        val bestScore: Int? = null,
    )

    @Serializable
    data class ExperimentStats(
        @SerialName("bonus_clicks")
        val bonusClicks: Int = 0,
        @SerialName("last_attempt")
        val lastAttempt: UnixTimeData? = null,
        @SerialName("last_claimed")
        val lastClaimed: UnixTimeData? = null,
    )

    @Serializable
    data class UnixTimeData(
        @SerialName("unix")
        val unix: Long,
        @SerialName("text")
        val text: String,
    )

    @Serializable
    data class Farming(
        @SerialName("talked")
        val talked: Boolean,
        @SerialName("pelts")
        val pelts: Int,
        @SerialName("current_badges")
        val currentBadges: Map<String, Int>? = null,
        @SerialName("total_badges")
        val totalBadges: Map<String, Int>? = null,
        @SerialName("perks")
        val perks: Map<String, Int>? = null,
        @SerialName("unique_golds")
        val uniqueGolds: Int = 0,
        @SerialName("unique_platinums")
        val uniquePlatinums: Int = 0,
        @SerialName("unique_diamonds")
        val uniqueDiamonds: Int = 0,
        @SerialName("crops")
        val crops: Map<String, CropData> = emptyMap(),
        @SerialName("contests")
        val contests: Contests? = null,
    )

    @Serializable
    data class Contests(
        @SerialName("attended_contests")
        val attendedContests: Int,
        @SerialName("all_contests")
        val allContests: List<ContestData>
    )

    @Serializable
    data class ContestData(
        @SerialName("date")
        val date: String,
        @SerialName("crop")
        val crop: String,
        @SerialName("collected")
        val collected: Int,
        @SerialName("claimed")
        val claimed: Boolean,
        @SerialName("medal")
        val medal: String? = null,
        @SerialName("placing")
        val placing: ContestPlacement,
    )

    @Serializable
    data class ContestPlacement(
        @SerialName("position")
        val position: Int? = null,
        @SerialName("percentage")
        val percentage: Double? = null,
    )

    @Serializable
    data class CropData(
        @SerialName("name")
        val name: String,
        @SerialName("icon")
        val icon: String,
        @SerialName("attended")
        val attended: Boolean,
        @SerialName("highest_tier")
        val highestTier: String,
        @SerialName("contests")
        val contests: Int,
        @SerialName("personal_best")
        val personalBest: Long,
        @SerialName("badges")
        val badges: Map<String, Int>
    )

    @Serializable
    data class Fishing(
        @SerialName("total")
        val total: Int,
        @SerialName("treasure")
        val treasure: Int,
        @SerialName("treasure_large")
        val treasureLarge: Int,
        @SerialName("shredder_fished")
        val shredderFished: Int,
        @SerialName("shredder_bait")
        val shredderBait: Int,
        @SerialName("trophy_fish")
        val trophyFish: Int
    )

    @Serializable
    data class Bestiary(
        @SerialName("categories")
        val categories: Map<String, BestiaryCategory>,
        @SerialName("milestone")
        val milestone: Int,
        @SerialName("maxMilestone")
        val maxMilestone: Int,
        @SerialName("familiesUnlocked")
        val familiesUnlocked: Int,
        @SerialName("totalFamilies")
        val totalFamilies: Int,
        @SerialName("familiesMaxed")
        val familiesMaxed: Int,
    )

    @Serializable
    data class BestiaryCategory(
        @SerialName("name")
        val name: String,
        @SerialName("texture")
        val texture: String,
        @SerialName("mobs")
        val mobs: List<BestiaryMob>,
        @SerialName("mobsUnlocked")
        val mobsUnlocked: Int,
        @SerialName("mobsMaxed")
        val mobsMaxed: Int
    )

    @Serializable
    data class BestiaryMob(
        @SerialName("name")
        val name: String,
        @SerialName("texture")
        val texture: String,
        @SerialName("kills")
        val kills: Int,
        @SerialName("nextTierKills")
        val nextTierKills: Int? = null,
        @SerialName("maxKills")
        val maxKills: Int,
        @SerialName("tier")
        val tier: Int,
        @SerialName("maxTier")
        val maxTier: Int
    )

    @Serializable
    data class Minions(
        @SerialName("minions")
        val minions: Map<String, MinionData>
    )

    @Serializable
    data class MinionData(
        @SerialName("minions")
        val minions: List<MinionEntryData>,
        @SerialName("totalMinions")
        val totalMinions: Int,
        @SerialName("maxedMinions")
        val maxedMinions: Int,
        @SerialName("unlockedTiers")
        val unlockedTiers: Int,
        @SerialName("unlockableTiers")
        val unlockableTiers: Int,
    )

    @Serializable
    data class MinionEntryData(
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String,
        @SerialName("texture")
        val texture: String,
        @SerialName("tiers")
        val tiers: List<Int>,
        @SerialName("tier")
        val tier: Int,
        @SerialName("maxTier")
        val maxTier: Int
    )

    @Serializable
    data class ProfileKills(
        @SerialName("kills")
        val kills: List<KillDeathData>,
        @SerialName("total")
        val total: Long = 0
    )

    @Serializable
    data class ProfileDeaths(
        @SerialName("deaths")
        val deaths: List<KillDeathData>,
        @SerialName("total")
        val total: Long = 0
    )

    @Serializable
    data class KillDeathData(
        @SerialName("type")
        val type: String,
        @SerialName("entity_id")
        val entityId: String,
        @SerialName("amount")
        val amount: Int,
        @SerialName("entity_name")
        val entityName: String,
    )

    @Serializable
    data class Slayer(
        @SerialName("slayers")
        val slayers: Map<String, SlayerData>? = null,
        @SerialName("total_slayer_xp")
        val totalSlayerXp: Long,
        @SerialName("total_coins_spent")
        val totalCoinsSpent: Long,
    )

    @Serializable
    data class SlayerData(
        @SerialName("level")
        val level: SlayerLevel,
        @SerialName("coins_spent")
        val coinsSpent: Long,
        @SerialName("kills")
        val kills: SlayerKills,
        @SerialName("name")
        val name: String,
        @SerialName("head")
        val head: String,
    )

    @Serializable
    data class SlayerKills(
        @SerialName("1")
        val tier1: Int = 0,
        @SerialName("2")
        val tier2: Int = 0,
        @SerialName("3")
        val tier3: Int = 0,
        @SerialName("4")
        val tier4: Int = 0,
        @SerialName("5")
        val tier5: Int? = null,
        @SerialName("total")
        val total: Int
    )

    @Serializable
    data class SlayerLevel(
        @SerialName("currentLevel")
        val currentLevel: Int,
        @SerialName("xp")
        val xp: Int,
        @SerialName("maxLevel")
        val maxLevel: Int,
        @SerialName("progress")
        val progress: Double,
        @SerialName("xpForNext")
        val xpForNext: Int,
        @SerialName("unclaimed")
        val unclaimed: Boolean,
    )

    @Serializable
    data class SlayerWeight(
        @SerialName("weight")
        val weight: Double,
        @SerialName("weight_overflow")
        val weightOverflow: Double
    )

    @Serializable
    data class FairySouls(
        @SerialName("collected")
        val collected: Int,
        @SerialName("total")
        val total: Int,
        @SerialName("progress")
        val progress: Double,
        @SerialName("fairy_exchanges")
        val fairyExchanges: Int = 0
    )

    @Serializable
    data class HarpQuest(
        @SerialName("selected_song")
        val selectedSong: String? = null,
        @SerialName("selected_song_epoch")
        val selectedSongEpoch: Long? = null,
        @SerialName("claimed_talisman")
        val claimedTalisman: Boolean? = null,
        //todo harp data
    )

    @Serializable
    data class Member(
        @SerialName("uuid")
        val uuid: String,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("skin_data")
        val skinData: SkinData
    )

    @Serializable
    data class ProfileInfo(
        @SerialName("cute_name")
        val cuteName: String,
        @SerialName("profile_id")
        val profileId: String,
        @SerialName("game_mode")
        val gameMode: String? = null,
    )

    @Serializable
    data class SkinData(
        @SerialName("model")
        val model: String,
        @SerialName("skinurl")
        val skinUrl: String,
        @SerialName("capeurl")
        val capeUrl: String? = null
    )

    @Serializable
    data class Level(
        @SerialName("xp")
        val xp: Double,
        @SerialName("level")
        val level: Int,
        @SerialName("maxLevel")
        val maxLevel: Int,
        @SerialName("xpCurrent")
        val xpCurrent: Double,
        @SerialName("xpForNext")
        val xpForNext: Double? = null,
        @SerialName("progress")
        val progress: Double,
        @SerialName("levelCap")
        val levelCap: Int,
        @SerialName("uncappedLevel")
        val uncappedLevel: Int,
        @SerialName("levelWithProgress")
        val levelWithProgress: Double,
        @SerialName("unlockableLevelWithProgress")
        val unlockableLevelWithProgress: Double? = null,
        @SerialName("rank")
        val rank: Int? = null,
        @SerialName("maxExperience")
        val maxExperience: Int? = null
    ) */
}