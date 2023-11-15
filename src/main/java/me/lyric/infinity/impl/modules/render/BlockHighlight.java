package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * @author lyric
 */

public class BlockHighlight extends Module {

    public Setting<Mode> mode = createSetting("Mode", "The mode of rendering.", Mode.CLAW));
    public FloatSetting renderWidth = createSetting("Width", "The line width.", 0.5f, 0.1f, 3.0f));

    // I wanted to make this depend on a certain option from a setting. TODO: this.
    //TODO: Fade?
    public FloatSetting clawHeight = createSetting("Claw Height", "The height of the claw.", 0.3f, 0.1f, 1.0f));

    public Setting<ColorPicker> primaryColor = createSetting("Gradient Color 1", "The first color of the gradient.", new ColorPicker(Color.BLUE)));
    public Setting<ColorPicker> secondaryColor = createSetting("Gradient Color 2", "The second color of the gradient.", new ColorPicker(Color.MAGENTA)));

    public Setting<ColorPicker> secondaryGradientColorOne = createSetting("Secondary Gradient Color 1", "The first color of mode BOTH.", new ColorPicker(Color.GREEN)));
    public Setting<ColorPicker> getSecondaryGradientColorTwo = createSetting("Secondary Gradient Color 2", "The second color of mode BOTH.", new ColorPicker(Color.RED)));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you are looking at.", Category.RENDER);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!nullSafe()) return;
        RayTraceResult rayTraceResult = mc.objectMouseOver;
        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = rayTraceResult.getBlockPos();
            switch (mode.getValue()) {
                case BOTH:
                    RenderUtils.drawBBFill(new AxisAlignedBB(pos), secondaryColor.getValue().getColor(), primaryColor.getValue().getColor());
                    RenderUtils.drawBBOutline(new AxisAlignedBB(pos), renderWidth.getValue(), secondaryGradientColorOne.getValue().getColor(), getSecondaryGradientColorTwo.getValue().getColor());
                    break;
                case OUTLINE: {
                    RenderUtils.drawBBOutline(new AxisAlignedBB(pos), renderWidth.getValue(), primaryColor.getValue().getColor(), secondaryColor.getValue().getColor());
                    break;
                }
                case FILL: {
                    RenderUtils.drawBBFill(new AxisAlignedBB(pos), secondaryColor.getValue().getColor(), primaryColor.getValue().getColor());
                    break;
                }
                case CLAW: {
                    RenderUtils.drawBBClaw(new AxisAlignedBB(pos), renderWidth.getValue(), clawHeight.getValue(), secondaryColor.getValue().getColor());
                    break;
                }
            }
        }
    }

    public enum Mode {
        OUTLINE,
        FILL,
        BOTH,
        CLAW
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString().toLowerCase();
    }
}
