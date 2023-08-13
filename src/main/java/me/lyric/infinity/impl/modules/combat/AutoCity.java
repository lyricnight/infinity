package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.rotation.RotationType;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import me.lyric.infinity.api.util.minecraft.switcher.SwitchType;
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
    public Setting<Float> targetRange = register(new Setting<>("Target Range", "Range to target.", 10f, 2f, 15f));
    public Setting<Float> resetRange = register(new Setting<>("Reset Range", "Range at which to reset the block we are attempting to mine. Set this to your SpeedMine's range.", 4f, 1f, 6f));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", "Rotates to hit the block.", false));
    public Setting<Boolean> burrow = register(new Setting<>("Burrow", "Whether to mine player's burrow or not.", false));

    public Setting<RotationType> type = register(new Setting<>("Rotation Mode", "Mode for rotating.", RotationType.NORMAL).withParent(rotate));
    public Setting<Boolean> NoSwing = register(new Setting<>("No Swing","Handles swing.", true));
    public Setting<Boolean> move = register(new Setting<>("MovementCheck", "Leave on if you want autocity to only city when stationary.", false));
    public Setting<Boolean> holeCheck = register(new Setting<>("Hole Check","Checks if the target is in a hole.", true));
    public Setting<Boolean> pickCheck = register(new Setting<>("PickCheck", "If you want AutoCity to check if you have a pickaxe or not.", false));

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
        if (pickCheck.getValue())
        {
            int i = InventoryUtil.findHotbar(ItemPickaxe.class);
            if (i == -1)
            {
                ChatUtils.sendMessage(ChatFormatting.BOLD + "No pickaxe found! Disabling AutoCity...");
                toggle();
            }
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
            if (holeCheck.getValue() && !HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)target)) && !CombatUtil.isBurrow((EntityPlayer) target)) {
                mining = null;
                return;
            }
            if (mc.player.getDistanceSq(mining) > MathUtils.square(resetRange.getValue().intValue()))
            {
                mining = null;
            }
        }
        if (mining == null && getBurrowBlock((EntityPlayer)target) != null && burrow.getValue())
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
            float[] rotations = RotationUtil.getRotations(blockPos);
            RotationUtil.doRotation(type.getValue(), rotations);
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
}
