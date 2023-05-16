package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.events.render.crosshair.CrosshairEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * @author lyric
 */

public class Crosshair extends Module {

    public Setting<Boolean> indicator = register(new Setting<>("Indicator", "Renders an attack indicator beneath the crosshair.", true));

    public Setting<Boolean> outline = register(new Setting<>("Outline", "Renders an outline on the crosshair.", true));
    public Setting<ColorPicker> outlineColor = register(new Setting<>("Outline Color", "The outline color of the crosshair.", new ColorPicker(Color.WHITE))).withParent(outline);

    public Setting<GapMode> gapMode = register(new Setting<>("Gap Mode", "The mode of the gap on the crosshair.", GapMode.NORMAL));
    public Setting<Float> gapSize = register(new Setting<>("Gap Size", "The size of the gap on the crosshair.", 2.0f, 0.5f, 20.f));

    public Setting<ColorPicker> color = register(new Setting<>("Color", "The color of the crosshair.", new ColorPicker(Color.BLUE)));
    public Setting<Float> length = register(new Setting<>("Length", "The length of the crosshair.", 5.5f, 0.5f, 50.f));
    public Setting<Float> width = register(new Setting<>("Width", "The width of the crosshair.", 0.5f, 0.1f, 10.f));

    public Crosshair() {
        super("Crosshair", "Renders your crosshair in various ways.", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre event) {
        if (!nullSafe()) return;

        final int resolutionMiddleX = event.getResolution().getScaledWidth() / 2;
        final int resolutionMiddleY = event.getResolution().getScaledHeight() / 2;

        if (gapMode.getValue() == GapMode.NONE) {

        } else {
            // Top.
            RenderUtils.drawBorderedRect(resolutionMiddleX - width.getValue(), resolutionMiddleY - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleX + (width.getValue()), resolutionMiddleY - (gapSize.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), 0.5f, color.getValue().getColor().getRGB(), outlineColor.getValue().getColor().getRGB());
            // Bottom.
            RenderUtils.drawBorderedRect(resolutionMiddleX - width.getValue(), resolutionMiddleY + (gapSize.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleX + (width.getValue()), resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), 0.5f, color.getValue().getColor().getRGB(), outlineColor.getValue().getColor().getRGB());
            // Left.
            RenderUtils.drawBorderedRect(resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleY - (width.getValue()), resolutionMiddleX - (gapSize.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleY + (width.getValue()), 0.5f, color.getValue().getColor().getRGB(), outlineColor.getValue().getColor().getRGB());
            // Right.
            RenderUtils.drawBorderedRect(resolutionMiddleX + (gapSize.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleY - (width.getValue()), resolutionMiddleX + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0), resolutionMiddleY + (width.getValue()), 0.5f, color.getValue().getColor().getRGB(), outlineColor.getValue().getColor().getRGB());
        }

        if (indicator.getValue()) {
            float f = this.mc.player.getCooledAttackStrength(0.0F);
            float indWidthInc = ((resolutionMiddleX + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0)) - (resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0))) / 17.f;
            if (f < 1.0f) {
                final float finWidth = (indWidthInc * (f * 17.f));
                RenderUtils.drawBorderedRect(resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0),
                        (resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0)) + 2,
                        resolutionMiddleX - (gapSize.getValue() + length.getValue()) - ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0) + finWidth,
                        (resolutionMiddleY + (gapSize.getValue() + length.getValue()) + ((isMoving() && gapMode.getValue() == GapMode.DYNAMIC) ? gapSize.getValue() : 0)) + 2 + (width.getValue() * 2),
                        0.5f, color.getValue().getColor().getRGB(), outlineColor.getValue().getColor().getRGB());

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
        if (gapMode.getValue() == GapMode.NONE)
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.WHITE + "default-mc" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        if (gapMode.getValue() == GapMode.NORMAL)
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.WHITE  + "fixed" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        if (gapMode.getValue() == GapMode.DYNAMIC)
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.WHITE  + "dynamic" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return "";
    }
}
