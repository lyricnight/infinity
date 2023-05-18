package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.entity.AttackEventPre;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumParticleTypes;


public class CritRender extends Module {
    public CritRender()
    {
        super("CritRender", "For jaydon.", Category.RENDER);
    }

    @EventListener
    public void onAttack(AttackEventPre event)
    {
        if(event.getEntity() instanceof EntityEnderCrystal)
        {
            mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.CRIT);
        }
    }

}
