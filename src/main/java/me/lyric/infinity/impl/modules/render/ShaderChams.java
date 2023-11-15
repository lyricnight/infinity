package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.RenderNametagEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
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

/**
 * @author cpacket - .vert files from moneymod
 */

public class ShaderChams extends Module {

    public BooleanSetting players = createSetting("Players", "Renders the ESP on players.", true));
    public BooleanSetting crystals = createSetting("Crystals", "Renders the ESP on crystals.", true));
    public BooleanSetting mobs = createSetting("Mobs", "Renders the ESP on mobs.", false));
    public BooleanSetting items = createSetting("Items", "Renders the ESP on items.", false));

    public Setting<Shader> shader = createSetting("Shader", "The type of shader.", Shader.SPACE));

    // RAINBOW
    public BooleanSetting outline = createSetting("Outline", "Renders a dynamic rainbow gradient.", true));
    public FloatSetting rainbowSpeed = createSetting("Rainbow Speed", "The speed of the rainbow outline.", 0.4f, 0.0f, 1.0f).withParent(outline));
    public FloatSetting rainbowStrength = createSetting("Rainbow Strength", "The strength of the rainbow online.", 0.3f, 0.0f, 1.0f).withParent(outline));
    public FloatSetting saturation = createSetting("Saturation", "The saturation of the rainbow outline.", 0.5f, 0.0f, 1.0f).withParent(outline));
    public FloatSetting radius = createSetting("Radius", "The radius of the rainbow outline.", 1.0f, 0.1f, 5.0f).withParent(outline));
    public FloatSetting quality = createSetting("Quality", "The quality of the rainbow outline.", 1.0f, 0.1f, 5.0f).withParent(outline));

    public Setting<ColorPicker> color = createSetting("Color", "The color for the ESP.", new ColorPicker(Color.BLUE)));

    // Shader ESP Framebuffer.
    public FramebufferShader framebuffer = null;
    // Nametags variable due to bug with Shader ESP. (This fixes it.) TODO: Find a better way?
    boolean renderNametags;

    public ShaderChams() {
        super("ShaderChams", "Highlights entities in various ways.", Category.RENDER);
    }

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
                    OutlineShader.INSTANCE.stopDraw(color.getValue().getColor(), radius.getValue(), quality.getValue(), saturation.getValue(), 1, 0.5f, 0.5f);
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
                    framebuffer.stopDraw(color.getValue().getColor(), 1f, 1f, 0.8f, 1, 0.5f, 0.5f);
                }
                    switch (shader.getValue()) {
                        case SPACE:
                            framebuffer = SpaceShader.INSTANCE;
                            break;
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
                        // LOL
                    }
                    renderNametags = false;
                    framebuffer.stopDraw(color.getValue().getColor(), 1f, 1f, 0.8f, 1, 0.5f, 0.5f);
            } catch (NullPointerException nullPointerException) {
                // Do nothing. :)
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
        if(shader.getValue() == Shader.SPACE)
        {
            return "space";
        }
        return "rainbow";
    }


    public enum Shader {
        NONE,
        SPACE,
    }
}
