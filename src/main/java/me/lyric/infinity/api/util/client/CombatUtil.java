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
    private final static DamageSource DAMAGE_SOURCE = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, 0, 0, 0, 6.0F, false, true));

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
    public static Set<BlockPos> getBlockedPositions(Entity entity) {
        return getBlockedPositions(entity.getEntityBoundingBox());
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb) {
        return getBlockedPositions(bb, 0.5);
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb, double offset) {
        Set<BlockPos> positions = new HashSet<>();

        double y = bb.minY;
        if (bb.minY - Math.floor(bb.minY) > offset) {
            y = Math.ceil(bb.minY);
        }

        positions.add(new BlockPos(bb.maxX, y, bb.maxZ));
        positions.add(new BlockPos(bb.minX, y, bb.minZ));
        positions.add(new BlockPos(bb.maxX, y, bb.minZ));
        positions.add(new BlockPos(bb.minX, y, bb.maxZ));

        return positions;
    }
    public static boolean canBreakWeakness(boolean checkStack) {
        if (!mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            return true;
        }

        int strengthAmp = 0;
        PotionEffect effect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);

        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }

        if (strengthAmp >= 1) {
            return true;
        }

        return checkStack && canBreakWeakness(mc.player.getHeldItemMainhand());
    }

    public static boolean canBreakWeakness(ItemStack stack) {
        return stack.getItem() instanceof ItemSword;
    }

    public static int findAntiWeakness() {
        int slot = -1;
        for (int i = 8; i > -1; i--) {
            if (canBreakWeakness(mc.player.inventory.getStackInSlot(i))) {
                slot = i;
                if (mc.player.inventory.currentItem == i) {
                    break;
                }
            }
        }

        return slot;
    }
    public static float getDifficultyMultiplier(float distance) {
        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0.0F;
            case EASY:
                return Math.min(distance / 2.0f + 1.0f, distance);
            case HARD:
                return distance * 3.0f / 2.0f;
        }

        return distance;
    }

    public static float calculate(BlockPos pos) {
        return calculate(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, mc.player);
    }

    public static float calculate(double x, double y, double z, EntityPlayer base) {
        return calculate(x, y, z, base, base.getEntityBoundingBox());
    }

    public static float calculate(double x, double y, double z, EntityPlayer base, AxisAlignedBB boundingBox) {
        double distance = base.getDistance(x, y, z) / 12.0D;
        if (distance > 1.0D) {
            return 0.0F;
        } else {
            final float density = getBlockDensity(new Vec3d(x, y, z), boundingBox);
            final double densityDistance = distance = (1.0D - distance) * density;
            float damage = CombatRules.getDamageAfterAbsorb(getDifficultyMultiplier((float) ((densityDistance * densityDistance + distance) / 2.0D * 7.0D * 12.0D + 1.0D)), base.getTotalArmorValue(), (float) base.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            final int modifierDamage = getEnchantmentModifierDamage(base.inventory.armorInventory, DAMAGE_SOURCE);
            if (modifierDamage > 0) {
                damage = CombatRules.getDamageAfterMagicAbsorb(damage, modifierDamage);
            }

            final PotionEffect resistance = base.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null) {
                damage = damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0F;
            }

            return Math.max(damage, 0.0F);
        }
    }
    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double x = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double y = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double z = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double xFloor = (1.0 - Math.floor(1.0 / x) * x) / 2.0;
        double zFloor = (1.0 - Math.floor(1.0 / z) * z) / 2.0;

        if (x >= 0.0D && y >= 0.0D && z >= 0.0D) {
            int air = 0;
            int traced = 0;

            for (float a = 0.0F; a <= 1.0F; a = (float) (a + x)) {
                for (float b = 0.0F; b <= 1.0F; b = (float) (b + y)) {
                    for (float c = 0.0F; c <= 1.0F; c = (float) (c + z)) {
                        double xOff = bb.minX + (bb.maxX - bb.minX) * a;
                        double yOff = bb.minY + (bb.maxY - bb.minY) * b;
                        double zOff = bb.minZ + (bb.maxZ - bb.minZ) * c;
                        RayTraceResult result = rayTraceBlocks(new Vec3d(xOff + xFloor, yOff, zOff + zFloor), vec, false, true, true);
                        if (result == null || result.typeOfHit == RayTraceResult.Type.MISS) {
                            air++;
                        }

                        traced++;
                    }
                }
            }
            return (float) air / (float) traced;
        } else {
            return 0.0F;
        }
    }
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i1 = MathHelper.floor(vec31.y);
        int j1 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
            return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
        }

        RayTraceResult raytraceresult2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
            if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                return null;
            }

            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            final double d6 = vec32.x - vec31.x;
            final double d7 = vec32.y - vec31.y;
            final double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            final IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
            final Block block1 = iblockstate1.getBlock();

            if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                if (block1.canCollideCheck(iblockstate1, stopOnLiquid) && block1 != Blocks.WEB) {
                    return iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);
                } else {
                    raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                }
            }

        }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

    public static float calculate(Entity crystal, EntityPlayer base) {
        return calculate(crystal.posX, crystal.posY, crystal.posZ, base);
    }


}