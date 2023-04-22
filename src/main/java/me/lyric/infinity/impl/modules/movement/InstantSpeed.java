package me.lyric.infinity.impl.modules.movement;

import event.bus.EventListener;
import me.lyric.infinity.api.event.events.player.MotionEvent;
import me.lyric.infinity.api.event.events.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.EntityUtil;
import me.lyric.infinity.api.util.minecraft.MovementUtil;

public class InstantSpeed extends Module {

    public InstantSpeed(){
        super("InstantSpeed", "Makes you accelerate instantly - don't use on strict with speed arrows!", Category.MOVEMENT);
    }
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid","Disables module in liquid",  true));


    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
    }

    @EventListener
    public void onMove(MoveEvent e) {
        if (this.isDisabled() || mc.player.isElytraFlying()) {
            return;
        }
        if (this.noLiquid.getValue() && EntityUtil.isInLiquid() || mc.player.capabilities.isFlying) {
            return;
        }
        MovementUtil.strafe(e, MovementUtil.getSpeed());
    }


}
