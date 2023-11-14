package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;

/**
 * @author lyric
 * @link {MixinEntityOtherPlayerMP}
 */

@ModuleInformation(getName = "Resolver", getDescription = "we REMOVING INTERPOLATION out here", category = Category.Player)
public class Resolver extends Module {
    @Override
    public String getDisplayInfo()
    {
        if (mc.world == null || mc.player == null) return "";
        return String.valueOf(mc.world.playerEntities.size());
    }
}
