package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.CombatUtil;
import me.lyric.infinity.api.util.minecraft.EntityUtil;
import me.lyric.infinity.api.util.minecraft.HoleUtil;
import me.lyric.infinity.api.util.minecraft.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.client.FriendManager;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.util.EnumHand.MAIN_HAND;


public class HoleFiller extends Module {

    public Setting<Boolean> rotate = register(new Setting<>("Rotate","Rotations to place blocks", false));
    public Setting<Boolean> smart = register(new Setting<>("Smart","Robot", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet","Packet rotations to prevent glitch blocks, may be slower", false).withParent(rotate));
    public Setting<Boolean> autoDisable = register(new Setting<>("AutoDisable","Disabler", true));
    public Setting<Integer> range = register(new Setting<>("Radius","Range to fill", 4, 0, 6));
    public Setting<Boolean> webs = register(new Setting<>("Webs","fuck prestige", true));
    public Setting<Boolean> wait = register(new Setting<>("Hole Wait","Waits for a target to leave their hole before holefilling. Recommended.", true).withParent(smart));

    private final Setting<Logic> logic = register(new Setting<>("Logic","set to hole when using smart.", Logic.PLAYER).withParent(smart));
    private final Setting<Integer> smartRange = register(new Setting<>("EnemyRange","Range to enemy", 4, 0, 6).withParent(smart));
    public Setting<Boolean> self = register(new Setting<>("SelfHoleCheck","Only fills if you are in a hole.", false));

    private EntityPlayer closestTarget;

    public HoleFiller() {
        super("HoleFiller", "Fills all safe spots in radius.", Category.COMBAT);
    }

    private enum Logic {
        PLAYER,
        HOLE
    }

    @Override
    public void onDisable() {
        closestTarget = null;
        RotationManager.resetRotationsPacket();
    }

    @Override
    public String getDisplayInfo() {
        if (mc.player == null)
        {
            return "";
        }
        if (closestTarget == null)
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.RED + "none" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" + ChatFormatting.RESET +ChatFormatting.WHITE + closestTarget.getDisplayNameString().toLowerCase() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) {
            return;
        }
        if (smart.getValue()) {
            findClosestTarget();
        }
        List<BlockPos> blocks = getPlacePositions();
        BlockPos q = null;

        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);

        if (obbySlot == -1 && eChestSlot == -1 && webSlot == -1)
        {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "No Valid Blocks! Disabling HoleFiller...");
            toggle();
            return;

        }
        int originalSlot = mc.player.inventory.currentItem;
        if(self.getValue() && (!isHole(getPlayerPos()) && !isBurrow(mc.player)))
        {
            return;
        }

        for (BlockPos blockPos : blocks) {
            if (!mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty()) continue;
            if (smart.getValue() && isInRange(blockPos)) {
                q = blockPos;
                continue;
            } else if (smart.getValue() && isInRange(blockPos) && logic.getValue() == Logic.HOLE && closestTarget.getDistanceSq(blockPos) <= smartRange.getValue()) {
                q = blockPos;
                continue;
            }
            q = blockPos;
        }


        if (q != null && mc.player.onGround) {
            mc.player.inventory.currentItem = webs.getValue() ? (webSlot == -1 ? (obbySlot == -1 ? eChestSlot : obbySlot) : webSlot) : (obbySlot == -1 ? eChestSlot : obbySlot);

            mc.playerController.updateController();
            PlacementManager.placeBlock(q, rotate.getValue(), packet.getValue(), true);

            if (mc.player.inventory.currentItem != originalSlot) {
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            }
            mc.player.swingArm(MAIN_HAND);
            mc.player.inventory.currentItem = originalSlot;
        }
        if (q == null && autoDisable.getValue() && !smart.getValue()) {
            toggle();
        }
    }
    private boolean isHole(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        BlockPos boost2 = pos.add(0, 0, 0);
        BlockPos boost3 = pos.add(0, 0, -1);
        BlockPos boost4 = pos.add(1, 0, 0);
        BlockPos boost5 = pos.add(-1, 0, 0);
        BlockPos boost6 = pos.add(0, 0, 1);
        BlockPos boost7 = pos.add(0, 2, 0);
        BlockPos boost8 = pos.add(0.5, 0.5, 0.5);
        BlockPos boost9 = pos.add(0, -1, 0);
        return !(mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR || mc.world.getBlockState(boost7).getBlock() != Blocks.AIR || mc.world.getBlockState(boost3).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(boost3).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(boost4).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(boost4).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(boost5).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(boost5).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(boost6).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(boost6).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(boost8).getBlock() != Blocks.AIR || mc.world.getBlockState(boost9).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(boost9).getBlock() != Blocks.BEDROCK);
    }

    public static boolean isBurrow(final Entity target) {
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return EntityUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || EntityUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    private BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private BlockPos getClosestTargetPos() {
        if (closestTarget != null) {
            return new BlockPos(Math.floor(closestTarget.posX), Math.floor(closestTarget.posY), Math.floor(closestTarget.posZ));
        }
        return null;
    }
    private BlockPos getTargetPos(EntityPlayer target)
    {
        return new BlockPos(Math.floor(target.posX), Math.floor(target.posY), Math.floor(target.posZ));
    }

    private void findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;

        closestTarget = null;

        for (EntityPlayer target : playerList) {
            if (target == mc.player || !EntityUtil.isLiving(target) || target.getHealth() <= 0.0f || Infinity.INSTANCE.friendManager.isFriend(String.valueOf(target))) continue;
            if(wait.getValue() && (isHole(getTargetPos(target)) || isBurrow(target))) continue;
            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }


            if (!(mc.player.getDistance(target) < mc.player.getDistance(closestTarget))) continue;
            closestTarget = target;
        }
    }

    private boolean isInRange(BlockPos blockPos) {
        NonNullList positions = NonNullList.create();

        positions.addAll(getSphere(getPlayerPos(), range.getValue(), range.getValue()).stream().filter(this::isHole).collect(Collectors.toList()));
        return positions.contains(blockPos);
    }

    private List<BlockPos> getPlacePositions() {
        NonNullList positions = NonNullList.create();

        if (smart.getValue() && closestTarget != null) {
            positions.addAll(getSphere(Objects.requireNonNull(getClosestTargetPos()), smartRange.getValue().floatValue(), range.getValue()).stream().filter(this::isHole).filter(this::isInRange).collect(Collectors.toList()));
        } else if (!smart.getValue()) {
            positions.addAll(getSphere(getPlayerPos(), range.getValue().floatValue(), range.getValue()).stream().filter(this::isHole).collect(Collectors.toList()));
        }
        return positions;
    }

    private List<BlockPos> getSphere(BlockPos loc, float r, int h) {

        ArrayList<BlockPos> circleBlocks = new ArrayList<>();

        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;

        while ((float)x <= (float)cx + r) {

            int z = cz - (int)r;

            while ((float)z <= (float)cz + r) {

                int y = cy - (int)r;

                while (true) {

                    float f = y;
                    float f2 = (float)cy + r;

                    if (!(f < f2)) break;

                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

                    if (dist < (double) (r * r)) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleBlocks;
    }
}
