package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * @author cpacketcustompayload
 */
@ModuleInformation(name = "ChorusControl", description = "Control chorus teleportation.", category = Category.Misc)
public class ChorusControl extends Module {

    public BooleanSetting cpacketplayer = createSetting("CPacketPlayer", true);
    public BooleanSetting spacketplayerposlook = createSetting("SPacketPlayerPosLook",true);

    public BooleanSetting render = createSetting("Render", true);
    public FloatSetting slabHeight = createSetting("Height", 0.1f, -1.0f, 1.0f);

    public ColorSetting color = createSetting("Color", defaultColor);

    Queue<CPacketPlayer> packets = new LinkedList<>();
    Queue<CPacketConfirmTeleport> teleportPackets = new LinkedList<>();

    SPacketPlayerPosLook sPacketPlayerPosLook;

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
            if (spacketplayerposlook.getValue()) event.cancel();
        }

        if (event.getPacket() instanceof CPacketPlayer) {
            packets.add(((CPacketPlayer) event.getPacket()));

            if (cpacketplayer.getValue())
                event.cancel();
        }

        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            teleportPackets.add(((CPacketConfirmTeleport) event.getPacket()));
            event.cancel();
        }
    }

    @Override
    public void onDisable() {
        while (!this.packets.isEmpty()) {
            mc.getConnection().sendPacket(Objects.requireNonNull(this.packets.poll()));
        }
        while (!this.teleportPackets.isEmpty()) {
            mc.getConnection().sendPacket(Objects.requireNonNull(this.teleportPackets.poll()));
        }
        sPacketPlayerPosLook = null;
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (sPacketPlayerPosLook == null) return;
        if (!render.getValue()) return;

        BlockPos pos = new BlockPos(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ());

        RenderUtils.drawBBSlab(new AxisAlignedBB(pos), slabHeight.getValue(), color.getValue());
    }

    @Override
    public void onLogout() {
        this.disable();
    }
}
