package me.lyric.infinity.impl.modules.misc;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.init.MobEffects;

/**
 * @author Paupro
 */

public class AntiLevitation extends Module {

    public AntiLevitation() {
        super("AntiLevitation", "Removes the levitation effect.", Category.MISC);
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        mc.player.removePotionEffect(MobEffects.LEVITATION);
    }
}
