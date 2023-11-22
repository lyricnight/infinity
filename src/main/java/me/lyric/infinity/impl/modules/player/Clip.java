package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.impl.modules.movement.InstantSpeed;
import me.lyric.infinity.manager.Managers;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author mioclient for the original module, WMS for weirdserver mode, rest is me
 */
@ModuleInformation(name = "Clip", description = "we CLIPPING out here", category = Category.Player)
public class Clip extends Module {

    public ModeSetting mode = createSetting("Mode", "Corner", Arrays.asList("Corner", "5b"));

    public IntegerSetting tick = createSetting("TickExisted", 5, 1, 10);

    public BooleanSetting disable = createSetting("Disable", true);

    public IntegerSetting updates = createSetting("Update-Amount", 10, 1, 40, (Predicate<Integer>) v -> disable.getValue());

    int disableTime = 0;
    @Override
    public void onDisable()
    {
        Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = false;
        disableTime = 0;
    }

    @Override
    public void onUpdate()
    {
        if (!nullSafe()) return;

        if (SpeedUtil.anyMovementKeys())
        {
            disable();
            return;
        }
        Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = true;
        if (Objects.equals(mode.getValue(), "Corner"))
        {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
                mc.player.setPosition(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));

            } else if (mc.player.ticksExisted % tick.getValue() == 0) {
                mc.player.setPosition(mc.player.posX + MathHelper.clamp(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            }
        }
        if (Objects.equals(mode.getValue(), "5b"))
        {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.0042123,mc.player.posZ,mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.02141,mc.player.posZ,mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX,mc.player.posY - 0.097421,mc.player.posZ,500,500,mc.player.onGround));
        }
        disableTime++;
        if (disable.getValue()) {
            if (disableTime >= updates.getValue()) {
                disable();
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
}
