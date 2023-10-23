package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.impl.modules.combat.Burrow;

public class InstantSpeed extends Module {

    public InstantSpeed(){
        super("InstantSpeed", "Makes you accelerate instantly", Category.MOVEMENT);
    }
    public boolean pause = false;
    public Setting<Boolean> noLiquid = register(new Setting<>("NoLiquid","Disables module in liquid",  true));
    @Override
    public String getDisplayInfo()
    {
        if(!nullSafe()) return "";
        if(pause)
        {
            return ChatFormatting.RED + "false";
        }
        return ChatFormatting.GREEN + "true";
    }

    @EventListener
    public void onMove(MoveEvent e) {
        if ((EntityUtil.isInLiquid() && noLiquid.getValue()) || mc.player.isElytraFlying())
        {
            pause = true;
        }
        if (pause) {
            return;
        }
        SpeedUtil.instant(e, SpeedUtil.getSpeed());
    }



}
