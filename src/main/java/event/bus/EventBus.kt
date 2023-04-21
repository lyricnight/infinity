package event.bus

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

enum class EventState { PRE, POST, NONE }

abstract class Event(val eventState: EventState = EventState.NONE) {
    var cancelled = false

    fun cancel() {
        cancelled = true
    }
}

object EventBus {
    data class Invocation(val method: Method, val obj: Any, val priority: Int = 0, val eventState: EventState) {
        fun <T : Any> invoke(event: T) {
            method.invoke(obj, event)
        }
    }

    private val invocations = ConcurrentHashMap<Class<*>, MutableList<Invocation>>()

    /*
    @JvmStatic
    fun post(event: Event) {
        val clazz = event.javaClass

        invocations[clazz]?.forEach {
            if (!event.cancelled) {
                if (it.eventState == EventState.NONE || (it.eventState == event.eventState)) {
                    it.invoke(event)
                }
            }
        }
    }
     */

    @JvmStatic
    fun <T : Any> post(event: T) {
        if (event is Event || event is net.minecraftforge.fml.common.eventhandler.Event) {
            val clazz = event.javaClass

            invocations[clazz]?.forEach {
                if (event is Event) {
                    if (!event.cancelled) {
                        if (it.eventState == EventState.NONE || (it.eventState == event.eventState)) {
                            it.invoke(event)
                        }
                    }
                } else {
                    it.invoke(event)
                }
            }
        }
    }

    fun register(obj: Any) {
        val clazz = obj.javaClass

        /* Find all methods with the @SubscribeEvent annotation and a parameter count of 1 */
        val listenerMethods =
            clazz.declaredMethods.filter { it.isAnnotationPresent(EventListener::class.java) && it.parameterCount == 1 }

        listenerMethods.forEach { method ->
            /* Only add to invocations if parameter subclass is the Event class */
            val type = method.parameterTypes[0]
            if (anySuperclassIsEvent(type)) {
                val annotation = method.getAnnotation(EventListener::class.java)
                invocations.getOrPut(type, { mutableListOf() })
                    .add(Invocation(method, obj, annotation.priority, annotation.state))
                invocations[type]?.sortByDescending { it.priority }
            }
        }
    }

    fun unregister(obj: Any) {
        invocations.keys.forEach { key ->
            invocations[key] = invocations[key]?.filter { it.obj != obj }?.toMutableList()!!
        }
    }

    private fun anySuperclassIsEvent(clazz: Class<*>): Boolean {
        if (clazz.superclass == Object::class.java) return false

        if (clazz.superclass == Event::class.java) return true
        if (clazz.superclass == net.minecraftforge.fml.common.eventhandler.Event::class.java) return true

        return anySuperclassIsEvent(clazz.superclass)
    }
}