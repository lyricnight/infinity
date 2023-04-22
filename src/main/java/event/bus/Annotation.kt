package event.bus

/**
 * @author
 */

annotation class EventListener(val priority: Int = 0, val state: EventState = EventState.NONE)