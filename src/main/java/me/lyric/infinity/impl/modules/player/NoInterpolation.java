package me.lyric.infinity.impl.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;

/**
 * @author lyric
 * @link {MixinEntityOtherPlayerMP}
 */

public class NoInterpolation extends Module {

    public NoInterpolation() {
        super("Resolver", "Renders server-side player positions.", Category.PLAYER);
    }

    @Override
    public String getDisplayInfo()
    {
        if (mc.world == null || mc.player == null) return "";
        return String.valueOf(mc.world.playerEntities.size());
    }
}
