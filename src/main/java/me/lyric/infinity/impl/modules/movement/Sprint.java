package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.events.entity.LivingUpdateEvent;
import me.lyric.infinity.api.event.events.player.MotionEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

/**
 * @author lyric
 */

public class Sprint extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "The mode for sprint.", Mode.DIRECTIONAL));
    public Setting<Boolean> strict = register(new Setting<>("Strict", "Changes sprint to function better in stricter anti-cheats.", false));

    public Sprint() {
        super("Sprint", "Keeps you always sprinting.", Category.MOVEMENT);
    }

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
    public void onMotion(MotionEvent event) {
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
        return ChatFormatting.GRAY +"["+ ChatFormatting.RESET + ChatFormatting.WHITE + mode.getValue().toString().toLowerCase() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }
}