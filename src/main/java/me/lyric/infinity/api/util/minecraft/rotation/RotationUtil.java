package me.lyric.infinity.api.util.minecraft.rotation;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.gl.Interpolation;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.manager.Managers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;

public class RotationUtil implements IGlobals {

    public static float[] getRotations(BlockPos pos, EnumFacing facing) {
        return getRotations(pos, facing, mc.player);
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from) {
        return getRotations(pos, facing, from, mc.world, mc.world.getBlockState(pos));
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from, IBlockAccess world, IBlockState state) {
        AxisAlignedBB bb = state.getBoundingBox(world, pos);

        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;

        if (facing != null) {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }

        return getRotations(x, y, z, from);
    }

    public static float[] getRotationsToTopMiddle(BlockPos pos) {
        return getRotations(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static float[] getRotationsToTopMiddleUp(BlockPos pos) {
        return getRotations(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }

    public static float[] getRotations(double x, double y, double z, Entity f) {
        return getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }

    public static float[] getRotations(Entity from, Entity entity, double height, double maxAngle) {
        return getRotations(entity, from.posX, from.posY, from.posZ, from.getEyeHeight(), height, maxAngle);
    }

    public static float[] getRotations(Entity entity, double fromX, double fromY, double fromZ, float eyeHeight, double height, double maxAngle) {
        float[] rotations = RotationUtil.getRotations(entity.posX, entity.posY + entity.getEyeHeight() * height, entity.posZ, fromX, fromY, fromZ, eyeHeight);
        return smoothen(rotations, maxAngle);
    }

    public static float[] smoothen(float[] rotations, double maxAngle) {
        float[] server = {Managers.ROTATIONS.getYaw(), Managers.ROTATIONS.getYaw()};
        return smoothen(server, rotations, maxAngle);
    }

    public static float[] smoothen(float[] server, float[] rotations, double maxAngle) {
        if (maxAngle >= 180.0f || maxAngle <= 0.0f || RotationUtil.angle(server, rotations) <= maxAngle) {
            return rotations;
        }
        return faceSmoothly(server[0], server[1], rotations[0], rotations[1], maxAngle, maxAngle);
    }

    public static float[] getRotations(double x, double y, double z, double fromX, double fromY, double fromZ, float fromHeight) {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float prevYaw = mc.player.prevCameraYaw;
        float diff = yaw - prevYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{prevYaw + diff, pitch};
    }

    public static float[] getRotations(Vec3d vec3d) {
        return getRotations(vec3d.x, vec3d.y, vec3d.z);
    }

    public static float[] getRotations(BlockPos pos) {
        return getRotations(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static float[] getRotations(Entity entity) {
        return getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static float[] getRotations(double x, double y, double z) {
        double xDiff = x - mc.player.posX;
        double yDiff = y - EntityUtil.getEyeHeight(mc.player);
        double zDiff = z - mc.player.posZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float diff = yaw - mc.player.rotationYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{mc.player.rotationYaw + diff, pitch};
    }

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin(MathUtils.rad(yaw)) * MathHelper.cos(MathUtils.rad(pitch));
        float vz = MathHelper.cos(MathUtils.rad(yaw)) * MathHelper.cos(MathUtils.rad(pitch));
        float vy = -MathHelper.sin(MathUtils.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public static double getAngle(Entity entity, double yOffset) {
        Vec3d vec3d = MathUtils.fromTo(Interpolation.interpolatedEyePos(), entity.posX, entity.posY + yOffset, entity.posZ);
        return MathUtils.angle(vec3d, Interpolation.interpolatedEyeVec());
    }

    public static void doRotation(String rotation, float[] angles) {
        switch (rotation) {
            case "Packet": {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angles[0], angles[1], mc.player.onGround));
                break;
            }
            case "Normal": {
                Managers.ROTATIONS.setRotations(angles[0], angles[1]);
                break;
            }
        }
    }

    public static float[] faceSmoothly(double curYaw, double curPitch, double intendedYaw, double intendedPitch, double yawSpeed, double pitchSpeed) {
        float yaw = updateRotation((float) curYaw, (float) intendedYaw, (float) yawSpeed);

        float pitch = updateRotation((float) curPitch, (float) intendedPitch, (float) pitchSpeed);

        return new float[]{yaw, pitch};
    }

    public static double angle(float[] rotation1, float[] rotation2) {
        Vec3d r1Vec = getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = getVec3d(rotation2[0], rotation2[1]);
        return MathUtils.angle(r1Vec, r2Vec);
    }

    public static float updateRotation(float current, float intended, float factor) {
        float updated = MathHelper.wrapDegrees(intended - current);

        if (updated > factor) {
            updated = factor;
        }

        if (updated < -factor) {
            updated = -factor;
        }

        return current + updated;
    }

    public static double normalizeAngle(Double angleIn) {
        double angle = angleIn;
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static Vec2f getRotationTo(Vec3d posTo, Vec3d posFrom) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double xz = Math.hypot(vec.x, vec.z);
        float yaw = (float) normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitch = (float) normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2f(yaw, pitch);
    }
}