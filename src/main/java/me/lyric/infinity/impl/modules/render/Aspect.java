package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.render.AspectEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

import java.text.DecimalFormat;

/**
 * @author lyric
 */

public class Aspect extends Module {

    public Setting<Float> aspect = register(new Setting("Aspect", "The aspect.", mc.displayWidth / mc.displayHeight + 0.0f, 0.1f, 3.0f));

    public Aspect() {
        super("Aspect", "Lets you modify the aspect ratio.", Category.RENDER);
    }
    public DecimalFormat format = new DecimalFormat("#.0");

    @EventListener(priority = ListenerPriority.LOW)
    public void onAspect(AspectEvent event) {
        if (!nullSafe()) return;
        event.setAspect(aspect.getValue());
    }
    @Override
    public String getDisplayInfo()
    {
        if(mc.player == null)
        {
            return "";
        }
        return format.format(aspect.getValue());
    }
}
