package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.rotation.Rotation;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.impl.modules.movement.InstantSpeed;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lyric
 */

@ModuleInformation(name = "HoleFiller", description = "bot", category = Category.Combat)
public class HoleFiller extends Module
{
    public ModeSetting switchMode = createSetting("SwitchMode","Silent",  Arrays.asList("Silent", "SilentPacket", "Slot"));

    public FloatSetting range = createSetting("Range", 5.0f, 1.0f, 10.0f);
    public FloatSetting wallRange = createSetting("WallRange", 3f, 1f, 10f);
    public IntegerSetting delay = createSetting("Delay", 1, 0, 1000);
    public IntegerSetting blocksPerTick = createSetting("BPT", 5, 1, 10);
    public BooleanSetting disableAfter = createSetting("Disable", false);
    public BooleanSetting onground = createSetting("OnGround",true);
    public BooleanSetting self = createSetting("SelfHoleCheck", false);
    public BooleanSetting rotate = createSetting("Rotate", true);

    public ModeSetting type = createSetting("Rotation Type","Packet", Arrays.asList("Packet", "Normal"), v -> rotate.getValue());
    public BooleanSetting doubles = createSetting("Doubles", true);
    public BooleanSetting smart = createSetting("Smart", true);
    public FloatSetting smartTargetRange = createSetting("SmartTargetRange",5.0f, 1.0f, 10.0f, v -> smart.getValue());
    public FloatSetting smartBlockRange = createSetting("SmartBlockRange",1.0f, 0.3f, 5.0f, v -> smart.getValue());

    private final Timer timer = new Timer();
    List<HoleUtil.Hole> holes = new ArrayList<HoleUtil.Hole>();
    EntityPlayer target = null;
    @Override
    public void onEnable() {
        timer.reset();
    }
    @Override
    public void onUpdate() {
        InventoryUtil.check(this);
        if (mc.player == null || mc.player.noClip || EntityUtil.isSuffocating(mc.player)) {
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
            BlockPos testPos = new BlockPos(target.posX + 0.5, target.posY + 0.5, target.posZ + 0.5);
            if (HoleUtil.isHole(testPos) || isBurrow(target))
            {
                return;
            }
        }
        int blocksPlaced = 0;
        if (timer.passedMs(delay.getValue())) {
            loadHoles();
            if (holes == null || holes.isEmpty()) {
                if (disableAfter.getValue()) {
                    ChatUtils.sendMessage(ChatFormatting.BOLD + "All holes filled, disabling HoleFiller...");
                    disable();
                }
                return;
            }
            int oldSlot = mc.player.inventory.currentItem;
            int blockSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int chestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            int slot = (blockSlot == -1 ? chestSlot : blockSlot);
            boolean switched = false;
            for (HoleUtil.Hole hole : holes) {
                Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = true;
                if (!switched) {
                    Switch.doSwitch(slot, switchMode.getValue());
                    switched = true;
                }
                if (hole.doubleHole) {
                    PlacementManager.placeBlock(hole.pos1, rotate.getValue(),type.getValue());
                    PlacementManager.placeBlock(hole.pos2, rotate.getValue(), type.getValue());
                }
                else {
                    PlacementManager.placeBlock(hole.pos1, rotate.getValue(), type.getValue());
                }
                if (++blocksPlaced >= blocksPerTick.getValue()) {
                    break;
                }
            }
            Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = false;
            if (switchMode.getValue() == "Slot")
            {
                Switch.switchBackAlt(slot);
            }
            else
            {
                Switch.doSwitch(oldSlot, switchMode.getValue());
            }
            timer.reset();
        }
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