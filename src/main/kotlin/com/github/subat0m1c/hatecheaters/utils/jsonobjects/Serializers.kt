package com.github.subat0m1c.hatecheaters.utils.jsonobjects

import com.github.subat0m1c.hatecheaters.utils.jsonobjects.Serializers.Gemstone.Gem
import com.github.subat0m1c.hatecheaters.utils.jsonobjects.Serializers.Gemstone.GemstoneData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object Serializers {
    @Serializable
    sealed class Timestamp{
        @Serializable
        data class TimeLong(val value: Long) : Timestamp()

        @Serializable
        data class TimeList(val values: List<Long>) : Timestamp()

        //this makes me want to jump.
        object TimestampSerializer : KSerializer<Timestamp> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Timestamp")

            override fun serialize(encoder: Encoder, value: Timestamp) {
                when (value) {
                    is TimeLong -> encoder.encodeLong(value.value)
                    is TimeList -> encoder.encodeSerializableValue(ListSerializer(Long.serializer()), value.values)
                }
            }

            override fun deserialize(decoder: Decoder): Timestamp {
                val input = decoder as? JsonDecoder ?: error("Can only deserialize with JsonDecoder")
                return when (val jsonElement = input.decodeJsonElement()) {
                    is JsonArray -> TimeList(jsonElement.map { it.jsonPrimitive.long })
                    is JsonPrimitive -> TimeLong(jsonElement.long)
                    else -> error("Unexpected format for Timestamp")
                }
            }
        }
    }

    @Serializable
    sealed class Gemstone{
        @Serializable
        data class Gem(val value: String) : Gemstone()

        @Serializable
        data class GemSlots(val values: List<String>) : Gemstone()

        @Serializable
        data class GemMapped(val value: Map<String, String>) : Gemstone()

        //this makes me want to jump v2.
        object GemStoneSerializer : KSerializer<Gemstone> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Gemstone")

            override fun serialize(encoder: Encoder, value: Gemstone) {
                when (value) {
                    is Gem -> encoder.encodeString(value.value)
                    is GemSlots -> encoder.encodeSerializableValue(ListSerializer(String.serializer()), value.values)
                    is GemMapped -> encoder.encodeSerializableValue(MapSerializer(String.serializer(), String.serializer()), value.value)
                }
            }

            override fun deserialize(decoder: Decoder): Gemstone {
                val input = decoder as? JsonDecoder ?: error("Can only deserialize with JsonDecoder")
                return when (val jsonElement = input.decodeJsonElement()) {
                    is JsonArray -> GemSlots(jsonElement.map { it.jsonPrimitive.content })
                    is JsonPrimitive -> Gem(jsonElement.jsonPrimitive.content)
                    is JsonObject -> {
                        val map = jsonElement.entries.associate { (key, value) ->
                            key to value.jsonPrimitive.content
                        }
                        GemMapped(map)
                    }
                    else -> error("Unexpected format for Gemstone $jsonElement")
                }
            }
        }

        data class GemstoneData(
            val name: String,
            val tier: String,
        )
    }

    fun getMappedGemstones(map: Map<String, Gemstone>?): Pair<Map<String, Gemstone>, Map<String, GemstoneData>> {
        if (map == null) return Pair(emptyMap(), emptyMap())
        val gemstoneDataList = mutableMapOf<String, GemstoneData>()
        val entries = map.toMutableMap()
        val gemstoneKeys = entries.keys.toSet()

        for (key in gemstoneKeys) {
            val gem2 = map["${key}_gem"] as? Gem? ?: continue
            val gemTier = when (val gem = map[key]) {
                is Gem -> gem.value
                is Gemstone.GemMapped -> (gem.value["quality"] ?: "null")
                else -> "null"
            }
            gemstoneDataList[key] = GemstoneData(gem2.value, gemTier)
            entries.remove(key)
            entries.remove("${key}_gem")
        }
        return Pair(entries, gemstoneDataList)
    }

    @Serializable
    sealed class LoreEntry {
        @Serializable
        data class LoreEntryList(val values: List<String>) : LoreEntry()

        object LoreSerializer : KSerializer<LoreEntry> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LoreEntry")

            override fun serialize(encoder: Encoder, value: LoreEntry) {
                when (value) {
                    is LoreEntryList -> encoder.encodeSerializableValue(ListSerializer(String.serializer()), value.values)
                }
            }

            override fun deserialize(decoder: Decoder): LoreEntry {
                val input = decoder as? JsonDecoder ?: error("Can only deserialize with JsonDecoder")
                val jsonElement = input.decodeJsonElement()

                return when (jsonElement) {
                    is JsonArray -> LoreEntryList(jsonElement.map { it.jsonPrimitive.content })
                    is JsonPrimitive -> LoreEntryList(listOf(jsonElement.content))
                    else -> error("Unexpected format for LoreEntry")
                }
            }
        }
    }
}