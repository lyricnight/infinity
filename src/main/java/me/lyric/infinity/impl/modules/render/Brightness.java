package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * @author lyric
 */

public class Brightness extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "The mode for brightness.", Mode.GAMMA));

    private float lastGamma;

    public Brightness() {
        super("Brightness", "Renders your environment brighter in various ways.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (!nullSafe()) return;
        if (mode.getValue() == Mode.GAMMA) {
            lastGamma = mc.gameSettings.gammaSetting;
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == Mode.GAMMA) {
            mc.gameSettings.gammaSetting = lastGamma;
        }
        if (mode.getValue() == Mode.POTION && mc.player != null) {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        if (mode.getValue() == Mode.TABLE) {
            if (mc.world != null) {
                for (int i = 0; i <= 15; i++) {
                    float f1 = 1.0f - i / 15.0f;
                    mc.world.provider.getLightBrightnessTable()[i] = (1.0f - f1) / (f1 * 3.0f + 1.0f) * 1.0f + 0.0f;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        switch (mode.getValue()) {

            case GAMMA:
                mc.gameSettings.gammaSetting = 1000;
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
                break;

            case POTION:
                mc.gameSettings.gammaSetting = 1.0f;
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 9999));
                break;

            case TABLE:
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

    public enum Mode {
        GAMMA,
        POTION,
        TABLE
    }

    @Override
    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.WHITE + mode.getValue().toString().toLowerCase() +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }
}