package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.misc.GameLoopEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.IntegerSetting;

/**
 * @author lyrrrric
 */

@ModuleInformation(name = "CustomTime", description = "Sets a world time.", category = Category.Render)
public class CustomTime extends Module {

    public IntegerSetting time = createSetting("Time",0,0, 24000);

    @Override
    public String getDisplayInfo()
    {
        if(mc.world == null || mc.player == null) return "";
        if (time.getValue() >= 12000) return "night";
        if (time.getValue() > 0)
        {
            return "day";
        }
        else
        {
            return "natural";
        }
    }

    @EventListener(priority = ListenerPriority.LOW)
    public void onGameLoop(GameLoopEvent event)
    {
        if (mc.world != null && time.getValue() != 0)
        {
            mc.world.setWorldTime(-time.getValue());
        }
    }
}
