package me.lyric.infinity.manager.client;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.player.MotionUpdateEvent;
import me.lyric.infinity.api.util.minecraft.IGlobals;

/**
  @author i wonder
 **/

public class RotationManager implements IGlobals {
    private float yaw, pitch;
    private boolean rotated;
    private int ticksSinceNoRotate;
    private double x, y, z;
    private boolean onGround;

    public void init()
    {
        Infinity.INSTANCE.eventBus.subscribe(this);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event)
    {
        if(event.getStage() == 1)
        {
            x = mc.player.posX;
            y = mc.player.posY;
            z = mc.player.posZ;
            onGround = mc.player.onGround;
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
        }
        if(event.getStage() == 2)
        {
            mc.player.posX = x;
            mc.player.posY = y;
            mc.player.posZ = z;
            mc.player.onGround = onGround;
            ticksSinceNoRotate++;
            if (ticksSinceNoRotate > 2) {
                rotated = false;
            }
            mc.player.rotationYaw = yaw;
            mc.player.rotationYawHead = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    public void setRotations(float yaw, float pitch) {
        rotated = true;
        ticksSinceNoRotate = 0;
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isRotated() {
        return rotated;
    }
}