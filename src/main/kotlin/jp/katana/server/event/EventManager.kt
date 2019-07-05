package jp.katana.server.event

import jp.katana.core.event.EventHandler
import jp.katana.core.event.IEvent
import jp.katana.core.event.IEventManager
import jp.katana.server.event.player.PlayerCreateEvent
import jp.katana.server.event.server.ServerStartEvent
import jp.katana.server.event.server.ServerStopEvent
import jp.katana.server.event.server.ServerUpdateTickEvent

class EventManager : IEventManager {
    private val events: HashMap<Class<*>, EventHandler<*>> = HashMap()

    init {
        register(EventHandler<PlayerCreateEvent>())

        register(EventHandler<ServerStartEvent>())
        register(EventHandler<ServerStopEvent>())
        register(EventHandler<ServerUpdateTickEvent>())
    }

    override fun <T : IEvent> register(handler: EventHandler<T>) {
        if (!events.containsKey(handler.javaClass)) {
            events[handler.javaClass] = handler
        }
    }

    override fun <V : IEvent> invoke(value: V) {
        val handler = EventHandler.generateClass<EventHandler<V>>()
        if (events.containsKey(handler)) {

            events[handler]!!(value)
        }
    }
}