package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * @author lyric
 * base is mio but heavily modified with double support and doesn't holefill yourself like mio does
 */

public class HoleFiller extends Module {

    public Setting<Boolean> rotate = register(new Setting<>("Rotate","Rotations to place blocks", false));
    public Setting<Boolean> smart = register(new Setting<>("Smart","Robot", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet","Packet rotations to prevent glitch blocks, may be slower", false).withParent(rotate));
    public Setting<Boolean> autoDisable = register(new Setting<>("AutoDisable","Disabler", true));
    public Setting<Integer> range = register(new Setting<>("Radius","Range to fill", 4, 0, 6));
    public Setting<Boolean> webs = register(new Setting<>("Webs","fuck prestige", true));
    public Setting<Boolean> wait = register(new Setting<>("HoleWait","Waits for a target to leave their hole before holefilling. Recommended.", true).withParent(smart));

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
        if (closestTarget == null)
        {
            return;
        }
        List<HoleUtil.Hole> holes = HoleUtil.getHoles(range.getValue(), getTargetPos(closestTarget), true);
        BlockPos q = null;
        BlockPos r = null;

        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);

        if (obbySlot == -1 && eChestSlot == -1 && webSlot == -1)
        {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "No Valid Blocks! Disabling HoleFiller...");
            toggle();
            return;

        }
        if(self.getValue() && (!HoleUtil.isHole(getPlayerPos()) && !isBurrow(mc.player)))
        {
            return;
        }

        for (HoleUtil.Hole pos : holes) {
            if (!mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.pos1)).isEmpty()) continue;
            if (smart.getValue()) {
                if (pos.doubleHole)
                {
                    q = pos.pos1;
                    r = pos.pos2;
                }
                else
                {
                    q = pos.pos1;
                }
                continue;
            } else if (smart.getValue() && logic.getValue() == Logic.HOLE && closestTarget.getDistanceSq(pos.pos1) <= smartRange.getValue()) {
                if (pos.doubleHole)
                {
                    q = pos.pos1;
                    r = pos.pos2;
                }
                else
                {
                    q = pos.pos1;
                }
                continue;
            }
            if (pos.doubleHole)
            {
                q = pos.pos1;
                r = pos.pos2;
            }
            else
            {
                q = pos.pos1;
            }
        }
        int slot = webSlot == -1 ? (obbySlot == -1 ? eChestSlot : obbySlot) : webSlot;
        if (r == null)
        {
            if (q != null && mc.player.onGround) {
                Switch.placeBlockWithSwitch(webs.getValue() ? slot : (obbySlot == -1 ? eChestSlot : obbySlot), rotate.getValue(), packet.getValue(), q, true);
            }
            if (q == null && autoDisable.getValue() && !smart.getValue()) {
                toggle();
            }
        }
        else
        {
            if (q != null && mc.player.onGround) {
                Switch.placeBlockWithSwitch(webs.getValue() ? slot : (obbySlot == -1 ? eChestSlot : obbySlot), rotate.getValue(), packet.getValue(), q, true);
                Switch.placeBlockWithSwitch(webs.getValue() ? slot : (obbySlot == -1 ? eChestSlot : obbySlot), rotate.getValue(), packet.getValue(), r, true);
            }
            if (q == null && autoDisable.getValue() && !smart.getValue()) {
                toggle();
            }
        }
    }

    public static boolean isBurrow(final Entity target) {
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return EntityUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || EntityUtil.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    private BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
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
            if(wait.getValue() && (HoleUtil.isHole(getTargetPos(target)) || isBurrow(target))) continue;
            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }


            if (!(mc.player.getDistance(target) < mc.player.getDistance(closestTarget))) continue;
            closestTarget = target;
        }
    }
}
