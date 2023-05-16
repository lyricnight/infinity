package me.lyric.infinity.api.util.minecraft;

import me.lyric.infinity.Infinity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.Objects;

public class CombatUtil implements IGlobals {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static EntityLivingBase getTarget(final double targetRange) {
        return (EntityLivingBase)mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).filter(entity -> entity instanceof EntityPlayer).filter(CombatUtil::isAlive).filter(entity -> entity.getEntityId() != mc.player.getEntityId()).filter(entity -> !Infinity.INSTANCE.friendManager.isFriend((EntityPlayer) entity)).filter(entity -> mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> mc.player.getDistance(entity))).orElse(null);
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
    public static boolean isBlockAbovePlayerHead()
    {
        BlockPos head = new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ);
        return mc.world.getBlockState(head).getBlock() == Blocks.OBSIDIAN;
    }
    public static BlockPos getAntiCevPlacement()
    {
        return new BlockPos(mc.player.posX, mc.player.posY + 3, mc.player.posZ);
    }
    public static boolean isAlreadyPrevented()
    {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 3, mc.player.posZ);
        return mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

}