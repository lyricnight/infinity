package me.lyric.infinity.api.event.events.render;

import me.bush.eventbus.event.Event;

/**
 * @author lyric ;)
 */

public class RenderNametagEvent extends Event {
    public RenderNametagEvent() {}
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
