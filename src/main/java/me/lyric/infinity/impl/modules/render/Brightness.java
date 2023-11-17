package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Arrays;

/**
 * @author lyric
 * fullbright thingy
 */

@ModuleInformation(name = "Brightness", description = "fullbright", category = Category.Render)
public class Brightness extends Module {

    public ModeSetting mode = createSetting("Mode", "Gamma", Arrays.asList("Gamma", "Potion", "Table"));

    private float lastGamma;

    @Override
    public void onEnable() {
        if (!nullSafe()) return;
        if (mode.getValue() == "Gamma") {
            lastGamma = mc.gameSettings.gammaSetting;
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == "Gamma") {
            mc.gameSettings.gammaSetting = lastGamma;
        }
        if (mode.getValue() == "Potion" && mc.player != null) {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        if (mode.getValue() == "Table") {
            if (mc.world != null) {
                for (int i = 0; i <= 15; i++) {
                    float f1 = 1.0f - i / 15.0f;
                    mc.world.provider.getLightBrightnessTable()[i] = (1.0f - f1) / (f1 * 3.0f + 1.0f) + 0.0f;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        switch (mode.getValue()) {

            case "Gamma":
                mc.gameSettings.gammaSetting = 1000;
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
                break;

            case "Potion":
                mc.gameSettings.gammaSetting = 1.0f;
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 9999));
                break;

            case "Table":
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
                mc.gameSettings.gammaSetting = lastGamma;
                if (mc.world != null) {
                    for (int i = 0; i <= 15; i++) {
                        mc.world.provider.getLightBrightnessTable()[i] = 1f;
                    }
                }
                break;

        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toLowerCase();
    }
}