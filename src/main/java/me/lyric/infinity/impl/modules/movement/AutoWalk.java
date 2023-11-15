package me.lyric.infinity.impl.modules.movement;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author lyric
 * what a useful module
 */

@ModuleInformation(getName = "AutoWalk", getDescription = "so useful", category = Category.Movement)
public class AutoWalk extends Module {

    public BooleanSetting sprint = createSetting("Sprint", "Enables sprinting when you auto walk.", false));

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
