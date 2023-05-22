package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.manager.client.HoleManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HoleESP extends Module {
    protected final Setting<Float> radius = register((new Setting<>("Radius", "Radius to render holes.", 8f, 0f, 50f)));
    protected final Setting<Float> lineWidth = register((new Setting<>("LineWidth", "Width of lines.", 0.1f, 0f, 5f)));
    protected final Setting<Float> height = register((new Setting<>("Height", "Height of box esp.", 0f, 0f, 2f)));
    public Setting<Boolean> doubles = register(new Setting<>("Doubles", "Renders double holes.", true));
    public Setting<Mode> animation = register(new Setting<Object>("Animation", "Mode of animation.", Mode.FADE));
    protected final Setting<Float> growSpeed = register((new Setting<>("GrowSpeed", "Speed for mode grow.", 200f, 0f, 1000f)));
    protected final Setting<Float> distanceDivision = register((new Setting<>("Divisor", "Divisor for mode fade.", 6f, 0f, 50f)));
    public Setting<ColorPicker> bedrockBox = register(new Setting<ColorPicker>("Bedrock Box", "Colour of bedrock box.", new ColorPicker(Color.green)));
    public Setting<ColorPicker> bedrockOutline = register(new Setting<ColorPicker>("Bedrock Outline", "Colour of bedrock box's outline.", new ColorPicker(Color.GREEN)));

    public Setting<ColorPicker> obsidianBox = register(new Setting<ColorPicker>("Obsidian Box", "Colour of obsidian box.", new ColorPicker(Color.RED)));

    public Setting<ColorPicker> obsidianOutline = register(new Setting<ColorPicker>("Obsidian Outline", "Colour of obsidian box's outline.", new ColorPicker(Color.RED)));
    protected final HashMap<BlockPos, Long> holePosLongHashMap = new HashMap<>();
    protected final ICamera camera = new Frustum();

    public HoleESP() {
        super("HoleESP", "Renders holes around you.", Category.RENDER);
    }

    @Override
    public void onRender3D(float partialTicks) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<HoleManager.HolePos> holes = Infinity.INSTANCE.holeManager.holes.stream().filter(holePos -> (mc.player.getDistanceSq(holePos.pos) < radius.getValue() * radius.getValue()) && (doubles.getValue() || (holePos.holeType.equals(HoleManager.Type.Bedrock) || holePos.holeType.equals(HoleManager.Type.Obsidian)))).collect(Collectors.toList());
        new HashMap<>(holePosLongHashMap).entrySet().stream().filter(entry -> holes.stream().noneMatch(holePos -> holePos.pos.equals(entry.getKey()))).forEach(entry -> holePosLongHashMap.remove(entry.getKey()));
        for (HoleManager.HolePos holePos : holes) {
            AxisAlignedBB bb = animation.getValue() == Mode.GROW ? new AxisAlignedBB(holePos.pos).shrink(0.5) : new AxisAlignedBB(holePos.pos);
            if (animation.getValue() == Mode.GROW) {
                for (Map.Entry<BlockPos, Long> entry : holePosLongHashMap.entrySet()) {
                    if (entry.getKey().equals(holePos.pos)) {
                        bb = bb.grow(Math.min((System.currentTimeMillis() - entry.getValue()) / (1001f - growSpeed.getValue()), 0.5));
                    }
                }
            }
            final int bedrockAlpha = (int) Math.min(bedrockBox.getValue().getColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockBox.getValue().getColor().getAlpha());
            final int obsidianAlpha = (int) Math.min(obsidianBox.getValue().getColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianBox.getValue().getColor().getAlpha());
            final int bedrockOutlineAlpha = (int) Math.min(bedrockOutline.getValue().getColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockOutline.getValue().getColor().getAlpha());
            final int obsidianOutlineAlpha = (int) Math.min(obsidianOutline.getValue().getColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianOutline.getValue().getColor().getAlpha());
            final Color bedrockBoxColor = animation.getValue() == Mode.FADE ? new Color(bedrockBox.getValue().getColor().getRed() / 255.0f, bedrockBox.getValue().getColor().getGreen() / 255.0f, bedrockBox.getValue().getColor().getBlue() / 255.0f, bedrockAlpha / 255.0f) : bedrockBox.getValue().getColor();
            final Color obsidianBoxColor = animation.getValue() == Mode.FADE ? new Color(obsidianBox.getValue().getColor().getRed() / 255.0f, obsidianBox.getValue().getColor().getGreen() / 255.0f, obsidianBox.getValue().getColor().getBlue() / 255.0f, obsidianAlpha / 255.0f) : obsidianBox.getValue().getColor();
            final Color bedrockOutlineColor = animation.getValue() == Mode.FADE ? new Color(bedrockOutline.getValue().getColor().getRed() / 255.0f, bedrockOutline.getValue().getColor().getGreen() / 255.0f, bedrockOutline.getValue().getColor().getBlue() / 255.0f, bedrockOutlineAlpha / 255.0f) : bedrockOutline.getValue().getColor();
            final Color obsidianOutlineColor = animation.getValue() == Mode.FADE ? new Color(obsidianOutline.getValue().getColor().getRed() / 255.0f, obsidianOutline.getValue().getColor().getGreen() / 255.0f, obsidianOutline.getValue().getColor().getBlue() / 255.0f, obsidianOutlineAlpha / 255.0f) : obsidianOutline.getValue().getColor();
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

    public enum Mode
    {
        NONE,
        GROW,
        FADE
    }
    @Override
    public String getDisplayInfo()
    {
        if (animation.getValue() == Mode.GROW)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + ChatFormatting.WHITE + "grow, " + holePosLongHashMap.size() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        if (animation.getValue() == Mode.FADE)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + ChatFormatting.WHITE  + "fade, " + holePosLongHashMap.size() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" +ChatFormatting.RESET  + ChatFormatting.WHITE + holePosLongHashMap.size() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";

    }

}
