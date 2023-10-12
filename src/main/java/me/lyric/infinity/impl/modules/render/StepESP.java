package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.gl.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class StepESP extends Module {
    public StepESP()
    {
        super("StepESP", "Shows positions you can step from.", Category.RENDER);
    }

    public Setting<Mode> mode = register(new Setting<>("Mode", "Mode to render.", Mode.Normal));

    public Setting<ColorPicker> colorPick = register(new Setting<>("Colour", "Colour of esp.", new ColorPicker(Color.PINK)));

    public Setting<Float> height = register(new Setting<>("Height", "Height of ESP.", 1.0f, -1.0f, 1.0f));

    @Override
    public void onRender3D(float partialTicks)
    {
        BlockPos pos = EntityUtil.getPosition(mc.player, 1.0);
        BlockPos alt = pos.up(2);
        if (mc.world.getBlockState(alt).getMaterial().blocksMovement())
        {
            if (mode.getValue() == Mode.Alternate)
            {
                return;
            }

            RenderUtils.renderPos(alt, colorPick.getValue().getColor(), 0.0f, height.getValue());

            for (EnumFacing facing : EnumFacing.HORIZONTALS)
            {
                BlockPos off = pos.offset(facing);
                if (!mc.world.getBlockState(off).getMaterial().blocksMovement())
                {
                    continue;
                }

                off = off.up();
                IBlockState state = mc.world.getBlockState(off);
                if (state.getMaterial().blocksMovement()
                        && state.getBoundingBox(mc.world, off)
                        == Block.FULL_BLOCK_AABB)
                {
                    if (mode.getValue() == Mode.Normal)
                    {
                        RenderUtils.renderPos(off, colorPick.getValue().getColor(), 0.0f, height.getValue());
                    }

                    continue;
                }

                IBlockState up = mc.world.getBlockState(off.up());
                if (up.getMaterial().blocksMovement())
                {
                    if (mode.getValue() == Mode.Normal)
                    {
                        RenderUtils.renderPos(off, colorPick.getValue().getColor(), 0.0f, height.getValue());
                    }

                    continue;
                }

                if (mode.getValue() == Mode.Alternate)
                {
                    RenderUtils.renderPos(off, colorPick.getValue().getColor(), 0.0f, height.getValue());
                }
            }
        }
    }
    private enum Mode
    {
        Normal,
        Alternate
    }
}
