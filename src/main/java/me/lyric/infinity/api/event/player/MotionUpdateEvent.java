package me.lyric.infinity.api.event.player;

import me.bush.eventbus.event.Event;

public class MotionUpdateEvent extends Event {
    private int stage;
    private double x;
    private double y;
    private double z;
    private float rotationYaw;
    private float rotationPitch;
    private boolean onGround;
    protected boolean modified;

    public MotionUpdateEvent(int stage, MotionUpdateEvent event) {
        this(stage, event.x, event.y, event.z, event.rotationYaw, event.rotationPitch, event.onGround);
    }

    public MotionUpdateEvent(int stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround) {
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
    }

    public boolean isModified() {
        return modified;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.modified = true;
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.modified = true;
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.modified = true;
        this.z = z;
    }

    public float getYaw() {
        return rotationYaw;
    }

    public void setYaw(float rotationYaw) {
        this.modified = true;
        this.rotationYaw = rotationYaw;
    }

    public float getPitch() {
        return rotationPitch;
    }

    public void setPitch(float rotationPitch) {
        this.modified = true;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.modified = true;
        this.onGround = onGround;
    }
    public int getStage()
    {
        return stage;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}