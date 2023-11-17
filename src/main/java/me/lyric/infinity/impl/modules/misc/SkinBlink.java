package me.lyric.infinity.impl.modules.misc;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import net.minecraft.entity.player.EnumPlayerModelParts;

/**
 * @author lyric
 */

@ModuleInformation(name = "SkinBlink", description = "useless", category = Category.Misc)
public class SkinBlink extends Module {

    static EnumPlayerModelParts[] PARTS_HORIZONTAL;

    static {
        PARTS_HORIZONTAL = new EnumPlayerModelParts[]{EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.HAT, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG, EnumPlayerModelParts.RIGHT_SLEEVE};
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        int delay = mc.player.ticksExisted % (PARTS_HORIZONTAL.length * 2);
        boolean renderLayer = false;

        if (delay >= PARTS_HORIZONTAL.length) {
            renderLayer = true;
            delay -= PARTS_HORIZONTAL.length;
        }

        mc.gameSettings.setModelPartEnabled(PARTS_HORIZONTAL[delay], renderLayer);
    }
}
