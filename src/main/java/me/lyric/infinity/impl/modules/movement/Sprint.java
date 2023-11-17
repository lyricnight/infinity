package me.lyric.infinity.impl.modules.movement;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.entity.LivingUpdateEvent;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;

import java.util.Arrays;

/**
 * @author lyric
 * rewrite?
 */
@ModuleInformation(name = "Sprint", description = "we SPRINTING out here", category = Category.Movement)
public class Sprint extends Module {

    public ModeSetting mode = createSetting("Mode", "Directional", Arrays.asList("Directional", "Normal"));
    public BooleanSetting strict = createSetting("Strict", false);

    @Override
    public void onUpdate() {
        switch (mode.getValue()) {
            case "Directional":
                mc.player.setSprinting(handleSprint() && isMoving());
                break;
            case "Normal":
                mc.player.setSprinting(handleSprint() && isMoving() && !mc.player.collidedHorizontally && mc.gameSettings.keyBindForward.isKeyDown());
                break;
        }
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onMotion(MoveEvent event) {
        event.setCancelled(nullSafe() && handleSprint() && isMoving() && mode.getValue().equals("Directional"));
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onLivingUpdate(LivingUpdateEvent event) {
        event.setCancelled(nullSafe() && handleSprint() && isMoving() && mode.getValue().equals("Directional"));
    }

    public static boolean isMoving() {
        return (mc.player.moveForward != 0 || mc.player.moveStrafing != 0);
    }

    public boolean handleSprint() {
        return (!mc.player.isHandActive() && !mc.player.isSneaking()) || !strict.getValue();
    }

    @Override
    public String getDisplayInfo()
    {
        if(mc.player == null)
        {
            return "";
        }
        return mode.getValue().toString().toLowerCase();
    }
}