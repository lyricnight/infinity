package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.SpeedUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import me.lyric.infinity.api.util.metadata.MathUtils;

public class Clip extends Module {
    private final Setting<Integer> timeout = register(new Setting<>("Timeout","time before packets are stopped", 5, 1, 10));
    private int packets;
    public Clip() {
        super("Clip", "Clips into blocks nearby to prevent crystal damage.", Category.COMBAT);
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
        if (SpeedUtil.anyMovementKeys()) {
            toggle();
            return;
        }

        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
            mc.player.setPosition(MathUtils.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, MathUtils.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
            packets = 0;

        } else if (mc.player.ticksExisted % timeout.getValue() == 0) {
            mc.player.setPosition(mc.player.posX + MathHelper.clamp(MathUtils.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(MathUtils.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(MathUtils.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, MathUtils.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            packets++;
        }
    }




}