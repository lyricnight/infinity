package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.SpeedUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * @author mioclient for the original module, WMS for weirdserver mode, rest is me
 */
public class Clip extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "Mode for clip.", Mode.CORNER));

    public Setting<Integer> tick = register(new Setting<>("TickExisted", "For clip mode corner.", 5, 1, 10));

    public Setting<Boolean> disable = register(new Setting<>("Disable", "Disables for you.", true));

    public Setting<Integer> updates = register(new Setting<>("Update-Amount", "Amount of ticks before autodisable disables.", 10, 1, 40).withParent(disable));

    int disableTime = 0;

    public Clip()
    {
        super("Clip", "least robotic player", Category.PLAYER);
    }

    @Override
    public void onDisable()
    {
        disableTime = 0;
    }

    @Override
    public void onUpdate()
    {
        if (!nullSafe()) return;

        if (SpeedUtil.anyMovementKeys())
        {
            toggle();
            return;
        }
        if (mode.getValue() == Mode.CORNER)
        {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
                mc.player.setPosition(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));

            } else if (mc.player.ticksExisted % tick.getValue() == 0) {
                mc.player.setPosition(mc.player.posX + MathHelper.clamp(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            }
        }
        if (mode.getValue() == Mode.WEIRDSERVER)
        {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.0042123,mc.player.posZ,mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.02141,mc.player.posZ,mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX,mc.player.posY - 0.097421,mc.player.posZ,500,500,mc.player.onGround));
        }
        disableTime++;
        if (disable.getValue()) {
            if (disableTime >= updates.getValue()) {
                toggle();
            }
        }

    }

    @Override
    public String getDisplayInfo()
    {
        return String.valueOf(disableTime);
    }

    private double roundToClosest(final double num, final double low, final double high) {
        final double d1 = num - low;
        final double d2 = high - num;
        if (d2 > d1) {
            return low;
        }
        return high;
    }

    private enum Mode
    {
        CORNER,
        WEIRDSERVER
    }

}
