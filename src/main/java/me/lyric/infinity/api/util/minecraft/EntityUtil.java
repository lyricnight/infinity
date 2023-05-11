package me.lyric.infinity.api.util.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EntityUtil implements IGlobals {
    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }
    public static boolean isInLiquid() {
        return EntityUtil.mc.player.isInWater() || EntityUtil.mc.player.isInLava();
    }
    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }
}