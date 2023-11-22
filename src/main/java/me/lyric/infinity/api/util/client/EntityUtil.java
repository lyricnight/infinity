package me.lyric.infinity.api.util.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.string.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;


/**
 * @author lyric
 */
public class EntityUtil implements IGlobals {
    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }
    public static boolean isInLiquid() {
        return EntityUtil.mc.player.isInWater() || EntityUtil.mc.player.isInLava();
    }
    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }
    public static String getFacing(final String in) {
        final String gray = ChatFormatting.DARK_GRAY + "";
        final String white = ChatFormatting.WHITE + "";
        final String facing = StringUtils.getTitle(in);
        String add;
        if (in.equalsIgnoreCase("North")) {
            add = " " + gray + "(" + white + "-Z" + gray + ")";
        }
        else if (in.equalsIgnoreCase("East")) {
            add = " " + gray + "(" + white + "+X" + gray + ")";
        }
        else if (in.equalsIgnoreCase("South")) {
            add = " " + gray + "(" + white + "+Z" + gray + ")";
        }
        else if (in.equalsIgnoreCase("West")) {
            add = " " + gray + "(" + white + "-X" + gray + ")";
        }
        else {
            add = " ERROR";
        }
        return facing + add;
    }
    public static double getEyeHeight(final Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }
    public static HoleUtil.Hole getTargetHoleVec3D(double targetRange, int deviation) {
        return HoleUtil.getHoles(targetRange, getPlayerPos(), false).stream().filter(hole -> mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, mc.player.posY, (double)hole.pos1.getZ() + 0.5)) <= targetRange).filter(hole -> mc.player.posY + deviation >= hole.pos1.getY()).min(Comparator.comparingDouble(hole -> mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, mc.player.posY, (double)hole.pos1.getZ() + 0.5)))).orElse(null);
    }
    public static BlockPos getPlayerPos() {
        double decimalPoint = mc.player.posY - Math.floor(mc.player.posY);
        return new BlockPos(mc.player.posX, decimalPoint > 0.8 ? Math.floor(mc.player.posY) + 1.0 : Math.floor(mc.player.posY), mc.player.posZ);
    }
    public static boolean isSuffocating(EntityPlayer player) {
        BlockPos playerPos = CombatUtil.getOtherPlayerPos(player);
        return mc.world.getBlockState(playerPos.up()).getBlock() != Blocks.AIR;
    }
}