package jp.katana.server.nbt.tag

import jp.katana.server.factory.NBTTagFactory
import jp.katana.server.factory.SimpleFactory
import jp.katana.server.nbt.io.NBTStream

interface INamedTag {
    companion object {
        const val END: Byte = 0
        const val BYTE: Byte = 1
        const val SHORT: Byte = 2
        const val INT: Byte = 3
        const val LONG: Byte = 4
        const val FLOAT: Byte = 5
        const val DOUBLE: Byte = 6
        const val BYTE_ARRAY: Byte = 7
        const val STRING: Byte = 8
        const val LIST: Byte = 9
        const val COMPOUND: Byte = 10
        const val INT_ARRAY: Byte = 11
        const val LONG_ARRAY: Byte = 12

        var factory: SimpleFactory<Byte, (String) -> INamedTag> = NBTTagFactory()

        fun getTag(type: Byte, name: String = ""): INamedTag {
            return factory[type]!!(name)
        }
    }

    var name: String
    val type: Byte

    fun write(stream: NBTStream)
    fun read(stream: NBTStream)
}