package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
/**
    @author lyric :P
 */
public class IOCancel extends Module {
    public IOCancel ()
    {
        super("IOCancel", "Prevents packet kicks, by cancelling IOException.", Category.PLAYER);
    }
}
