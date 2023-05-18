package me.lyric.infinity.api.event.player;

import me.bush.eventbus.event.Event;

public class UpdateWalkingPlayerEventPost extends Event {
    private float yaw, pitch;
    public boolean rotationUsed;
    public UpdateWalkingPlayerEventPost(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }

    public UpdateWalkingPlayerEventPost() {

    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.rotationUsed = true;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.rotationUsed = true;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}
