package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.RenderNametagEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.gl.shader.FramebufferShader;
import me.lyric.infinity.api.util.gl.shader.shaders.OutlineShader;
import me.lyric.infinity.api.util.gl.shader.shaders.SpaceShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

/**
 * @author cpacket - .vert files from moneymod
 */

@ModuleInformation(name = "ShaderChams", description = "mid", category = Category.Render)
public class ShaderChams extends Module {

    public BooleanSetting players = createSetting("Players",  true);
    public BooleanSetting crystals = createSetting("Crystals",  true);
    public BooleanSetting mobs = createSetting("Mobs", false);
    public BooleanSetting items = createSetting("Items", false);

    public ModeSetting shader = createSetting("Shader", "Space", Arrays.asList("Space", "None"));

    // RAINBOW
    public BooleanSetting outline = createSetting("Outline", true);
    public FloatSetting rainbowSpeed = createSetting("Rainbow Speed",  0.4f, 0.0f, 1.0f, v -> outline.getValue());
    public FloatSetting rainbowStrength = createSetting("Rainbow Strength", 0.3f, 0.0f, 1.0f, v -> outline.getValue());
    public FloatSetting saturation = createSetting("Saturation", 0.5f, 0.0f, 1.0f, v -> outline.getValue());
    public FloatSetting radius = createSetting("Radius", 1.0f, 0.1f, 5.0f, v -> outline.getValue());
    public FloatSetting quality = createSetting("Quality", 1.0f, 0.1f, 5.0f, v -> outline.getValue());

    public ColorSetting color = createSetting("Color", defaultColor);

    // Shader ESP Framebuffer.
    public FramebufferShader framebuffer = null;
    // Nametags variable due to bug with Shader ESP. (This fixes it.) TODO: Find a better way?
    boolean renderNametags;

    //try-catch heaven
    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre event) {
        if (!nullSafe()) return;
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            try {
                if (outline.getValue()) {
                    GlStateManager.pushMatrix();
                    framebuffer = OutlineShader.INSTANCE;
                    OutlineShader.INSTANCE.setCustomValues(rainbowSpeed.getValue(), rainbowStrength.getValue(), saturation.getValue());
                    OutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    renderNametags = true;
                   try {
                       mc.world.loadedEntityList.forEach(entity -> {
                           if (entity != mc.player && ((entity instanceof EntityPlayer && players.getValue()) || (entity instanceof EntityEnderCrystal && crystals.getValue()))) {
                               mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), true);
                           }
                       });
                   } catch (ReportedException e) {
                       // Okay I have no idea what this happens but ok.
                   }
                    renderNametags = false;
                    OutlineShader.INSTANCE.stopDraw(color.getValue(), radius.getValue(), quality.getValue(), saturation.getValue(), 1, 0.5f, 0.5f);
                    GlStateManager.popMatrix();
                    framebuffer.startDraw(event.getPartialTicks());
                    renderNametags = true;
                   try {
                       mc.world.loadedEntityList.forEach(entity -> {
                           if (entity != mc.player && ((entity instanceof EntityPlayer && players.getValue()) || (entity instanceof EntityEnderCrystal && crystals.getValue()) || (entity instanceof EntityMob && mobs.getValue()) || (entity instanceof EntityItem && items.getValue()))) {
                               mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), true);
                           }
                       });
                   } catch (ReportedException e) {
                       // LMAO
                   }
                    renderNametags = false;
                    framebuffer.stopDraw(color.getValue(), 1f, 1f, 0.8f, 1, 0.5f, 0.5f);
                }
                if (shader.getValue().equals("Space")) {
                    framebuffer = SpaceShader.INSTANCE;
                }
                    framebuffer.startDraw(event.getPartialTicks());
                    renderNametags = true;
                    try {
                        mc.world.loadedEntityList.forEach(entity -> {
                            if (entity != mc.player && ((entity instanceof EntityPlayer && players.getValue()) || (entity instanceof EntityEnderCrystal && crystals.getValue()) || (entity instanceof EntityMob && mobs.getValue()) || (entity instanceof EntityItem && items.getValue()))) {
                                mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), true);
                            }
                        });
                    } catch (ReportedException e) {
                        e.printStackTrace();
                        // LOL
                    }
                    renderNametags = false;
                    framebuffer.stopDraw(color.getValue(), 1f, 1f, 0.8f, 1, 0.5f, 0.5f);
            } catch (NullPointerException nullPointerException) {
                // Do nothing. :)
                nullPointerException.printStackTrace();
            }
        }
    }

    @EventListener
    public void onRenderNametag(RenderNametagEvent event) {
        if (!nullSafe()) return;
        if (renderNametags)
            event.cancel();
    }
    @Override
    public String getDisplayInfo()
    {
        if(mc.player == null)
        {
            return "";
        }
        if(shader.getValue() == "Space")
        {
            return "space";
        }
        return "rainbow";
    }
}
