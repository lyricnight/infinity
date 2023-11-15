package me.lyric.infinity.impl.modules.movement;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.entity.LivingUpdateEvent;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;

/**
 * @author lyric
 * rewrite?
 */
@ModuleInformation(getName = "Sprint", getDescription = "we SPRINTING out here", category = Category.Movement)
public class Sprint extends Module {

    public Setting<Mode> mode = createSetting("Mode", "The mode for sprint.", Mode.DIRECTIONAL));
    public BooleanSetting strict = createSetting("Strict", "Changes sprint to function better in stricter anti-cheats.", false));

    @Override
    public void onUpdate() {
        switch (mode.getValue()) {
            case DIRECTIONAL:
                mc.player.setSprinting(handleSprint() && isMoving());
                break;
            case NORMAL:
                mc.player.setSprinting(handleSprint() && isMoving() && !mc.player.collidedHorizontally && mc.gameSettings.keyBindForward.isKeyDown());
                break;
        }
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onMotion(MoveEvent event) {
        event.setCancelled(nullSafe() && handleSprint() && isMoving() && mode.getValue().equals(Mode.DIRECTIONAL));
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onLivingUpdate(LivingUpdateEvent event) {
        event.setCancelled(nullSafe() && handleSprint() && isMoving() && mode.getValue().equals(Mode.DIRECTIONAL));
    }

    public static boolean isMoving() {
        return (mc.player.moveForward != 0 || mc.player.moveStrafing != 0);
    }

    public boolean handleSprint() {
        return (!mc.player.isHandActive() && !mc.player.isSneaking()) || !strict.getValue();
    }

    public enum Mode {
        DIRECTIONAL,
        NORMAL
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