package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;

@ModuleInformation(name = "InstantSpeed", description = "we GOING TOO FAST out here", category = Category.Movement)
public class InstantSpeed extends Module {

    public BooleanSetting air = createSetting("Air", false);

    public BooleanSetting keycheck = createSetting("Key", false);

    public boolean pause = false;
    private boolean pauseLocal = false;

    @Override
    public String getDisplayInfo()
    {
        if(!nullSafe()) return "";
        if(pause || pauseLocal)
        {
            return ChatFormatting.RED + "false";
        }
        return ChatFormatting.GREEN + "true";
    }

    @Override
    public void onUpdate()
    {
        pauseLocal = EntityUtil.isInLiquid() || mc.player.noClip || mc.player.isElytraFlying() || mc.player.isSpectator() || mc.player.isSneaking();
    }

    @EventListener
    public void onMove(MoveEvent e) {
        if (pause || pauseLocal) {
            return;
        }
        if ((keycheck.getValue() && !SpeedUtil.anyMovementKeys()) || (air.getValue() && !mc.player.onGround))
        {
            return;
        }
        SpeedUtil.instant(e, SpeedUtil.getSpeed());
    }



}
