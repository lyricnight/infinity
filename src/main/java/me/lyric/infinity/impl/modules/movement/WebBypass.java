package me.lyric.infinity.impl.modules.movement;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.mixins.accessors.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.START_SNEAKING;
import static net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SNEAKING;

/**
 * @author lyric :(
 */
@ModuleInformation(getName = "WebBypass", getDescription = "we FALLING out here", category = Category.Movement)
public class WebBypass extends Module {

    public Setting<Mode> mode = createSetting("Mode", "The mode of web bypass.", Mode.VANILLA));
    public BooleanSetting sneak = createSetting("Sneak", "Sneaks whilst falling through the web.", false));

    @Override
    public void onUpdate() {
        if (!nullSafe()) {
            return;
        }
        boolean sneaking = mc.player.isSneaking();
        if (((IEntity) mc.player).isInWeb()) {
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
                    //TODO: I removed this because of the stupid AccessTransformer
                    break;
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString().toLowerCase();
    }

    public enum Mode {
        STRICT,
        VANILLA
    }
}
