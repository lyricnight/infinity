package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.blocks.CanCollideCheckEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;

/**
 * @author lyric
 */

@ModuleInformation(name = "LiquidInteract", description = "????", category = Category.Misc)
public class LiquidInteract extends Module {
    @EventListener
    public void canCollide(CanCollideCheckEvent event) {
        if (!nullSafe()) return;
        event.cancel();
    }
}
