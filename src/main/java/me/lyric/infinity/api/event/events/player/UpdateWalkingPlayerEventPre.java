package me.lyric.infinity.api.event.events.player;

import me.bush.eventbus.event.Event;

public class UpdateWalkingPlayerEventPre extends Event {
    private float yaw, pitch;
    public boolean rotationUsed;
    public UpdateWalkingPlayerEventPre(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }

    public UpdateWalkingPlayerEventPre() {

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
