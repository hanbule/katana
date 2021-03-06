package jp.katana.core.world

import jp.katana.core.IServer
import jp.katana.core.actor.IActorPlayer
import jp.katana.core.world.chunk.IChunk
import jp.katana.core.world.chunk.IChunkLoader
import jp.katana.core.world.gamerule.IGameRules
import jp.katana.math.Vector2Int
import jp.katana.math.Vector3Int
import java.io.File

interface IWorld {
    val name: String
    val server: IServer
    val worldType: WorldType
    val gameRules: IGameRules

    fun loadData()
    fun loadData(file: File)

    fun save()

    fun getChunk(x: Int, z: Int, useShift: Boolean = true): IChunk
    fun getChunk(pos: Vector2Int, useShift: Boolean = true): IChunk
    fun getChunk(x: Int, y: Int, z: Int, useShift: Boolean = true): IChunk
    fun getChunk(pos: Vector3Int, useShift: Boolean = true): IChunk

    fun loadChunk(x: Int, z: Int, useShift: Boolean = true): IChunk
    fun loadChunk(pos: Vector2Int, useShift: Boolean = true): IChunk
    fun loadChunk(x: Int, y: Int, z: Int, useShift: Boolean = true): IChunk
    fun loadChunk(pos: Vector3Int, useShift: Boolean = true): IChunk

    fun unloadChunk(chunk: IChunk): Boolean
    fun unloadChunk(x: Int, z: Int, useShift: Boolean = true): Boolean
    fun unloadChunk(pos: Vector2Int, useShift: Boolean = true): Boolean
    fun unloadChunk(x: Int, y: Int, z: Int, useShift: Boolean = true): Boolean
    fun unloadChunk(pos: Vector3Int, useShift: Boolean = true): Boolean

    fun registerChunkLoader(loader: IChunkLoader)
    fun unregisterChunkLoader(loader: IChunkLoader)
    fun unregisterChunkLoader(id: Long)

    fun getChunkRadius(loader: IChunkLoader): Sequence<IChunk>
    fun sendChunks(player: IActorPlayer): Boolean
}