package me.lyric.infinity.api.event.entity;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class AttackEventPre extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
    Entity entity;

    public AttackEventPre(Entity attack) {
        this.entity = attack;
    }

    public Entity getEntity() {
        return entity;
    }

}
