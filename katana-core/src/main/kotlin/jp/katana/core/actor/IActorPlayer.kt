package jp.katana.core.actor

import jp.katana.core.data.IClientData
import jp.katana.core.data.ILoginData
import jp.katana.core.network.IPacketHandler
import jp.katana.core.network.Reliability
import jp.katana.core.network.packet.mcpe.IMinecraftPacket
import jp.katana.core.world.chunk.IChunkLoader
import java.net.InetSocketAddress
import java.security.KeyPair
import javax.crypto.Cipher

interface IActorPlayer : IActorLiving, IChunkLoader {
    val address: InetSocketAddress
    val packetHandler: IPacketHandler

    val loginData: ILoginData?
    val clientData: IClientData?
    val keyPair: KeyPair?

    val sharedKey: ByteArray?
    val decrypt: Cipher?
    val encrypt: Cipher?

    val isEncrypted: Boolean
    var encryptCounter: Long
    var decryptCounter: Long

    val displayName: String

    val state: PlayerState

    val chunkRadius: Int

    fun handlePacket(packet: IMinecraftPacket)
    fun sendPacket(packet: IMinecraftPacket, reliability: Reliability = Reliability.RELIABLE_ORDERED)

    fun disconnect(reason: String)

    fun onDisconnect(reason: String?)
}