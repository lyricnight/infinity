package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
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
    public Setting<Boolean> noLiquid = register(new Setting<>("NoLiquid","Disables module in liquid",  true));


    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
    }
    @Override
    public String getDisplayInfo()
    {
        if(noLiquid.getValue())
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "noliquid" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "bypass" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";

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
