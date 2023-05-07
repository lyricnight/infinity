package me.lyric.infinity.api.util.minecraft;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Objects;

public class CombatUtil implements IGlobals {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static EntityLivingBase getTarget(final double targetRange) {
        return (EntityLivingBase)mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).filter(entity -> entity instanceof EntityPlayer).filter(CombatUtil::isAlive).filter(entity -> entity.getEntityId() != mc.player.getEntityId()).filter(entity -> !Infinity.INSTANCE.friendManager.isFriend((EntityPlayer) entity)).filter(entity -> mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> mc.player.getDistance(entity))).orElse(null);
    }

    public static HoleUtil.Hole getTargetHole(final double targetRange) {
        return HoleUtil.getHoles(targetRange, RotationManager.getPlayerPos(), false).stream().filter(hole -> mc.player.getDistanceSq(hole.pos1) <= targetRange).min(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole.pos1))).orElse(null);
    }

    public static HoleUtil.Hole getTargetHoleVec3D(final double targetRange) {
        return HoleUtil.getHoles(targetRange, RotationManager.getPlayerPos(), false).stream().filter(hole -> mc.player.getPositionVector().distanceTo(new Vec3d(hole.pos1.getX() + 0.5, mc.player.posY, hole.pos1.getZ() + 0.5)) <= targetRange).min(Comparator.comparingDouble(hole -> mc.player.getPositionVector().distanceTo(new Vec3d(hole.pos1.getX() + 0.5, mc.player.posY, hole.pos1.getZ() + 0.5)))).orElse(null);
    }

    public static boolean isAlive(final Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f;
    }

    public static boolean isLiving(final Entity entity) {
        return entity instanceof EntityLivingBase;
    }
    public static BlockPos getOtherPlayerPos(final EntityPlayer player) {
        final double decimalPoint = player.posY - Math.floor(player.posY);
        return new BlockPos(player.posX, (decimalPoint > 0.8) ? (Math.floor(player.posY) + 1.0) : Math.floor(player.posY), player.posZ);
    }
}