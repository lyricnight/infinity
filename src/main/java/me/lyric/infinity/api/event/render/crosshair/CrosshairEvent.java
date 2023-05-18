package me.lyric.infinity.api.event.render.crosshair;

import me.bush.eventbus.event.Event;

/**
 * @author lyric
 */

public class CrosshairEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }

}
