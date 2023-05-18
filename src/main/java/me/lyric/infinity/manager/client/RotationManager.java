package me.lyric.infinity.manager.client;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.event.player.UpdateWalkingPlayerEventPost;
import me.lyric.infinity.api.event.player.UpdateWalkingPlayerEventPre;
import me.lyric.infinity.api.event.render.RenderLivingEntityEvent;
import me.lyric.infinity.api.util.minecraft.HoleUtil;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.rotation.Rotation;
import me.lyric.infinity.mixin.mixins.accessors.IEntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;


/**
  @author lyric :)
 **/

public class RotationManager implements IGlobals {
    private static float yaw;
    private static float pitch;
    private float headPitch = -1;
    private Rotation serverRotation = new Rotation(0, 0, Rotation.Rotate.NONE);

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void updateRotations() {
        if (mc.player == null)
        {
            return;
        }
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }
    public static void resetRotations() {
        if (mc.player == null)
        {
            return;
        }

        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }
    public static void lookAtVec3dPacket(Vec3d vec, boolean normalize, boolean update) {
        float[] angle = getAngle(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], normalize ? (float) MathHelper.normalizeAngle((int) angle[1], 360) : angle[1], mc.player.onGround));

        if (update) {
            ((IEntityPlayerSP) mc.player).setLastReportedYaw(angle[0]);
            ((IEntityPlayerSP) mc.player).setLastReportedPitch(angle[1]);
        }
    }
    public static float[] getAngle(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{ mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (mc.player != null && mc.world != null) {
            headPitch = -1;
        }
    }
    @EventListener(priority = ListenerPriority.HIGH)
    public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEventPre e) {
        if (mc.player == null) return;
        RotationManager.updateRotations();

    }
    @EventListener(priority = ListenerPriority.HIGH)
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEventPost e) {
        if (mc.player == null) return;
        RotationManager.resetRotations();
    }
    public static void resetRotationsPacket() {
        if (mc.player == null)
        {
            return;
        }
        float[] angle = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer)
            serverRotation = new Rotation(((CPacketPlayer) event.getPacket()).getYaw(0), ((CPacketPlayer) event.getPacket()).getPitch(0), Rotation.Rotate.NONE);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (event.getEntityLivingBase().equals(mc.player)) {
            event.setCanceled(true);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), headPitch == -1 ? mc.player.rotationPitch : headPitch, event.getScaleFactor());
        }
    }

    public void setHeadPitch(float in) {
        headPitch = in;
    }

    public Rotation getServerRotation() {
        return this.serverRotation;
    }
    public static HoleUtil.Hole getTargetHoleVec3D(double targetRange) {
        return HoleUtil.getHoles(targetRange, RotationManager.getPlayerPos(), false).stream().filter(hole -> RotationManager.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, mc.player.posY, (double)hole.pos1.getZ() + 0.5)) <= targetRange).min(Comparator.comparingDouble(hole -> mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, mc.player.posY, (double)hole.pos1.getZ() + 0.5)))).orElse(null);
    }
    public static BlockPos getPlayerPos() {
        double decimalPoint = mc.player.posY - Math.floor(mc.player.posY);
        return new BlockPos(mc.player.posX, decimalPoint > 0.8 ? Math.floor(mc.player.posY) + 1.0 : Math.floor(mc.player.posY), mc.player.posZ);
    }
    public static Vec2f getRotationTo(Vec3d posTo, Vec3d posFrom) {
        return RotationManager.getRotationFromVec(posTo.subtract(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double xz = Math.hypot(vec.x, vec.z);
        float yaw = (float)RotationManager.normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitch = (float)RotationManager.normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2f(yaw, pitch);
    }
    public static double normalizeAngle(Double angleIn) {
        double angle = angleIn;
        angle %= 360.0;
        if (angle >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
}