package me.lyric.infinity.impl.modules.movement;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.manager.Managers;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author lyric
 * what a useful module
 */

@ModuleInformation(name = "AutoWalk", description = "so useful", category = Category.Movement)
public class AutoWalk extends Module {

    public BooleanSetting sprint = createSetting("Sprint", false);

    @SubscribeEvent
    public void onUpdateInput(InputUpdateEvent event) {
        if (!nullSafe()) return;
        if (sprint.getValue()) {
            Managers.MODULES.getModuleByClass(Sprint.class).enable();
        }
        event.getMovementInput().moveForward = 1;
    }

    @Override
    public void onDisable() {
        if (sprint.getValue()) {
            Managers.MODULES.getModuleByClass(Sprint.class).disable();
        }
    }
}
