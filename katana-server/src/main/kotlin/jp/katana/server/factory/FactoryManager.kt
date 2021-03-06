package jp.katana.server.factory

import jp.katana.factory.IFactory
import jp.katana.factory.IFactoryManager
import jp.katana.core.world.gamerule.IGameRules
import jp.katana.server.Server
import jp.katana.nbt.tag.INamedTag

class FactoryManager(server: Server) : IFactoryManager {
    private val factories: HashMap<Class<*>, IFactory<*, *>> = HashMap()

    init {
        register(CommandFactory(server))
        register(PacketFactory())
        register(INamedTag.factory)
        register(IGameRules.factory)
    }

    override fun <K, V> register(handler: IFactory<K, V>) {
        if (!factories.containsKey(handler.javaClass)) factories[handler.javaClass] = handler
    }

    @Suppress("UNCHECKED_CAST")
    override fun <F : IFactory<*, *>> get(clazz: Class<F>): F? {
        if (factories.containsKey(clazz)) {
            return factories[clazz] as F
        }

        return null
    }
}