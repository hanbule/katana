package jp.katana.core.network

import jp.katana.core.entity.IPlayer
import jp.katana.server.network.packet.mcpe.MinecraftPacket
import java.net.InetSocketAddress

/**
 * ネットワークの管理を実装します。
 */
interface INetworkManager {
    /**
     * ネットワークを開始します。
     */
    fun start()

    /**
     * ネットワークを終了します。
     */
    fun shutdown()

    /**
     * パケットを送信します。
     * @param player プレイヤー実行
     * @param packet Minecraftのパケット
     * @param reliability パケットの信頼性
     */
    fun sendPacket(player: IPlayer, packet: MinecraftPacket, reliability: Reliability = Reliability.RELIABLE_ORDERED)

    /**
     * パケットを受信した時に呼び出されます。
     * @param address プレイヤーのIPアドレスとポート
     * @param packet Minecraftのパケット
     */
    fun handlePacket(address: InetSocketAddress, packet: MinecraftPacket)
}