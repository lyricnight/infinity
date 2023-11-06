package me.lyric.infinity.api.event.misc;

import me.bush.eventbus.event.Event;
import me.lyric.infinity.impl.modules.render.CustomTime;

/**
 * @author lyric
 * {@link CustomTime}
 */

public class GameLoopEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
