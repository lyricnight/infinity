package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.mixins.accessors.IEntity;
import net.minecraft.network.play.client.CPacketEntityAction;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.START_SNEAKING;
import static net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SNEAKING;

/**
 * @author lyric :(
 */

public class WebBypass extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "The mode of web bypass.", Mode.VANILLA));
    public Setting<Boolean> sneak = register(new Setting<>("Sneak", "Sneaks whilst falling through the web.", false));


    public WebBypass() {
        super("WebBypass", "Prevents you from slowing down in webs.", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) {
            return;
        }
        boolean sneaking = mc.player.isSneaking();
        if (((IEntity) (me.lyric.infinity.mixin.transformer.IEntity) mc.player).isInWeb()) {
            switch (mode.getValue()) {
                case STRICT:
                    for (int i = 0; i < 10; i++)
                        if (sneak.getValue()) {
                            if (sneaking) {
                                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_SNEAKING));
                            }
                        }
                    mc.player.motionY--;
                    if (sneak.getValue()) {
                        if (sneaking) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, STOP_SNEAKING));
                        }
                    }
                    break;
                case VANILLA:
                    break;
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + mode.getValue().toString().toLowerCase() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

    public enum Mode {
        STRICT,
        VANILLA
    }
}
