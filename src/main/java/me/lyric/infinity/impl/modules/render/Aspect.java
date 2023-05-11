package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.api.event.events.render.AspectEvent;
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

    @EventListener
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
        return ChatFormatting.GRAY +"["+ ChatFormatting.RESET + ChatFormatting.WHITE + format.format(aspect.getValue()) + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }
}
