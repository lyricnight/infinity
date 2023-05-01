package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;


public class NoInterpolation extends Module {

    public NoInterpolation() {
        super("Resolver", "Renders server-side player positions.", Category.PLAYER);
    }
    public Setting<Boolean> forceSneak = register(new Setting<>("Force Sneak", "Forces all players to sneak clientside.", false));
    public static NoInterpolation INSTANCE = new NoInterpolation();
    public static NoInterpolation getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoInterpolation();
        }
        return INSTANCE;
    }
}
