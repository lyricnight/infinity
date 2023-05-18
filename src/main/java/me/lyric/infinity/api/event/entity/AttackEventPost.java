package me.lyric.infinity.api.event.entity;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class AttackEventPost extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
    Entity entity;

    public AttackEventPost(Entity attack) {
        this.entity = attack;
    }

    public Entity getEntity() {
        return entity;
    }

}
