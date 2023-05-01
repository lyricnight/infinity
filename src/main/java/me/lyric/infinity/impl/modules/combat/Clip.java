package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class Clip extends Module {

    public static Clip INSTANCE;

    private final Setting<Integer> timeout =
            register(new Setting<>("Timeout","time before packets are stopped", 5, 1, 10));

    private int packets;
    public static boolean isMoving() {
        return mc.gameSettings.keyBindForward.isKeyDown()
                || mc.gameSettings.keyBindBack.isKeyDown()
                || mc.gameSettings.keyBindLeft.isKeyDown()
                || mc.gameSettings.keyBindRight.isKeyDown();
    }
    public Clip() {
        super("Clip", "Clips into blocks nearby to prevent crystal damage.", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        packets = 0;
    }

    @Override
    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.RESET+ChatFormatting.WHITE + String.valueOf(packets).toLowerCase() +ChatFormatting.RESET+ ChatFormatting.GRAY + "]";
    }

    @Override
    public void onUpdate() {
        if (isMoving()) {
            toggle();
            return;
        }

        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
            mc.player.setPosition(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
            packets = 0;

        } else if (mc.player.ticksExisted % timeout.getValue() == 0) {
            mc.player.setPosition(mc.player.posX + MathHelper.clamp(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            packets++;
        }
    }

    private double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;

        if (d2 > d1) {
            return low;

        } else {
            return high;
        }
    }


}