package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class AutoCity extends Module
{
    public Setting<Integer> targetRange = register(new Setting<>("Target Range", "Range to target.", 10, 2, 15));
    public Setting<Integer> resetRange = register(new Setting<>("Reset Range", "Range at which to reset the block we are attempting to mine. Set this to your SpeedMine's range.", 1, 4, 6));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", "Rotates to hit the block.", false));
    public Setting<Boolean> burrow = register(new Setting<>("Burrow", "Whether to mine player's burrow or not.", false));
    public Setting<Mode2> cityMode = register(new Setting<>("Switch", "Handles swap.", Mode2.SILENT));
    public Setting<Boolean> NoSwing = register(new Setting<>("No Swing","Handles swing.", true));
    public Setting<Boolean> move = register(new Setting<>("MovementCheck", "Leave on if you want autocity to only city when stationary.", false));
    public Setting<Boolean> holeCheck = register(new Setting<>("Hole Check","Checks if the target is in a hole.", true));

    BlockPos mining;
    long startTime;
    Entity target;
    int old;
    boolean swapBack;

    public AutoCity() {
        super("AutoCity", "Automatically cities opp", Category.COMBAT);
    }
    @Override
    public void onEnable()
    {
        startTime = 0L;
        old = 1;
        swapBack = false;
    }

    @Override
    public String getDisplayInfo()
    {
        if (mc.player == null)
        {
            return "";
        }
        if (target != null)
        {
            return ((EntityPlayer)target).getDisplayNameString().toLowerCase();
        }
        return ChatFormatting.RED + "none" + ChatFormatting.RESET;

    }
    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if(move.getValue() && SpeedUtil.anyMovementKeys())
        {
            return;
        }
        if ((target = CombatUtil.getTarget((targetRange.getValue()).doubleValue())) == null) {
            return;
        }
        if (mining != null) {
            if (mc.world.getBlockState(mining).getBlock() instanceof BlockAir) {
                mining = null;
                return;
            }
            if (holeCheck.getValue() && !HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)target)) && !isBurrow(target)) {
                mining = null;
                return;
            }
            if (mc.player.getDistanceSq(mining) > MathUtils.square(resetRange.getValue()))
            {
                mining = null;
                return;
            }
        }
        if (cityMode.getValue() == Mode2.REQUIRE_PICK) {
            if(mining == null && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && getBurrowBlock((EntityPlayer)target) != null && burrow.getValue())
            {
                mine(getBurrowBlock((EntityPlayer) target));
            }
            if (mining == null && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)target)) && getCityBlockSurround((EntityPlayer)target) != null) {
                mine(getCityBlockSurround((EntityPlayer)target));
            }

        }
        else if (mining == null && getBurrowBlock((EntityPlayer)target) != null && burrow.getValue())
        {
            mine(getBurrowBlock((EntityPlayer) target));
        }
        else if (mining == null && HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)target)) && getCityBlockSurround((EntityPlayer)target) != null) {
            mine(getCityBlockSurround((EntityPlayer)target));
        }
    }

    private void mine(final BlockPos blockPos) {
        if (rotate.getValue())
        {
            RotationManager.lookAtVec3dPacket(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true);
        }
        mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.UP);
        if (!NoSwing.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        mining = blockPos;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mining = null;
        swapBack = false;
    }

    public static List<BlockPos> getSurroundBlocks(final EntityPlayer player) {
        if (player == null) {
            return null;
        }
        final List<BlockPos> positions = new ArrayList<BlockPos>();
        for (final EnumFacing direction : EnumFacing.values()) {
            if (direction != EnumFacing.UP) {
                if (direction != EnumFacing.DOWN) {
                    final BlockPos pos = CombatUtil.getOtherPlayerPos(player).offset(direction);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN && canCityBlock(pos, direction)) {
                        positions.add(pos);
                    }
                }
            }
        }
        return positions;
    }
    public static boolean isBurrow(final Entity target) {
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }
    public static BlockPos getBurrowBlock(final EntityPlayer player)
    {
        if (player == null)
        {
            return null;
        }
        final BlockPos blockPos = new BlockPos(player.posX, player.posY, player.posZ);
        if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST))
        {
            return blockPos;
        }
        else
        {
            return null;
        }
    }

    public static BlockPos getCityBlockSurround(final EntityPlayer player) {
        final List<BlockPos> posList = getSurroundBlocks(player);
        posList.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>)MathUtils::distanceTo));
        return posList.isEmpty() ? null : posList.get(0);
    }
    public static boolean canCityBlock(final BlockPos blockPos, final EnumFacing direction) {
        return mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR || (mc.world.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.offset(direction).up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.BEDROCK));
    }
    public enum Mode2
    {
        REQUIRE_PICK,
        SILENT
    }
}
