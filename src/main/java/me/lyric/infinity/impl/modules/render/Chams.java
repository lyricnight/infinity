package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.RenderLivingEntityEvent;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPostEvent;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPreEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

/**
 * @author cpacket
 */

@ModuleInformation(name = "Chams", description = "Module is not updated and isn't very good", category = Category.Render)
public class Chams extends Module {

    public ModeSetting mode = createSetting("Mode", "Model", Arrays.asList("Model", "Wire", "Wiremodel", "Shine"));
    
    public FloatSetting width = createSetting("Line Width", 3.0f, 0.1f, 5.0f);

    public BooleanSetting players = createSetting("Players", true);
    public BooleanSetting mobs = createSetting("Mobs", true);
    public BooleanSetting monsters = createSetting("Monsters", true);

    public BooleanSetting crystals = createSetting("Crystals", true);

    public BooleanSetting texture = createSetting("Textured", false);
    public BooleanSetting lighting = createSetting("Lighting", true);
    public BooleanSetting blend = createSetting("Blended", false);
    public BooleanSetting transparent = createSetting("Transparent", true);
    public BooleanSetting depth = createSetting("Depth",  true);
    public BooleanSetting walls = createSetting("Walls",  true);

    public BooleanSetting xqz = createSetting("XQZ",  false);
    public ColorSetting playerXQZColor = createSetting("Player XQZ", defaultColor);
    public ColorSetting crystalXQZColor = createSetting("CrystalXQZ", defaultColor);

    public BooleanSetting highlight = createSetting("Highlight",  true);
    public ColorSetting crystalHighlightColor = createSetting("Crystal Highlight Color", defaultColor);
    public ColorSetting playerHighlightColor = createSetting("Player Highlight Color", defaultColor);
    @EventListener
    public void onRenderCrystalPre(RenderCrystalPreEvent event) {
        if (!nullSafe()) return;
        if (crystals.getValue()) {
            event.cancel();
        }
    }

    @EventListener
    public void onRenderCrystalPost(RenderCrystalPostEvent event) {
        if (!nullSafe()) return;
        if (crystals.getValue()) {
            if (transparent.getValue()) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            float rotation = (float) event.getEntityEnderCrystal().innerRotation + event.getPartialTicks();
            float rotationMoved = MathHelper.sin(rotation * 0.2f) / 2.0f + 0.5f;
            rotationMoved = (float) ((double) rotationMoved + Math.pow(rotationMoved, 2.0));
            GL11.glTranslated(event.getX(), event.getY(), event.getZ());
            if (!texture.getValue() && !mode.getValue().equals("Shine")) {
                GL11.glDisable(3553);
            }
            if (blend.getValue()) {
                GL11.glEnable(3042);
            }
            if (lighting.getValue()) {
                GL11.glDisable(2896);
            }
            if (depth.getValue()) {
                GL11.glDepthMask(false);
            }
            if (walls.getValue()) {
                GL11.glDisable(2929);
            }
            switch (mode.getValue()) {
                case "Wire": {
                    GL11.glPolygonMode(1032, 6913);
                    break;
                }
                case "Wiremodel":
                case "Model": {
                    GL11.glPolygonMode(1032, 6914);
                }
            }
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(width.getValue());
            if (xqz.getValue()) {
                setColor(crystalXQZColor.getValue());
            }
            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            }
            if (walls.getValue() && !mode.getValue().equals("Wiremodel")) {
                GL11.glEnable(2929);
            }
            if (mode.getValue().equals("Wiremodel")) {
                GL11.glPolygonMode(1032, 6913);
            }
            if (highlight.getValue()) {
                setColor(mode.getValue().equals("Wiremodel") ? new Color(crystalXQZColor.getValue().getRed(), crystalXQZColor.getValue().getGreen(), crystalXQZColor.getValue().getBlue(), 255) : crystalHighlightColor.getValue());
            }
            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            }
            if (walls.getValue() && mode.getValue().equals("Wiremodel")) {
                GL11.glEnable(2929);
            }
            if (lighting.getValue()) {
                GL11.glEnable(2896);
            }
            if (depth.getValue()) {
                GL11.glDepthMask(true);
            }
            if (blend.getValue()) {
                GL11.glDisable(3042);
            }
            if (!texture.getValue() && !mode.getValue().equals("Shine")) {
                GL11.glEnable(3553);
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    @EventListener
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (!nullSafe()) return;
        if ((event.getEntityLivingBase() instanceof EntityOtherPlayerMP && players.getValue() || (isPassiveMob(event.getEntityLivingBase()) || isNeutralMob(event.getEntityLivingBase())) && mobs.getValue() != false || isHostileMob(event.getEntityLivingBase()) && monsters.getValue())) {
            if (!texture.getValue())
            {
             event.setCancelled(true);
            }
            if (transparent.getValue()) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            if (!texture.getValue() && !mode.getValue().equals("Shine")) {
                GL11.glDisable(3553);
            }
            if (blend.getValue()) {
                GL11.glEnable(3042);
            }
            if (lighting.getValue()) {
                GL11.glDisable(2896);
            }
            if (depth.getValue()) {
                GL11.glDepthMask(false);
            }
            if (walls.getValue()) {
                GL11.glDisable(2929);
            }
            switch (mode.getValue()) {
                case "Wire": {
                    GL11.glPolygonMode(1032, 6913);
                    break;
                }
                case "Wiremodel":
                case "Model": {
                    GL11.glPolygonMode(1032, 6914);
                }
            }
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(width.getValue());
            if (xqz.getValue()) {
                setColor(playerXQZColor.getValue());
            }
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (walls.getValue() && !mode.getValue().equals("Wiremodel")) {
                GL11.glEnable(2929);
            }
            if (mode.getValue().equals("Wiremodel")) {
                GL11.glPolygonMode(1032, 6913);
            }
            if (highlight.getValue()) {
                setColor(mode.getValue().equals("Wiremodel") ? new Color(playerXQZColor.getValue().getRed(), playerXQZColor.getValue().getGreen(), playerXQZColor.getValue().getBlue(), 255) : playerHighlightColor.getValue());
            }
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (walls.getValue() && mode.getValue().equals("Wiremodel")) {
                GL11.glEnable(2929);
            }
            if (lighting.getValue()) {
                GL11.glEnable(2896);
            }
            if (depth.getValue()) {
                GL11.glDepthMask(true);
            }
            if (blend.getValue()) {
                GL11.glDisable(3042);
            }
            if (!texture.getValue() && !mode.getValue().equals("Shine")) {
                GL11.glEnable(3553);
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    // Convenience function for Chams.
    public static void setColor(Color color) {
        GL11.glColor4d((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
    }

    public static boolean isPassiveMob(Entity entity) {
        if (entity instanceof EntityWolf && ((EntityWolf) entity).isAngry()) {
            return false;
        }
        if (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid) {
            return true;
        }
        return entity instanceof EntityIronGolem && ((EntityIronGolem) entity).getRevengeTarget() == null;
    }

    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity)) || entity instanceof EntitySpider;
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    @Override
    public String getDisplayInfo() {
        return mode.getValue().toLowerCase();
    }
}
