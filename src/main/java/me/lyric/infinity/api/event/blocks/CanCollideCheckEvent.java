package me.lyric.infinity.api.event.blocks;

import me.bush.eventbus.event.Event;
import me.lyric.infinity.impl.modules.misc.LiquidInteract;

/**
 * @author lyric
 * used for {@link LiquidInteract}
 */

public class CanCollideCheckEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}

