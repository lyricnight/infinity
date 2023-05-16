package me.lyric.infinity.api.event.events.render;

import me.bush.eventbus.event.Event;

public class Render3DEvent extends Event {
    public float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }
}