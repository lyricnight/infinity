package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.rotation.Rotation;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lyric
 * much better than before
 */

public class HoleFiller extends Module
{
    public Setting<Mode> switchMode = register(new Setting<>("Mode", "Mode for switch", Mode.SILENT));
    public Setting<Float> range = register(new Setting<>("Range", "Range for placing.", 5.0f, 1.0f, 10.0f));
    public Setting<Float> wallRange = register(new Setting<>("WallRange", "Range for placing through walls.", 3f, 1f, 10f));
    public Setting<Integer> delay = register(new Setting<>("Delay", "Delay of blockplacement",1, 0, 1000));
    public Setting<Integer> blocksPerTick = register(new Setting<>("BPT", "this", 5, 1, 10));
    public Setting<Boolean> disableAfter = register(new Setting<>("Disable", "for dumb hf", false));
    public Setting<Boolean> onground = register(new Setting<>("OnGround", "only fills if you are on the ground.", true));
    public Setting<Boolean> self = register(new Setting<>("SelfHoleCheck", "only fills if you are in a hole.", false));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", "rots", true));
    public Setting<Boolean> doubles = register(new Setting<>("Doubles", "double holes!!", true));
    public Setting<Boolean> smart = register(new Setting<>("smart", "smartypants", true));
    public Setting<Float> smartTargetRange = register(new Setting<>("SmartTargetRange","Range for smart to find a target.",5.0f, 1.0f, 10.0f));
    public Setting<Float> smartBlockRange = register(new Setting<>("SmartBlockRange","Range for smart fill.",1.0f, 0.3f, 5.0f));

    private Timer timer = new Timer();
    List<HoleUtil.Hole> holes = new ArrayList<HoleUtil.Hole>();
    EntityPlayer target = null;

    public HoleFiller() {
        super("HoleFiller","very good for strict and other servers.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        timer.reset();
    }
    @Override
    public void onDisable()
    {
        RotationManager.resetRotationsPacket();
    }
    @Override
    public void onUpdate() {
        InventoryUtil.check(this);
        if (mc.player == null) {
            return;
        }
        if(onground.getValue() && !mc.player.onGround)
        {
            return;
        }
        if (self.getValue() && (!HoleUtil.isHole(mc.player.getPosition()) && !isBurrow(mc.player)))
        {
            return;
        }
        target = CombatUtil.getTarget((smartTargetRange.getValue()).doubleValue());
        if (target != null)
        {
            if (HoleUtil.isHole(target.getPosition()) || isBurrow(target))
            {
                return;
            }
        }
        int blocksPlaced = 0;
        if (timer.passedMs(delay.getValue())) {
            getHoles();
            if (holes == null || holes.size() == 0) {
                if (disableAfter.getValue()) {
                    ChatUtils.sendMessage(ChatFormatting.BOLD + "All holes filled, disabling HoleFiller...");
                    toggle();
                }
                return;
            }
            if (switchMode.getValue() == Mode.REQUIRE && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                return;
            }
            final int oldSlot = mc.player.inventory.currentItem;
            final int blockSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            final int chestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            boolean switched = false;
            for (final HoleUtil.Hole hole : holes) {
                if (!switched) {
                    doSwitch(blockSlot == -1 ? chestSlot : blockSlot);
                    switched = true;
                }
                if (hole.doubleHole) {
                    PlacementManager.placeBlock(hole.pos1, rotate.getValue());
                    PlacementManager.placeBlock(hole.pos2, rotate.getValue());
                }
                else {
                    PlacementManager.placeBlock(hole.pos1, rotate.getValue());
                }
                if (++blocksPlaced >= ((Number)blocksPerTick.getValue()).intValue()) {
                    break;
                }
            }
            if ((switchMode.getValue() == Mode.SILENT || switchMode.getValue() == Mode.NORMAL) && switched) {
                doSwitch(oldSlot);
            }
            timer.reset();
        }
    }

    public void getHoles() {
        loadHoles();
    }
    public static boolean isBurrow(EntityPlayer target) {
        if(mc.world == null || mc.player == null || target == null)
        {
            return false;
        }
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    public void loadHoles() {
        holes = HoleUtil.getHoles(range.getValue().doubleValue(), mc.player.getPosition(), doubles.getValue()).stream().filter(hole -> {
            boolean isAllowedHole = true;
            AxisAlignedBB bb = hole.doubleHole ? new AxisAlignedBB(hole.pos1.getX(), hole.pos1.getY(), hole.pos1.getZ(), (hole.pos2.getX() + 1), (hole.pos2.getY() + 1), (hole.pos2.getZ() + 1)) : new AxisAlignedBB(hole.pos1);
            for (Entity e : mc.world.getEntitiesWithinAABB(Entity.class, bb)) {
                isAllowedHole = false;
            }
            return isAllowedHole;
        }).filter(hole -> {
            boolean isAllowedSmart = false;
            if (smart.getValue()) {
                if (target != null && target.getDistance((double)hole.pos1.getX() + 0.5, (hole.pos1.getY() + 1), (double)hole.pos1.getZ() + 0.5) < smartBlockRange.getValue().doubleValue()) {
                    isAllowedSmart = true;
                }
            } else {
                isAllowedSmart = true;
            }
            return isAllowedSmart;
        }).filter(hole -> {
            BlockPos pos = hole.pos1.add(0, 1, 0);
            boolean raytrace = mc.world.rayTraceBlocks(Rotation.getEyesPos(), new Vec3d(pos)) != null;
            return !raytrace || mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= wallRange.getValue().doubleValue();
        }).collect(Collectors.toList());
    }

    public void doSwitch(final int i) {
        if (switchMode.getValue() == Mode.NORMAL) {
            Switch.switchToSlot(i);
        }
        if (switchMode.getValue() == Mode.SILENT) {
            Switch.switchToSlotGhost(i);
        }
    }
    public enum Mode
    {
        NORMAL,
        REQUIRE,
        SILENT
    }
    @Override
    public String getDisplayInfo()
    {
        if (mc.player == null || mc.world == null) return "";
        if (target != null)
        {
            if (holes == null)
            {
                return ChatFormatting.RED + target.getDisplayName().getFormattedText().toLowerCase()+ ChatFormatting.RESET;

            }
            else {
                return target.getDisplayName().getFormattedText().toLowerCase();

            }
        }
        return ChatFormatting.RED +"none"+ ChatFormatting.RESET;
    }
}