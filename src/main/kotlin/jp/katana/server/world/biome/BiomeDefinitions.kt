package jp.katana.server.world.biome

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import jp.katana.core.world.biome.IBiomeDefine
import jp.katana.core.world.biome.IBiomeDefinitions
import jp.katana.server.nbt.Endian
import jp.katana.server.nbt.io.NBTIO
import jp.katana.server.nbt.tag.CompoundTag
import jp.katana.server.nbt.tag.FloatTag
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class BiomeDefinitions : IBiomeDefinitions {
    private val defines: MutableList<IBiomeDefine> = mutableListOf()
    private var prevSize: Int = 0
    private var binaryData: ByteArray = ByteArray(0)

    init {
        val parser = JsonParser()
        val stream =
            this::class.java.classLoader.getResourceAsStream("biome_definition_list.json") ?: throw IOException()

        val element = parser.parse(JsonReader(InputStreamReader(stream, StandardCharsets.UTF_8)))
        if (element is JsonArray) {
            element.forEach { el ->
                run {
                    if (el is JsonObject) {
                        el.entrySet().forEach { entry ->
                            run {
                                val data = entry.value.asJsonObject
                                defines.add(
                                    BiomeDefine(
                                        entry.key,
                                        data.get("temperature").asFloat,
                                        data.get("downfall").asFloat
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun fromName(name: String): IBiomeDefine {
        return defines.first { define -> define.name == name }
    }

    override fun register(blockDefine: IBiomeDefine) {
        prevSize = defines.size
        defines.add(blockDefine)
    }

    override fun size(): Int {
        return defines.size
    }

    override fun binary(): ByteArray {
        return if (prevSize != defines.size) {
            val compound = CompoundTag("")
            for (biome in defines) {
                val define = CompoundTag(biome.name)
                define.put(FloatTag("temperature", biome.temperature))
                define.put(FloatTag("downfall", biome.downfall))
                compound.put(define)
            }
            binaryData = NBTIO.writeTag(compound, Endian.Little, true)
            binaryData
        } else {
            binaryData
        }
    }
}