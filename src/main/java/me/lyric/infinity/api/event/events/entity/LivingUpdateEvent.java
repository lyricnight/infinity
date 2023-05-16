package me.lyric.infinity.api.event.events.entity;

import me.bush.eventbus.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * author lyric ;)
 */

public class LivingUpdateEvent extends Event {

    private final EntityPlayerSP entityPlayerSP;
    private final boolean sprinting;

    public LivingUpdateEvent(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        this.entityPlayerSP = entityPlayerSP;
        this.sprinting = sprinting;
    }

    public EntityPlayerSP getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }
}