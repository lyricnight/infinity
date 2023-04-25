package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.util.EnumHand;

public class Swing extends Module {

    public static Swing INSTANCE;


    public Setting<SwingHand> swing =
            register(new Setting<>("Swing","Changes hand of swing.", SwingHand.MAINHAND));
    public Setting<Boolean> slowSwing =
            register(new Setting<>("SlowSwing","Makes the swing animation slower.", false));
    public Setting<Boolean> instantSwap =
            register(new Setting<>("InstantSwap","Changes some stuff.", false));

    public Swing() {
        super("Swing", "Changes swing.", Category.RENDER);
        INSTANCE = this;
    }
    public enum SwingHand {
        MAINHAND,
        OFFHAND
    }

    @Override
    public void onUpdate(){
        if (instantSwap.getValue()) {

            if (mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
        }
        if (swing.getValue() == SwingHand.OFFHAND) {
            mc.player.swingingHand = EnumHand.OFF_HAND;

        } else if (swing.getValue() == SwingHand.MAINHAND) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        }
    }
}
