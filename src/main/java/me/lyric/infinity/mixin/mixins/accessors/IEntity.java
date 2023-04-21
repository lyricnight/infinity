package me.lyric.infinity.mixin.mixins.accessors;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author zzurio
 */

@Mixin(Entity.class)
public interface IEntity {

    @Accessor("isInWeb")
    boolean isInWeb();
}
