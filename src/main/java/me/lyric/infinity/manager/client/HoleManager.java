package me.lyric.infinity.manager.client;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.Render3DEvent;
import me.lyric.infinity.api.util.client.BlockUtil;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.impl.modules.render.HoleESP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HoleManager implements IGlobals {
    public ArrayList<HolePos> holes = new ArrayList<>();

    public void init()
    {
        Infinity.INSTANCE.eventBus.subscribe(this);
    }
    public Vec3i[] Hole = {
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1)
    };
    public Vec3i[] DoubleHoleNorth = {
            new Vec3i(0, 0, -2),
            new Vec3i(-1, 0, -1),
            new Vec3i(1, 0, -1),
            new Vec3i(0, -1, -1),
            new Vec3i(0, -1, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
            new Vec3i(0, 0, 1)
    };
    public Vec3i[] DoubleHoleWest = {
            new Vec3i(-2, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(-1, 0, -1),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(1, 0, 0)
    };

    @EventListener(priority = ListenerPriority.HIGH)
    public void onRender3D(Render3DEvent event) {
        boolean enable = Infinity.INSTANCE.moduleManager.getModuleByClass(HoleESP.class).isEnabled();
        if (enable)
        {
            Infinity.INSTANCE.threadManager.run(() -> holes = getHoles());
        }
    }

    public ArrayList<HolePos> getHoles() {
        final ArrayList<HolePos> holes = new ArrayList<>();
        for (final BlockPos pos : BlockUtil.getBlocksInRadius(Infinity.INSTANCE.moduleManager.getModuleByClass(HoleESP.class).radius.getValue().intValue(), false, 0).stream().filter(blockPos -> this.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)).collect(Collectors.toList())) {
            if (this.isEnterable(pos)) {
                boolean isSafe = true;
                for (final Vec3i vec3i : this.Hole) {
                    if (this.isntSafe(pos.add(vec3i))) {
                        isSafe = false;
                    }
                }
                if (isSafe) {
                    holes.add(new HolePos(pos, Type.Bedrock));
                }
                else {
                    boolean isUnsafe = true;
                    for (final Vec3i vec3i2 : this.Hole) {
                        if (this.isntUnsafe(pos.add(vec3i2))) {
                            isUnsafe = false;
                        }
                    }
                    if (isUnsafe) {
                        holes.add(new HolePos(pos, Type.Obsidian));
                    }
                    else {
                        boolean isSafeDoubleNorth = true;
                        for (final Vec3i vec3i3 : this.DoubleHoleNorth) {
                            if (this.isntSafe(pos.add(vec3i3))) {
                                isSafeDoubleNorth = false;
                            }
                        }
                        if (isSafeDoubleNorth) {
                            holes.add(new HolePos(pos, Type.DoubleBedrockNorth));
                        }
                        else {
                            boolean isUnSafeDoubleNorth = true;
                            for (final Vec3i vec3i4 : this.DoubleHoleNorth) {
                                if (this.isntUnsafe(pos.add(vec3i4))) {
                                    isUnSafeDoubleNorth = false;
                                }
                            }
                            if (isUnSafeDoubleNorth) {
                                holes.add(new HolePos(pos, Type.DoubleObsidianNorth));
                            }
                            else {
                                boolean isSafeDoubleWest = true;
                                for (final Vec3i vec3i5 : this.DoubleHoleWest) {
                                    if (this.isntUnsafe(pos.add(vec3i5))) {
                                        isSafeDoubleWest = false;
                                    }
                                }
                                if (isSafeDoubleWest) {
                                    holes.add(new HolePos(pos, Type.DoubleBedrockWest));
                                }
                                else {
                                    boolean isUnSafeDoubleWest = true;
                                    for (final Vec3i vec3i6 : this.DoubleHoleWest) {
                                        if (this.isntUnsafe(pos.add(vec3i6))) {
                                            isUnSafeDoubleWest = false;
                                        }
                                    }
                                    if (!isUnSafeDoubleWest) {
                                        continue;
                                    }
                                    holes.add(new HolePos(pos, Type.DoubleObsidianWest));
                                }
                            }
                        }
                    }
                }
            }
        }
        return holes;
    }

    public boolean isEnterable(BlockPos pos) {
        return mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR);
    }

    public boolean isntUnsafe(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN);
    }


    public boolean isntSafe(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK);
    }

    public enum Type {
        Bedrock,
        Obsidian,
        DoubleBedrockNorth,
        DoubleBedrockWest,
        DoubleObsidianNorth,
        DoubleObsidianWest
    }

    public static class HolePos {
        public BlockPos pos;
        public Type holeType;

        public HolePos(BlockPos pos, Type holeType) {
            this.pos = pos;
            this.holeType = holeType;
        }
    }
}

