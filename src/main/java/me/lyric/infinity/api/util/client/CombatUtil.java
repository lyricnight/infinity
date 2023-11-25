package me.lyric.infinity.api.util.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.manager.Managers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentModifierDamage;

public class CombatUtil implements IGlobals {
    public static EntityPlayer getTarget(final double targetRange) {
        return (EntityPlayer) mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).filter(entity -> entity instanceof EntityPlayer).filter(CombatUtil::isAlive).filter(entity -> !entity.getName().equals(mc.player.getName())).filter(entity -> entity.getEntityId() != mc.player.getEntityId()).filter(entity -> !Managers.FRIENDS.isFriend(((EntityPlayer) entity).getDisplayNameString())).filter(entity -> mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> mc.player.getDistance(entity))).orElse(null);
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
        return mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }
    public static boolean isBurrow(EntityPlayer target) {
        if(mc.world == null || mc.player == null || target == null)
        {
            return false;
        }
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }
}