package me.lyric.infinity.api.event.events.render.crosshair;

import me.bush.eventbus.event.Event;

/**
 * @author lyric
 * for crosshair module
 */

public class CrosshairEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }

}
