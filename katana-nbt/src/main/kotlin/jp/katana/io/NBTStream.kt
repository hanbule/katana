package jp.katana.io

import jp.katana.nbt.Endian
import jp.katana.utils.BinaryStream

class NBTStream(private val endian: Endian, private val isNetwork: Boolean) {
    private val stream = BinaryStream()

    fun readByte(): Byte {
        return stream.readByte()
    }

    fun writeByte(value: Byte) {
        stream.writeByte(value)
    }

    fun readShort(): Short {
        if (endian == Endian.Little)
            return stream.readShortLE()

        return stream.readShort()
    }

    fun writeShort(value: Short) {
        if (endian == Endian.Little)
            stream.writeShortLE(value)
        else
            stream.writeShort(value)
    }

    fun readInt(): Int {
        if (isNetwork)
            return stream.readVarInt()

        if (endian == Endian.Little)
            return stream.readIntLE()

        return stream.readInt()
    }

    fun writeInt(value: Int) {
        if (isNetwork) {
            stream.writeVarInt(value)
            return
        }

        if (endian == Endian.Little)
            stream.writeIntLE(value)
        else
            stream.writeInt(value)
    }

    fun readLong(): Long {
        if (isNetwork)
            return stream.readVarLong()

        if (endian == Endian.Little)
            return stream.readLongLE()

        return stream.readLong()
    }

    fun writeLong(value: Long) {
        if (isNetwork) {
            stream.writeVarLong(value)
            return
        }

        if (endian == Endian.Little)
            stream.writeLongLE(value)
        else
            stream.writeLong(value)
    }

    fun readFloat(): Float {
        if (endian == Endian.Little)
            return stream.readFloatLE()

        return stream.readFloat()
    }

    fun writeFloat(value: Float) {
        if (endian == Endian.Little)
            stream.writeFloatLE(value)
        else
            stream.writeFloat(value)
    }

    fun readDouble(): Double {
        if (endian == Endian.Little)
            return stream.readDoubleLE()

        return stream.readDouble()
    }

    fun writeDouble(value: Double) {
        if (endian == Endian.Little)
            stream.writeDoubleLE(value)
        else
            stream.writeDouble(value)
    }

    fun readString(): String {
        if (isNetwork) {
            return stream.readVarString()
        }

        val len = readShort()
        return String(stream.read(len.toInt()))
    }

    fun writeString(value: String) {
        if (isNetwork) {
            stream.writeVarString(value)
            return
        }

        val buf = value.toByteArray()
        writeShort(buf.size.toShort())
        stream.write(buf)
    }

    fun getBuffer(): ByteArray {
        return stream.array()
    }

    fun setBuffer(buffer: ByteArray) {
        stream.setBuffer(buffer)
    }

    fun readRemaining(): ByteArray {
        return stream.readRemaining()
    }

    fun close() {
        stream.close()
    }
}
