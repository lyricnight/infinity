package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.transformer.IItemRenderer;

public class Swing extends Module {

    public static Swing INSTANCE;


    public Setting<SwingHand> swing =
            createSetting("Swing","Changes hand of swing.", SwingHand.MAINHAND));
    public BooleanSetting slowSwing =
            createSetting("SlowSwing","Makes the swing animation slower.", false));
    public BooleanSetting instantSwap =
            createSetting("InstantSwap","AKA 1.8 Animations.", false));

    public Swing() {
        super("Swing", "Changes swing.", Category.RENDER);
        INSTANCE = this;
    }
    public enum SwingHand {
        MAINHAND,
        OFFHAND,
        NONE
    }

    @Override
    public void onUpdate(){
        if (mc.player == null)
        {
            return;
        }

        if (instantSwap.getValue()) {

            if (((IItemRenderer)mc.entityRenderer.itemRenderer).getprevEquippedProgressMainHand() >= 0.9) {
                ((IItemRenderer)mc.entityRenderer.itemRenderer).setequippedProgressMainHand(1.0f);
                ((IItemRenderer)mc.entityRenderer.itemRenderer).setitemStackMainHand(mc.player.getHeldItemMainhand());
            }
        }
    }
    @Override
    public String getDisplayInfo()
    {
        if (mc.player == null)
        {
            return "";
        }
        if (swing.getValue() == SwingHand.MAINHAND)
        {
            return "mainhand";
        }
        if (swing.getValue() == SwingHand.OFFHAND)
        {
            return "offhand";
        }
        if (swing.getValue() == SwingHand.NONE)
        {
            return "none";
        }
        return "";
    }
}
