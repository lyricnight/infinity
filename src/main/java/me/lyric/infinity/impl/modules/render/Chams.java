package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.events.render.RenderLivingEntityEvent;
import me.lyric.infinity.api.event.events.render.crystal.RenderCrystalPostEvent;
import me.lyric.infinity.api.event.events.render.crystal.RenderCrystalPreEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
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

public class Chams extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "The chams mode.", Mode.MODEL));
    public Setting<Float> width = register(new Setting<>("Line Width", "The line width for the model.", 3.0f, 0.1f, 5.0f).withParent(mode));

    public Setting<Boolean> players = register(new Setting<>("Players", "Renders chams on players.", true));
    public Setting<Boolean> mobs = register(new Setting<>("Mobs", "Renders chams on mobs.", true));
    public Setting<Boolean> monsters = register(new Setting<>("Monsters", "Renders chams on monsters.", true));

    public Setting<Boolean> crystals = register(new Setting<>("Crystals", "Renders chams on crystals.", true));
    public Setting<Double> scale = register(new Setting<>("Scale", "Scale for the crystal.", 1.0, 0.1, 2.0).withParent(crystals));

    public Setting<Boolean> texture = register(new Setting<>("Textured", "Textures the entity.", false));
    public Setting<Boolean> lighting = register(new Setting<>("Lighting", "Disables vanilla lighting.", true));
    public Setting<Boolean> blend = register(new Setting<>("Blended", "Blends the texture.", false));
    public Setting<Boolean> transparent = register(new Setting<>("Transparent", "Renders the entity models as transparent.", true));
    public Setting<Boolean> depth = register(new Setting<>("Depth", "Enables entity depth.", true));
    public Setting<Boolean> walls = register(new Setting<>("Walls", "Enables chams to be rendered through walls.", true));

    public Setting<Boolean> xqz = register(new Setting<>("XQZ", "Secondary color for chams through walls.", false));
    public Setting<ColorPicker> playerXQZColor = register(new Setting<>("Player XQZ", "The XQZ color for players.", new ColorPicker(Color.BLUE)));
    public Setting<ColorPicker> crystalXQZColor = register(new Setting<>("CrystalXQZ", "the XQZ color for crystals.", new ColorPicker(Color.MAGENTA)));

    public Setting<Boolean> highlight = register(new Setting<>("Highlight", "Highlights the model of the entity.", true));
    public Setting<ColorPicker> crystalHighlightColor = register(new Setting<>("Crystal Highlight Color", "The highlight color for crystals.", new ColorPicker(Color.RED)));
    public Setting<ColorPicker> playerHighlightColor = register(new Setting<>("Player Highlight Color", "The highlight color for players.", new ColorPicker(Color.PINK)));

    public Chams() {
        super("Chams", "Renders entities through walls in various ways.", Category.RENDER);
    }

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
            GL11.glScaled(scale.getValue(), scale.getValue(), scale.getValue());
            if (!texture.getValue() && !mode.getValue().equals(Mode.SHINE)) {
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
                case WIRE: {
                    GL11.glPolygonMode(1032, 6913);
                    break;
                }
                case WIREMODEL:
                case MODEL: {
                    GL11.glPolygonMode(1032, 6914);
                }
            }
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(width.getValue());
            if (xqz.getValue()) {
                setColor(crystalXQZColor.getValue().getColor());
            }
            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            }
            if (walls.getValue() && !mode.getValue().equals(Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }
            if (mode.getValue().equals(Mode.WIREMODEL)) {
                GL11.glPolygonMode(1032, 6913);
            }
            if (highlight.getValue()) {
                setColor(mode.getValue().equals(Mode.WIREMODEL) ? new Color(crystalXQZColor.getValue().getColor().getRed(), crystalXQZColor.getValue().getColor().getGreen(), crystalXQZColor.getValue().getColor().getBlue(), 255) : crystalHighlightColor.getValue().getColor());
            }
            if (event.getEntityEnderCrystal().shouldShowBottom()) {
                event.getModelBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            } else {
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0.0f, rotation * 3.0f, rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
            }
            if (walls.getValue() && mode.getValue().equals(Mode.WIREMODEL)) {
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
            if (!texture.getValue() && !mode.getValue().equals(Mode.SHINE)) {
                GL11.glEnable(3553);
            }
            GL11.glScaled(1.0 / scale.getValue(), 1.0 / scale.getValue(), 1.0 / scale.getValue());
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
             event.setCanceled(true);
            }
            if (transparent.getValue()) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            if (!texture.getValue() && !mode.getValue().equals(Mode.SHINE)) {
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
                case WIRE: {
                    GL11.glPolygonMode(1032, 6913);
                    break;
                }
                case WIREMODEL:
                case MODEL: {
                    GL11.glPolygonMode(1032, 6914);
                }
            }
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(width.getValue());
            if (xqz.getValue()) {
                setColor(playerXQZColor.getValue().getColor());
            }
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (walls.getValue() && !mode.getValue().equals(Mode.WIREMODEL)) {
                GL11.glEnable(2929);
            }
            if (mode.getValue().equals(Mode.WIREMODEL)) {
                GL11.glPolygonMode(1032, 6913);
            }
            if (highlight.getValue()) {
                setColor(mode.getValue().equals(Mode.WIREMODEL) ? new Color(playerXQZColor.getValue().getColor().getRed(), playerXQZColor.getValue().getColor().getGreen(), playerXQZColor.getValue().getColor().getBlue(), 255) : playerHighlightColor.getValue().getColor());
            }
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (walls.getValue() && mode.getValue().equals(Mode.WIREMODEL)) {
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
            if (!texture.getValue() && !mode.getValue().equals(Mode.SHINE)) {
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

    public enum Mode {
        MODEL, WIRE, WIREMODEL, SHINE
    }

    @Override
    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + mode.getValue().toString().toLowerCase() + ChatFormatting.GRAY + "]";
    }
}
