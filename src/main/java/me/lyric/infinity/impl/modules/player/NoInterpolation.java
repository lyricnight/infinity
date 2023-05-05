package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;

public class NoInterpolation extends Module {

    public NoInterpolation() {
        super("Resolver", "Renders server-side player positions.", Category.PLAYER);
    }
    public static NoInterpolation INSTANCE = new NoInterpolation();
    public static NoInterpolation getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoInterpolation();
        }
        return INSTANCE;
    }
}
