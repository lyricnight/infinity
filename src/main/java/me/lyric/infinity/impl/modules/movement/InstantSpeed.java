package me.lyric.infinity.impl.modules.movement;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
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
        if(noLiquid.getValue())
        {
            return "noliquid";
        }
        return "normal";

    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onMove(MoveEvent e) {
        if (mc.player.isElytraFlying()) {
            return;
        }
        if (pause)
        {
            return;
        }
        if (this.noLiquid.getValue() && EntityUtil.isInLiquid()) {
            return;
        }
        SpeedUtil.instant(e, SpeedUtil.getSpeed());
    }



}
