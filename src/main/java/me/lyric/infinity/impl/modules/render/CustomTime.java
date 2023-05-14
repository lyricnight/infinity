package me.lyric.infinity.impl.modules.render;

import event.bus.EventListener;
import me.lyric.infinity.api.event.events.network.GameLoopEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

/**
 * @author lyrrrric
 */

public class CustomTime extends Module {
    public CustomTime()
    {
        super("CustomTime", "Allows you to set time clientside.", Category.RENDER);
    }
    public Setting<Integer> time = register(new Setting<>("Time", "The time to set to - set to 0 for time to progress normally.",0,0, 24000));


    @EventListener
    public void onGameLoop(GameLoopEvent event)
    {
        if (mc.world != null && time.getValue() != 0)
        {
            mc.world.setWorldTime(-time.getValue());
        }
    }
}
