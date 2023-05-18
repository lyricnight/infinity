package me.lyric.infinity.manager.client;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.Render3DEvent;
import me.lyric.infinity.api.util.minecraft.BlockUtil;
import me.lyric.infinity.impl.modules.render.HoleESP;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HoleManager {
    public Minecraft mc = Minecraft.getMinecraft();
    public ArrayList<HolePos> holes = new ArrayList<>();

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
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
    public void onRender3D(Render3DEvent ignored) {
        boolean enable = Infinity.INSTANCE.moduleManager.getModuleByClass(HoleESP.class).isEnabled();
        if (enable)
        {
            Infinity.INSTANCE.threadManager.run(() -> holes = getHoles());
        }
    }

    public ArrayList<HolePos> getHoles() {
        ArrayList<HolePos> holes = new ArrayList<>();
        for (BlockPos pos : BlockUtil.getSphere(50, BlockUtil.AirType.OnlyAir, mc.player).stream().filter(blockPos -> mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)).collect(Collectors.toList())) {
            if (isEnterable(pos)) {
                boolean isSafe = true;
                for (Vec3i vec3i : Hole) {
                    if (isntSafe(pos.add(vec3i))) {
                        isSafe = false;
                    }
                }
                if (isSafe) {
                    holes.add(new HolePos(pos, Type.Bedrock));
                    continue;
                }
                boolean isUnsafe = true;
                for (Vec3i vec3i : Hole) {
                    if (isntUnsafe(pos.add(vec3i))) {
                        isUnsafe = false;
                    }
                }
                if (isUnsafe) {
                    holes.add(new HolePos(pos, Type.Obsidian));
                    continue;
                }
                boolean isSafeDoubleNorth = true;
                for (Vec3i vec3i : DoubleHoleNorth) {
                    if (isntSafe(pos.add(vec3i))) {
                        isSafeDoubleNorth = false;
                    }
                }
                if (isSafeDoubleNorth) {
                    holes.add(new HolePos(pos, Type.DoubleBedrockNorth));
                    continue;
                }
                boolean isUnSafeDoubleNorth = true;
                for (Vec3i vec3i : DoubleHoleNorth) {
                    if (isntUnsafe(pos.add(vec3i))) {
                        isUnSafeDoubleNorth = false;
                    }
                }
                if (isUnSafeDoubleNorth) {
                    holes.add(new HolePos(pos, Type.DoubleObsidianNorth));
                    continue;
                }
                boolean isSafeDoubleWest = true;
                for (Vec3i vec3i : DoubleHoleWest) {
                    if (isntUnsafe(pos.add(vec3i))) {
                        isSafeDoubleWest = false;
                    }
                }
                if (isSafeDoubleWest) {
                    holes.add(new HolePos(pos, Type.DoubleBedrockWest));
                    continue;
                }
                boolean isUnSafeDoubleWest = true;
                for (Vec3i vec3i : DoubleHoleWest) {
                    if (isntUnsafe(pos.add(vec3i))) {
                        isUnSafeDoubleWest = false;
                    }
                }
                if (isUnSafeDoubleWest) {
                    holes.add(new HolePos(pos, Type.DoubleObsidianWest));
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

