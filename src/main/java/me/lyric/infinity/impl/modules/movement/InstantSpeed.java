package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;

public class InstantSpeed extends Module {

    public InstantSpeed(){
        super("InstantSpeed", "Makes you accelerate instantly - don't use on strict with speed arrows!", Category.MOVEMENT);
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
        if (!EntityUtil.isInLiquid())
        {
            pause = false;
        }
        if (mc.player.isElytraFlying() || pause) {
            return;
        }
        if (this.noLiquid.getValue() && EntityUtil.isInLiquid()) {
            pause = true;
            return;
        }
        SpeedUtil.instant(e, SpeedUtil.getSpeed());
    }



}
