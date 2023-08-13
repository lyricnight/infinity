package me.lyric.infinity.api.event.player;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.MoverType;
import org.jetbrains.annotations.NotNull;

public class MoveEvent extends Event {
    private MoverType moverType;

    private double motionX, motionY, motionZ;

    public MoveEvent(MoverType type, double motionX, double motionY, double motionZ) {
        this.moverType = type;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }
    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }


    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }

}