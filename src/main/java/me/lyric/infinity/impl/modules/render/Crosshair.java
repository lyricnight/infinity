package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.crosshair.CrosshairEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.mixin.mixins.gui.MixinGuiIngame;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

/**
 * @author lyric
 * {@link MixinGuiIngame}
 */

@ModuleInformation(name = "Crosshair", description = "Changes crosshair rendering.", category = Category.Render)
public class Crosshair extends Module {

    public BooleanSetting indicator = createSetting("Indicator", true);

    public BooleanSetting outline = createSetting("Outline", true);
    public ColorSetting outlineColor = createSetting("Outline Color", defaultColor, v -> outline.getValue());

    public ModeSetting gapMode = createSetting("Gap-Mode", "Dynamic", Arrays.asList("Dynamic", "Normal", "None"));
    public FloatSetting gapSize = createSetting("Gap Size",  2.0f, 0.5f, 20.f);

    public ColorSetting color = createSetting("Color", defaultColor);
    
    public FloatSetting length = createSetting("Length", 5.5f, 0.5f, 50.f);
    
    public FloatSetting width = createSetting("Width", 0.5f, 0.1f, 10.f);
    
    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre event) {
        if (!nullSafe()) return;

        final int resolutionMiddleX = event.getResolution().getScaledWidth() / 2;
        final int resolutionMiddleY = event.getResolution().getScaledHeight() / 2;

        if (gapMode.getValue() == "None") {
            return;
        } else {
            // Top.
            RenderUtils.drawBorderedRect(resolutionMiddleX - width.getValue(), resolutionMiddleY - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleX + (width.getValue()), resolutionMiddleY - (gapSize.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), 0.5f, color.getValue().getRGB(), outlineColor.getValue().getRGB());
            // Bottom.
            RenderUtils.drawBorderedRect(resolutionMiddleX - width.getValue(), resolutionMiddleY + (gapSize.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleX + (width.getValue()), resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), 0.5f, color.getValue().getRGB(), outlineColor.getValue().getRGB());
            // Left.
            RenderUtils.drawBorderedRect(resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleY - (width.getValue()), resolutionMiddleX - (gapSize.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleY + (width.getValue()), 0.5f, color.getValue().getRGB(), outlineColor.getValue().getRGB());
            // Right.
            RenderUtils.drawBorderedRect(resolutionMiddleX + (gapSize.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleY - (width.getValue()), resolutionMiddleX + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0), resolutionMiddleY + (width.getValue()), 0.5f, color.getValue().getRGB(), outlineColor.getValue().getRGB());
        }

        if (indicator.getValue()) {
            float f = this.mc.player.getCooledAttackStrength(0.0F);
            float indWidthInc = ((resolutionMiddleX + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0)) - (resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0))) / 17.f;
            if (f < 1.0f) {
                final float finWidth = (indWidthInc * (f * 17.f));
                RenderUtils.drawBorderedRect(resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0),
                        (resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0)) + 2,
                        resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0) + finWidth,
                        (resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == "Dynamic") ? gapSize.getValue() : 0)) + 2 + (width.getValue() * 2),
                        0.5f, color.getValue().getRGB(), outlineColor.getValue().getRGB());

            }
        }
    }

    @EventListener
    public void onRenderCrosshair(CrosshairEvent event) {
        if (!nullSafe()) return;
        event.setCancelled(true);
    }

    public static boolean isMoving() {
        return mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0;
    }

    public enum GapMode {
        NONE,
        NORMAL,
        DYNAMIC
    }

    @Override
    public String getDisplayInfo()
    {
        if(mc.player == null)
        {
            return "";
        }
        if (gapMode.getValue() == "None")
        {
            return "default-mc";
        }
        if (gapMode.getValue() == "Normal")
        {
            return "fixed";
        }
        if (gapMode.getValue() == "Dynamic")
        {
            return "dynamic";
        }
        return "";
    }
}
