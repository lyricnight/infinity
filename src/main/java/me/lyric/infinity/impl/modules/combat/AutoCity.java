package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.minecraft.CombatUtil;
import me.lyric.infinity.api.util.minecraft.HoleUtil;
import me.lyric.infinity.api.util.minecraft.Switch;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

public class AutoCity extends Module
{
    public Setting<Mode> mode = register(new Setting<>("Mode", "Mode of City", Mode.SMART));
    public Setting<Integer> targetRange = register(new Setting<>("Target Range", "Range to target", 10, 2, 15));
    public Setting<Integer> range = register(new Setting<>("Break Range", "Range to break.", 4, 1, 9));

    public Setting<Mode2> cityMode = register(new Setting<>("Switch", "Handles swap.", Mode2.SWITCH));
    public Setting<Boolean> autoBreak = register(new Setting<>("Auto Break","Handles swapback.", true));
    public Setting<Boolean> NoSwing = register(new Setting<>("No Swing","Handles swing.", true));

    public Setting<Boolean> holeCheck = register(new Setting<>("Hole Check","Checks if the target is in a hole.", true));

    public Setting<Boolean> Abort = register(new Setting<>("Abort","Handles aborting speedmine.", true));

    BlockPos mining;
    long startTime;
    Entity target;
    int old;
    boolean swapBack;

    public AutoCity() {
        super("AutoCity", "Automatically cities opp", Category.COMBAT);
        this.startTime = 0L;
        this.old = 1;
        this.swapBack = false;
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
            return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.WHITE + target.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" + ChatFormatting.RESET + ChatFormatting.RED + "none" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";

    }
    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }
        if ((this.target = (Entity) CombatUtil.getTarget(((Number)this.targetRange.getValue()).doubleValue())) == null) {
            return;
        }
        if ((boolean)this.autoBreak.getValue() && this.swapBack) {
            Switch.switchToSlotGhost(AutoCity.mc.player.inventory.currentItem);
            this.swapBack = false;
        }
        if ((boolean)this.autoBreak.getValue() && this.mining != null && this.getPickSlot() != -1) {
            final float breakTime = AutoCity.mc.world.getBlockState(this.mining).getBlockHardness((World)AutoCity.mc.world, this.mining) * 20.0f * 2.0f;
            final double shrinkFactor = MathUtils.normalize((double)(System.currentTimeMillis() - this.startTime), 0.0, (double)breakTime);
            if (this.mining != null && shrinkFactor > 1.2 && !this.swapBack) {
                Switch.switchToSlotGhost(this.getPickSlot());
                this.swapBack = true;
            }
        }
        if (this.mining != null) {
            if (AutoCity.mc.world.getBlockState(this.mining).getBlock() instanceof BlockAir) {
                this.mining = null;
            }
            if ((boolean)this.holeCheck.getValue() && !HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)this.target))) {
                this.mining = null;
            }
        }
        if (this.cityMode.getValue() == Mode2.SWITCH) {
            if (this.mining == null && AutoCity.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)this.target)) && getCityBlock((EntityPlayer)this.target) != null) {
                this.mine(getCityBlock((EntityPlayer)this.target));
            }
        }
        else if (this.mining == null && HoleUtil.isHole(CombatUtil.getOtherPlayerPos((EntityPlayer)this.target)) && getCityBlock((EntityPlayer)this.target) != null) {
            this.mine(getCityBlock((EntityPlayer)this.target));
        }
    }

    private void mine(final BlockPos blockPos) {
        if (this.Abort.getValue()) {
            AutoCity.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.UP));
        }
        AutoCity.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
        if (!(boolean)this.NoSwing.getValue()) {
            AutoCity.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        AutoCity.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
        this.mining = blockPos;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mining = null;
        this.swapBack = false;
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
                    if (AutoCity.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN && canCityBlock(pos, direction)) {
                        positions.add(pos);
                    }
                }
            }
        }
        return positions;
    }

    public static BlockPos getCityBlock(final EntityPlayer player) {
        final List<BlockPos> posList = getSurroundBlocks(player);
        posList.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>)MathUtils::distanceTo));
        return posList.isEmpty() ? null : posList.get(0);
    }

    public static boolean canCityBlock(final BlockPos blockPos, final EnumFacing direction) {
        return AutoCity.mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR || (AutoCity.mc.world.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.AIR && AutoCity.mc.world.getBlockState(blockPos.offset(direction).up()).getBlock() == Blocks.AIR && (AutoCity.mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.OBSIDIAN || AutoCity.mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.BEDROCK));
    }

    int getPickSlot() {
        int pickSlot = -1;
        for (int i = 0; i < 9; ++i) {
            if (AutoCity.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                pickSlot = i;
                break;
            }
        }
        return pickSlot;
    }

    public AxisAlignedBB AxisAlignedBB(final BlockPos poslel) {
        return new AxisAlignedBB(poslel.getX() + 0.5, poslel.getY() + 0.5, poslel.getZ() + 0.5, poslel.getX() + 0.5, poslel.getY() + 0.5, poslel.getZ() + 0.5);
    }
    public enum Mode
    {
        SMART,
        ENABLE

    }
    public enum Mode2
    {
        SWITCH,
        ALWAYS
    }
}
