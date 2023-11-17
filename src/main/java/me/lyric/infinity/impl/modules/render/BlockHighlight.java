package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

/**
 * @author lyric
 */

@ModuleInformation(name = "BlockHighlight", description = "Highlights the block you are looking at.", category = Category.Render)
public class BlockHighlight extends Module {

    public ModeSetting mode = createSetting("Mode", "Outline" , Arrays.asList("Outline", "Fill", "Both", "Claw"));
    public FloatSetting renderWidth = createSetting("Width", 0.5f, 0.1f, 3.0f);

    // I wanted to make this depend on a certain option from a setting. TODO: this. //did it work?
    public FloatSetting clawHeight = createSetting("Claw Height", 0.3f, 0.1f, 1.0f, v -> mode.getValue() == "Claw");
    public ColorSetting primaryColor = createSetting("Gradient Color 1", defaultColor);
    public ColorSetting secondaryColor = createSetting("Gradient Color 2", defaultColor);

    public ColorSetting secondaryGradientColorOne = createSetting("Secondary Gradient Color 1", defaultColor);
    public ColorSetting getSecondaryGradientColorTwo = createSetting("Secondary Gradient Color 2", defaultColor);

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!nullSafe()) return;
        RayTraceResult rayTraceResult = mc.objectMouseOver;
        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = rayTraceResult.getBlockPos();
            switch (mode.getValue()) {
                case "Both":
                    RenderUtils.drawBBFill(new AxisAlignedBB(pos), secondaryColor.getValue(), primaryColor.getValue());
                    RenderUtils.drawBBOutline(new AxisAlignedBB(pos), renderWidth.getValue(), secondaryGradientColorOne.getValue(), getSecondaryGradientColorTwo.getValue());
                    break;
                case "Outline": {
                    RenderUtils.drawBBOutline(new AxisAlignedBB(pos), renderWidth.getValue(), primaryColor.getValue(), secondaryColor.getValue());
                    break;
                }
                case "Fill": {
                    RenderUtils.drawBBFill(new AxisAlignedBB(pos), secondaryColor.getValue(), primaryColor.getValue());
                    break;
                }
                case "Claw": {
                    RenderUtils.drawBBClaw(new AxisAlignedBB(pos), renderWidth.getValue(), clawHeight.getValue(), secondaryColor.getValue());
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
