package me.lyric.infinity.impl.modules.movement;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author lyric
 * what a useful module
 */

public class AutoWalk extends Module {

    public Setting<Boolean> sprint = register(new Setting<>("Sprint", "Enables sprinting when you auto walk.", false));

    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward for you.", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdateInput(InputUpdateEvent event) {
        if (!nullSafe()) return;
        if (sprint.getValue()) {
            Infinity.INSTANCE.moduleManager.getModuleByClass(Sprint.class).setEnabled(true);
        }
        event.getMovementInput().moveForward = 1;
    }

    @Override
    public void onDisable() {
        if (sprint.getValue()) {
            Infinity.INSTANCE.moduleManager.getModuleByClass(Sprint.class).setEnabled(false);
        }
    }
}
