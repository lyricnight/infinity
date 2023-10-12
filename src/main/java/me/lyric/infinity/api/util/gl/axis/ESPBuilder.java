package me.lyric.infinity.api.util.gl.axis;

import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.function.Function;

public class ESPBuilder implements IGlobals
{
    private static final Color LIGHT_WHITE = new Color(255, 255, 255, 125);

    private Color color   =  LIGHT_WHITE;
    private Color outline =  Color.white;
    private Float width   =  1.5f;
    private Function<AxisAlignedBB, AxisAlignedBB> interpolation = bb -> bb;

    public ESPBuilder withColor(Color color)
    {
        this.color = color;
        return this;
    }

    public ESPBuilder withOutlineColor(Color outlineColor)
    {
        this.outline = outlineColor;
        return this;
    }

    public ESPBuilder withLineWidth(Float lineWidth)
    {
        this.width = lineWidth;
        return this;
    }

    public ESPBuilder withInterpolation(Function<AxisAlignedBB, AxisAlignedBB> interpolation)
    {
        this.interpolation = interpolation;
        return this;
    }

    public IAxis build()
    {
        return bb -> RenderUtils.renderBox(interpolation.apply(bb), color, outline, width);
    }

}