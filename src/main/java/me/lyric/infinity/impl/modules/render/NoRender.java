package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.init.MobEffects;


public class NoRender extends Module {
    public static NoRender INSTANCE;
    public Setting<Boolean> effects = register(new Setting<>("Effects", "Cancels potions effects.", false)); // TODO: Add more potion effects lol.
    public Setting<Boolean> weather = register(new Setting<>("Weather", "Cancels weather.", false));


     //public Setting<Boolean> Fire = register(new Setting<>("Fire", "Avoids fire overlay render.", false));
     public Setting<Boolean> NoArmor = register(new Setting<>("NoArmor", "Avoids armor overlay render.", false));
     public Setting<Boolean> sneak = register(new Setting<>("Force Sneak", "Forces all players to sneak clientside.", true));


    public NoRender() {
        super("NoRender", "Cancels rendering various things.", Category.RENDER);
        INSTANCE = this;
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
