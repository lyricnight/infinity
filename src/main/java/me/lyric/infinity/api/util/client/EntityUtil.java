package me.lyric.infinity.api.util.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.string.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;


/**
 * @author lyric
 */
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
    public static String getFacing(final String in) {
        final String gray = ChatFormatting.DARK_GRAY + "";
        final String white = ChatFormatting.WHITE + "";
        final String facing = StringUtils.getTitle(in);
        String add;
        if (in.equalsIgnoreCase("North")) {
            add = " " + gray + "(" + white + "-Z" + gray + ")";
        }
        else if (in.equalsIgnoreCase("East")) {
            add = " " + gray + "(" + white + "+X" + gray + ")";
        }
        else if (in.equalsIgnoreCase("South")) {
            add = " " + gray + "(" + white + "+Z" + gray + ")";
        }
        else if (in.equalsIgnoreCase("West")) {
            add = " " + gray + "(" + white + "-X" + gray + ")";
        }
        else {
            add = " ERROR";
        }
        return facing + add;
    }



}