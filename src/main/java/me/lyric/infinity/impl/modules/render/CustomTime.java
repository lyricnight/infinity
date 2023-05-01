package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

public class CustomTime extends Module {
    public CustomTime()
    {
        super("Custom Time", "Allows you to set time clientside.", Category.RENDER);
    }
    public Setting<Integer> time = register(new Setting<>("Time", "The time to set to - set to 0 for time to progress normally.",0,0, 24000));


    @Override
    public void onUpdate()
    {
        if (mc.world != null && time.getValue() != 0)
        {
            mc.world.setWorldTime(-time.getValue());
        }
    }
}
