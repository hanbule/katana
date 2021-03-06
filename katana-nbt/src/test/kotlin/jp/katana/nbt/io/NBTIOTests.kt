package jp.katana.nbt.io

import jp.katana.io.NBTIO
import jp.katana.nbt.Endian
import jp.katana.nbt.tag.CompoundTag
import jp.katana.nbt.tag.INamedTag
import jp.katana.nbt.tag.ListTag
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException

class NBTIOTests {
    @Test
    fun readExTests() {
        val stream = this::class.java.classLoader.getResourceAsStream("raw.nbt") ?: throw IOException()
        val com = NBTIO.read(stream.readBytes(), Endian.Little)
    }

    @Test
    fun readWriteTagTests() {
        val com = CompoundTag("")
        val list = ListTag("test", INamedTag.INT)
        list.addInt(1234)
        com.putTag(list)

        val buf = NBTIO.writeTag(com, Endian.Big, false)
        val ncom = NBTIO.readTag(buf, Endian.Big, false) as CompoundTag
        Assertions.assertTrue(ncom.getListTag("test").getInt(0) == 1234)
        Assertions.assertTrue(ncom.getListTag("test").getIntTag(0).name == "")
    }

    @Test
    fun readWriteZlibTagTests() {
        val com = CompoundTag("")
        val list = ListTag("test", INamedTag.INT)
        list.addInt(1234)
        com.putTag(list)

        val buf = NBTIO.writeZlibTag(com, Endian.Big, false)
        val ncom = NBTIO.readZlibTag(buf, Endian.Big, false) as CompoundTag
        Assertions.assertTrue(ncom.getListTag("test").getInt(0) == 1234)
        Assertions.assertTrue(ncom.getListTag("test").getIntTag(0).name == "")
    }

    @Test
    fun toStringTests(){
        val com = CompoundTag("")
        val list = ListTag("test", INamedTag.INT)
        list.addInt(1234)
        com.putTag(list)

        println(com.toString())
    }
}