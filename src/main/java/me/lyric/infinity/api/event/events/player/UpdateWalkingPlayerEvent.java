package me.lyric.infinity.api.event.events.player;

import event.bus.Event;
import event.bus.EventState;


public class UpdateWalkingPlayerEvent extends Event {
    public UpdateWalkingPlayerEvent(EventState e) {
        super(e);
    }
}

