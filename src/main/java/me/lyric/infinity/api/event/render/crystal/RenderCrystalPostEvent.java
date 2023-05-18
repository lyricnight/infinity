package me.lyric.infinity.api.event.render.crystal;

import me.bush.eventbus.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityEnderCrystal;

/**
 * @author lyric <-_->
 */

public class RenderCrystalPostEvent extends Event {

    @Override
    protected boolean isCancellable() {
        return true;
    }
    private final ModelBase modelBase;
    private final ModelBase modelNoBase;
    private final EntityEnderCrystal entityEnderCrystal;
    private final double x;
    private final double y;
    private final double z;
    private final float entityYaw;
    private final float partialTicks;

    public RenderCrystalPostEvent(ModelBase modelBase, ModelBase modelNoBase, EntityEnderCrystal entityEnderCrystal, double x, double y, double z, float entityYaw, float partialTicks) {
        this.modelBase = modelBase;
        this.modelNoBase = modelNoBase;
        this.entityEnderCrystal = entityEnderCrystal;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
    }

    public ModelBase getModelBase() {
        return this.modelBase;
    }

    public ModelBase getModelNoBase() {
        return this.modelNoBase;
    }

    public EntityEnderCrystal getEntityEnderCrystal() {
        return this.entityEnderCrystal;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getEntityYaw() {
        return this.entityYaw;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
