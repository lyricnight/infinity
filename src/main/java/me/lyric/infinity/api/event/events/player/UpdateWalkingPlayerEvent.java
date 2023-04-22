package me.lyric.infinity.api.event.events.player;

import event.bus.Event;
import event.bus.EventState;


public class UpdateWalkingPlayerEvent extends Event {

    private float yaw, pitch;
    public boolean rotationUsed;
    public UpdateWalkingPlayerEvent(EventState stage, float yaw, float pitch) {
        super(stage);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public UpdateWalkingPlayerEvent(EventState stage) {
        super(stage);
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

