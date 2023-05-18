package me.lyric.infinity.api.event.render;

import me.bush.eventbus.event.Event;

/**
 * @author lyric
 * for aspect module
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
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
