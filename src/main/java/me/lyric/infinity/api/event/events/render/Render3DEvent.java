package me.lyric.infinity.api.event.events.render;

import me.lyric.infinity.api.event.Event;

public class Render3DEvent extends Event {
    public float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}