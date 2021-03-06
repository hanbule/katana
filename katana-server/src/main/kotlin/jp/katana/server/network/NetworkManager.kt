package jp.katana.server.network

import com.whirvis.jraknet.Packet
import com.whirvis.jraknet.identifier.MinecraftIdentifier
import com.whirvis.jraknet.peer.RakNetClientPeer
import com.whirvis.jraknet.server.RakNetServer
import jp.katana.core.actor.IActorPlayer
import jp.katana.core.actor.PlayerState
import jp.katana.core.network.INetworkManager
import jp.katana.core.network.Reliability
import jp.katana.core.network.packet.mcpe.IMinecraftPacket
import jp.katana.i18n.I18n
import jp.katana.server.Server
import jp.katana.server.io.CompressException
import jp.katana.server.network.packet.BatchPacket
import jp.katana.utils.BinaryStream
import java.net.InetSocketAddress
import java.util.zip.Deflater

class NetworkManager(private val server: Server) : INetworkManager {
    private val raknetServer: RakNetServer = RakNetServer(server.serverPort, server.maxPlayer)
    private val players: HashMap<InetSocketAddress, IActorPlayer> = HashMap()
    private val sessions: HashMap<InetSocketAddress, RakNetClientPeer> = HashMap()

    private val networkThread: Thread = Thread { startNetworkThread() }
    private var networkStart = false

    override fun start() {
        if (networkThread.state == Thread.State.NEW)
            networkThread.start()
    }

    override fun shutdown() {
        while (!networkStart)
            Thread.sleep(1)
        raknetServer.shutdown()
    }

    override fun addPlayer(address: InetSocketAddress, player: IActorPlayer): Boolean {
        if (!players.containsKey(address)) {
            players[address] = player
            return true
        }

        return false
    }

    override fun removePlayer(address: InetSocketAddress): Boolean {
        if (players.containsKey(address)) {
            players.remove(address)
            return true
        }

        return false
    }

    override fun getPlayer(address: InetSocketAddress): IActorPlayer? {
        if (players.containsKey(address)) return players[address]

        return null
    }

    override fun getPlayers(): List<IActorPlayer> {
        return players.values.toList()
    }

    override fun sendPacket(player: IActorPlayer, packet: IMinecraftPacket, reliability: Reliability) {
        val address = player.address
        try {
            if (sessions.containsKey(address)) {
                try {
                    packet.encode()
                } catch (e: Exception) {
                    throw InvalidPacketException(e.toString())
                }

                val binary = BinaryStream()
                val buf = packet.array()
                binary.writeUnsignedVarInt(buf.size)
                binary.write(buf)

                if (this.server.katanaConfig!!.showSendPacketId)
                    server.logger.info("Send 0x" + packet.packetId.toString(16) + " -> Size " + buf.size)
                if (this.server.katanaConfig!!.sendPacketDump)
                    server.logger.info("SendDump " + buf.joinToString("") {
                        String.format(
                            "%02X",
                            (it.toInt() and 0xFF)
                        )
                    })
                if (this.server.katanaConfig!!.printSendPacket)
                    server.logger.info("SendPrint \n$packet")

                // TODO: キャッシュ
                val batch = BatchPacket(null, Deflater(Deflater.DEFAULT_COMPRESSION, true))
                try {
                    batch.isEncrypt = player.isEncrypted
                    batch.decrypt = player.decrypt
                    batch.encrypt = player.encrypt
                    batch.decryptCounter = player.decryptCounter
                    batch.encryptCounter = player.encryptCounter
                    batch.sharedKey = player.sharedKey
                    batch.payload = binary.array()
                    batch.encode()

                    if (player.isEncrypted)
                        player.encryptCounter++

                    sessions[address]!!.sendMessage(
                        com.whirvis.jraknet.protocol.Reliability.lookup(reliability.id.toInt()),
                        packet.channel,
                        Packet(batch.array())
                    )
                } catch (e: CompressException) {
                    server.logger.warn("", e)
                } catch (e: EncryptException) {
                    server.logger.warn("", e)
                } finally {
                    binary.close()
                    batch.close()
                }
            }
        } catch (e: InvalidPacketException) {
            server.logger.warn("", e)
        } finally {
            packet.close()
        }
    }

    override fun sendPacket(players: List<IActorPlayer>, packet: IMinecraftPacket, reliability: Reliability) {
        for (player in players) {
            sendPacket(player, packet, reliability)
        }
    }

    override fun sendBroadcastPacket(packet: IMinecraftPacket, reliability: Reliability) {
        for (player in players.filter { entry -> entry.value.state == PlayerState.Joined }) {
            sendPacket(player.value, packet, reliability)
        }
    }

    override fun handlePacket(address: InetSocketAddress, packet: IMinecraftPacket) {
        if (players.containsKey(address)) {
            players[address]!!.handlePacket(packet)
        }
    }

    fun addSession(address: InetSocketAddress, session: RakNetClientPeer): Boolean {
        if (!sessions.containsKey(address)) {
            sessions[address] = session
            return true
        }

        return false
    }

    fun removeSession(address: InetSocketAddress): Boolean {
        if (sessions.containsKey(address)) {
            sessions.remove(address)
            return true
        }

        return false
    }

    fun updateIdentifier(motd: String, onlinePlayerCount: Int, maxPlayer: Int, subMotd: String) {
        val identifier = raknetServer.identifier
        if (identifier is MinecraftIdentifier) {
            identifier.serverName = motd
            identifier.onlinePlayerCount = onlinePlayerCount
            identifier.maxPlayerCount = maxPlayer
            identifier.worldName = subMotd
        }
    }

    fun updateOnlinePlayerCount() {
        updateOnlinePlayerCount(raknetServer.clientCount)
    }

    fun updateOnlinePlayerCount(count: Int) {
        val identifier = raknetServer.identifier
        if (identifier is MinecraftIdentifier) {
            identifier.onlinePlayerCount = count
        }
    }

    internal fun updateState(f: Boolean) {
        networkStart = f
    }

    private fun startNetworkThread() {
        Thread.currentThread().name = I18n["katana.server.thread.networkThread"]

        raknetServer.identifier = MinecraftIdentifier(
            server.motd,
            server.protocolVersion,
            server.gameVersion,
            0,
            server.maxPlayer,
            raknetServer.globallyUniqueId,
            server.subMotd,
            ""
        )
        raknetServer.addSelfListener()
        raknetServer.addListener(ServerListener(server, this))
        raknetServer.start()
    }
}
