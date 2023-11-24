package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import net.minecraft.init.MobEffects;

@ModuleInformation(name = "NoRender", description = "Prevents certain things from rendering.", category = Category.Render)
public class NoRender extends Module {
    public BooleanSetting effects = createSetting("Effects", false);
    public BooleanSetting weather = createSetting("Weather", false);

     //public BooleanSetting Fire = createSetting("Fire", "Avoids fire overlay render.", false));
     public BooleanSetting NoArmor = createSetting("NoArmor", false);
     public BooleanSetting sneak = createSetting("Force Sneak", true);

    @Override
    public void onUpdate() {
        if (effects.getValue()) {
            mc.player.removePotionEffect(MobEffects.NAUSEA);
            mc.player.removePotionEffect(MobEffects.BLINDNESS);
            mc.player.removePotionEffect(MobEffects.UNLUCK);
        }
        if (weather.getValue()) {
            mc.world.setRainStrength(0.0f);
        }
    }
}
