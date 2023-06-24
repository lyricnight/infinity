package me.lyric.infinity.api.event.blocks;

import me.bush.eventbus.event.Event;
import me.lyric.infinity.impl.modules.combat.Burrow;

/**
 * @author lyric
 * used by {@link Burrow}
 */
public class BlockPushOutEvent extends Event {
    @Override
    protected boolean isCancellable()
    {
        return true;
    }
}
