package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.manager.client.HoleManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


@ModuleInformation(name = "HoleESP", description = "ESPs holes.", category = Category.Render)
public class HoleESP extends Module {
    public FloatSetting radius = createSetting("Radius", 8f, 0f, 50f);
    protected final FloatSetting lineWidth = createSetting("LineWidth", 0.1f, 0f, 5f);
    protected final FloatSetting height = createSetting("Height", 0f, 0f, 2f);
    public BooleanSetting doubles = createSetting("Doubles", true);
    public ModeSetting animation = createSetting("Animation", "Fade", Arrays.asList("Fade", "Grow"));
    protected final FloatSetting growSpeed = createSetting("GrowSpeed", 200f, 0f, 1000f);
    protected final FloatSetting distanceDivision = createSetting("Divisor", 6f, 0f, 50f);
    public ColorSetting bedrockBox = createSetting("Bedrock Box", defaultColor);
    public ColorSetting bedrockOutline = createSetting("Bedrock Outline", defaultColor);

    public ColorSetting obsidianBox = createSetting("Obsidian Box", defaultColor);

    public ColorSetting obsidianOutline = createSetting("Obsidian Outline", defaultColor);
    protected final HashMap<BlockPos, Long> holePosLongHashMap = new HashMap<>();
    protected final ICamera camera = new Frustum();

    @Override
    public void onRender3D(float partialTicks) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<HoleManager.HolePos> holes = Managers.HOLES.holes.stream().filter(holePos -> (mc.player.getDistanceSq(holePos.pos) < radius.getValue() * radius.getValue()) && (doubles.getValue() || (holePos.holeType.equals(HoleManager.Type.Bedrock) || holePos.holeType.equals(HoleManager.Type.Obsidian)))).collect(Collectors.toList());
        new HashMap<>(holePosLongHashMap).entrySet().stream().filter(entry -> holes.stream().noneMatch(holePos -> holePos.pos.equals(entry.getKey()))).forEach(entry -> holePosLongHashMap.remove(entry.getKey()));
        for (HoleManager.HolePos holePos : holes) {
            AxisAlignedBB bb = animation.getValue() == "Grow" ? new AxisAlignedBB(holePos.pos).shrink(0.5) : new AxisAlignedBB(holePos.pos);
            if (animation.getValue() == "Grow") {
                for (Map.Entry<BlockPos, Long> entry : holePosLongHashMap.entrySet()) {
                    if (entry.getKey().equals(holePos.pos)) {
                        bb = bb.grow(Math.min((System.currentTimeMillis() - entry.getValue()) / (1001f - growSpeed.getValue()), 0.5));
                    }
                }
            }
            final int bedrockAlpha = (int) Math.min(bedrockBox.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockBox.getValue().getAlpha());
            final int obsidianAlpha = (int) Math.min(obsidianBox.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianBox.getValue().getAlpha());
            final int bedrockOutlineAlpha = (int) Math.min(bedrockOutline.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockOutline.getValue().getAlpha());
            final int obsidianOutlineAlpha = (int) Math.min(obsidianOutline.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianOutline.getValue().getAlpha());
            final Color bedrockBoxColor = animation.getValue() == "Fade" ? new Color(bedrockBox.getValue().getRed() / 255.0f, bedrockBox.getValue().getGreen() / 255.0f, bedrockBox.getValue().getBlue() / 255.0f, bedrockAlpha / 255.0f) : bedrockBox.getValue();
            final Color obsidianBoxColor = animation.getValue() == "Fade" ? new Color(obsidianBox.getValue().getRed() / 255.0f, obsidianBox.getValue().getGreen() / 255.0f, obsidianBox.getValue().getBlue() / 255.0f, obsidianAlpha / 255.0f) : obsidianBox.getValue();
            final Color bedrockOutlineColor = animation.getValue() == "Fade" ? new Color(bedrockOutline.getValue().getRed() / 255.0f, bedrockOutline.getValue().getGreen() / 255.0f, bedrockOutline.getValue().getBlue() / 255.0f, bedrockOutlineAlpha / 255.0f) : bedrockOutline.getValue();
            final Color obsidianOutlineColor = animation.getValue() == "Fade" ? new Color(obsidianOutline.getValue().getRed() / 255.0f, obsidianOutline.getValue().getGreen() / 255.0f, obsidianOutline.getValue().getBlue() / 255.0f, obsidianOutlineAlpha / 255.0f) : obsidianOutline.getValue();
            if (camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                switch (holePos.holeType) {
                    case Bedrock:
                        RenderUtils.drawBoxWithHeight(bb, bedrockBoxColor, height.getValue());
                        RenderUtils.drawBlockOutlineBBWithHeight(bb, bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case Obsidian:
                        RenderUtils.drawBoxWithHeight(bb, obsidianBoxColor, height.getValue());
                        RenderUtils.drawBlockOutlineBBWithHeight(bb, obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleBedrockNorth:
                        RenderUtils.drawCustomBB(bedrockBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtils.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleObsidianNorth:
                        RenderUtils.drawCustomBB(obsidianBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtils.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleBedrockWest:
                        RenderUtils.drawCustomBB(bedrockBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtils.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleObsidianWest:
                        RenderUtils.drawCustomBB(obsidianBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtils.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                }
            }
            if (!holePosLongHashMap.containsKey(holePos.pos)) {
                holePosLongHashMap.put(holePos.pos, System.currentTimeMillis());
            }
        }
    }

    @Override
    public String getDisplayInfo()
    {
        if (animation.getValue() == "Grow")
        {
            return "grow, " + holePosLongHashMap.size();
        }
        if (animation.getValue() == "Fade")
        {
            return "fade, " + holePosLongHashMap.size();
        }
        return String.valueOf(holePosLongHashMap.size());

    }

}
