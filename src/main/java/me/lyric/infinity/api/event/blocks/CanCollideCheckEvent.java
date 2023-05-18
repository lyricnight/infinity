package me.lyric.infinity.api.event.blocks;

import me.bush.eventbus.event.Event;

/**
 * @author lyric
 * uhh
 */

public class CanCollideCheckEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}

