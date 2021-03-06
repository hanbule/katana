package jp.katana.server.factory

import jp.katana.factory.SimpleFactory
import jp.katana.server.network.packet.mcpe.*

class PacketFactory : SimpleFactory<Int, () -> MinecraftPacket>() {
    init {
        this += { LoginPacket() } // 0x01
        this += { PlayStatusPacket() } // 0x02
        this += { ServerToClientHandshakePacket() } // 0x03
        this += { ClientToServerHandshakePacket() } // 0x04
        this += { DisconnectPacket() } // 0x05
        this += { ResourcePacksInfoPacket() } // 0x06
        this += { ResourcePackStackPacket() } // 0x07
        this += { ResourcePackClientResponsePacket() } // 0x08
        this += { TextPacket() } // 0x09
        this += { SetTimePacket() } // 0x0a
        this += { StartGamePacket() } // 0x0b
        this += { AddPlayerPacket() } // 0x0c
        this += { AddActorPacket() } // 0x0d
        this += { RemoveActorPacket() } // 0x0e
        this += { AddItemActorPacket() } // 0x0f

        this += { TakeItemActorPacket() } // 0x11
        this += { MoveActorAbsolutePacket() } // 0x12
        this += { MovePlayerPacket() } // 0x13

        this += { LevelChunkPacket() } // 0x3a

        this += { RequestChunkRadiusPacket() } // 0x45
        this += { ChunkRadiusUpdatedPacket() } // 0x46

        this += { ResourcePackDataInfoPacket() } // 0x52
        this += { ResourcePackChunkDataPacket() } // 0x53
        this += { ResourcePackChunkRequestPacket() } // 0x54

        this += { SetLocalPlayerAsInitializedPacket() } // 0x71

        this += { AvailableActorIdentifiersPacket() }// 0x77

        this += { BiomeDefinitionListPacket() }// 0x7a

        this += { PacketViolationWarningPacket() }// 0x9c
    }

    override fun plusAssign(value: () -> MinecraftPacket) {
        val v = value()
        if (!map.containsKey(v.packetId))
            map[v.packetId] = value
    }

    override operator fun get(name: Int): (() -> MinecraftPacket)? {
        if (map.containsKey(name))
            return map[name]

        return null
    }
}