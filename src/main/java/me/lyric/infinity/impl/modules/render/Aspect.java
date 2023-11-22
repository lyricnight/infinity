package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.render.AspectEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.FloatSetting;

import java.text.DecimalFormat;

/**
 * @author lyric
 */

@ModuleInformation(name = "Aspect", description = "Changes the aspect ratio of your game.", category = Category.Render)
public class Aspect extends Module {

    public FloatSetting aspect = createSetting("Aspect", (float) mc.displayWidth / mc.displayHeight + 0.0f, 0.1f, 3.0f);

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
