package me.lyric.infinity.api.event.events.player;

import me.bush.eventbus.event.Event;

public class TurnEvent extends Event {

    private final float yaw;
    private final float pitch;

    public TurnEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
