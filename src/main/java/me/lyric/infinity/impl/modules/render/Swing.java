package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.mixin.transformer.IItemRenderer;

import java.util.Arrays;

@ModuleInformation(name = "Swing", description = "Modifies Swing.", category = Category.Render)
public class Swing extends Module {
    public ModeSetting swing = createSetting("Swing","Mainhand", Arrays.asList("Mainhand", "Offhand", "None"));
    public BooleanSetting slowSwing = createSetting("SlowSwing", false);
    public BooleanSetting instantSwap = createSetting("InstantSwap", false);

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
        if (swing.getValue() == "Mainhand")
        {
            return "mainhand";
        }
        if (swing.getValue() == "Offhand")
        {
            return "offhand";
        }
        if (swing.getValue() == "None")
        {
            return "none";
        }
        return "";
    }
}
