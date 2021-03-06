package jp.katana.core.world.gamerule

import jp.katana.utils.BinaryStream

class BooleanGameRule(name: String, override var value: Boolean) : IGameRule<Boolean> {
    override val type: Byte = IGameRule.BOOLEAN
    override var name: String = name
        private set

    override fun write(stream: BinaryStream) {
        stream.writeVarString(name.toLowerCase())
        stream.writeByte(type)
        stream.writeBoolean(value)
    }

    override fun read(stream: BinaryStream) {
        value = stream.readBoolean()
    }
}