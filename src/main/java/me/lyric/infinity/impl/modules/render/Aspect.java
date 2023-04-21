package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.event.events.render.AspectEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import event.bus.EventListener;

/**
 * @author zzurio
 */

public class Aspect extends Module {

    public Setting<Float> aspect = register(new Setting("Aspect", "The aspect.", mc.displayWidth / mc.displayHeight + 0.0f, 0.1f, 3.0f));

    public Aspect() {
        super("Aspect", "Lets you modify the aspect ratio.", Category.RENDER);
    }

    @EventListener
    public void onAspect(AspectEvent event) {
        if (!nullSafe()) return;
        event.setAspect(aspect.getValue());
    }
}
