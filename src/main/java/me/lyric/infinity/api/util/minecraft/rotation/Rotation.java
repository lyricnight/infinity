package me.lyric.infinity.api.util.minecraft.rotation;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.math.Vec3d;

/**
 * @author lyric
 */

public class Rotation implements IGlobals {

    private final Rotate rotate;
    private float yaw;
    private float pitch;

    public Rotation(float yaw, float pitch, Rotate rotate) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = rotate;
    }

    public void updateModelRotations() {
        if (mc.player != null && mc.world != null) {
            switch (rotate) {
                case PACKET:
                    mc.player.renderYawOffset = this.yaw;
                    mc.player.rotationYawHead = this.yaw;
                    Infinity.INSTANCE.rotationManager.setHeadPitch(this.pitch);
                    break;
                case CLIENT:
                    mc.player.rotationYaw = this.yaw;
                    mc.player.rotationPitch = this.pitch;
                    break;
                case NONE:
                    break;
            }
        }
    }
    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }


    public enum Rotate {
        PACKET, CLIENT, NONE
    }
}
