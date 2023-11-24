package me.lyric.infinity.api.util.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class RaytraceUtil implements IGlobals {

    public static RayTraceResult getRayTraceResult(float yaw, float pitch) {
        return getRayTraceResult(yaw, pitch, mc.playerController.getBlockReachDistance());
    }

    public static RayTraceResult getRayTraceResult(float yaw, float pitch, float distance) {
        Vec3d vec3d = EntityUtil.getEyesPos(mc.player);
        Vec3d lookVec = RotationUtil.getVec3d(yaw, pitch);
        Vec3d rotations = vec3d.add(lookVec.x * (double)distance, lookVec.y * (double)distance, lookVec.z * (double)distance);
        return Optional.ofNullable(
                mc.world.rayTraceBlocks(vec3d, rotations, false, false, false)).orElseGet(()
                -> new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN));
    }

    public static float[] hitVecToPlaceVec(BlockPos pos, Vec3d hitVec) {
        float x = (float)(hitVec.x - pos.getX());
        float y = (float)(hitVec.y - pos.getY());
        float z = (float)(hitVec.z - pos.getZ());

        return new float[]{x, y, z};
    }


}
