package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.init.MobEffects;

/**
 * @author Paupro
 */

public class NoRender extends Module {

    public Setting<Boolean> effects = register(new Setting<>("Effects", "Cancels potions effects.", false)); // TODO: Add more potion effects lol.
    public Setting<Boolean> weather = register(new Setting<>("Weather", "Cancels weather.", false));

    /*
     public Setting<Boolean> Fire = register(new Setting<>("Fire", "Avoids fire overlay render.", false));
     public Setting<Boolean> NoArmor = register(new Setting<>("NoArmor", "Avoids armor overlay render.", false));
    */

    public NoRender() {
        super("NoRender", "Cancels rendering various things.", Category.RENDER);
    }

    // TODO: NoArmor & Fire

    @Override
    public void onUpdate() {
        if (effects.getValue()) {
            mc.player.removePotionEffect(MobEffects.NAUSEA);
            mc.player.removePotionEffect(MobEffects.BLINDNESS);
        }
        if (weather.getValue()) {
            mc.world.setRainStrength(0.0f);
        }
    }
}
