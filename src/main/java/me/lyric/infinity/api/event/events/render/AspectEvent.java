package me.lyric.infinity.api.event.events.render;

import event.bus.Event;

/**
 * @author zzurio
 */

public class AspectEvent extends Event {

    private float aspect;

    public AspectEvent(float aspect) {
        this.aspect = aspect;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }
}
