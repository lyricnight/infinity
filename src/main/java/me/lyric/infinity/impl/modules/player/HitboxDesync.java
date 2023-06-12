package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.MathHelper.abs;

/**
 * @author cattyn and mironov ig?
    idek if this works
 */

public class HitboxDesync extends Module {
    private static final double MAGIC_OFFSET = .200009968835369999878673424677777777777761;

    public HitboxDesync() {
        super("HitboxDesync", "Crashes chinese crystal auras.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        EnumFacing f = mc.player.getHorizontalFacing();
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        Vec3d center = bb.getCenter();
        Vec3d offset = new Vec3d(f.getDirectionVec());

        Vec3d fin = merge(new Vec3d(new BlockPos(MathHelper.floor(center.x), MathHelper.floor(center.y), MathHelper.floor(center.z))).add(.5, 0, .5).add(offset.scale(MAGIC_OFFSET)), f);
        mc.player.setPosition(fin.x == 0 ? mc.player.posX : fin.x, mc.player.posY, fin.z == 0 ? mc.player.posZ : fin.z);
        toggle();
    }

    private Vec3d merge(Vec3d a, EnumFacing facing) {
        return new Vec3d(a.x * abs(facing.getDirectionVec().getX()), a.y * abs(facing.getDirectionVec().getY()), a.z * abs(facing.getDirectionVec().getZ()));
    }
}