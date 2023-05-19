package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPreEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


public class CritRender extends Module {
    public CritRender()
    {
        super("CritRender", "For jaydon.", Category.RENDER);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event)
    {
        if (event.getPacket() instanceof CPacketUseEntity)
        {
            CPacketUseEntity p = (CPacketUseEntity) event.getPacket();
            Entity target = p.getEntityFromWorld(mc.world);
            assert target != null;
            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
        }
    }
    @EventListener
    public void onPacketReceive(PacketEvent.Receive event)
    {
        if(event.getPacket() instanceof SPacketExplosion)
        {
            SPacketExplosion p = (SPacketExplosion) event.getPacket();
            BlockPos pos = new BlockPos(p.posX, p.posY, p.posZ);
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            for (Entity entity : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, bb))
            {
                mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
            }
        }
    }
    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderCrystalPre(RenderCrystalPreEvent event)
    {
        if(Infinity.INSTANCE.moduleManager.getModuleByClass(CrystalModifier.class).isEnabled())
        {
            event.cancel();
            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount() * Infinity.INSTANCE.moduleManager.getModuleByClass(CrystalModifier.class).spinSpeed.getValue(), event.getAgeInTicks() * Infinity.INSTANCE.moduleManager.getModuleByClass(CrystalModifier.class).bounceFactor.getValue(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

        }
        else
        {
            event.cancel();
            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

        }
    }
}
