package me.lyric.infinity.impl.modules.combat;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

/**
 * @author lyric
 */
//TODO: THIS IS UTTER TRASH
    //TODO: Add displayinfo

public class Blocker extends Module {

    public Setting<Mode> switchMode = register(new Setting<>("Mode", "Mode for switch", Mode.SILENT));

    private final Setting<Boolean> extend = register(new Setting<>("Extend","bot",  true));
    private final Setting<Boolean> face = register(new Setting<>("Face","bot", true));
    private final Setting<Boolean> diag = register(new Setting<>("Diagonals","bot", true));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate","bot", false));
    private final Setting<Boolean> packet = register(new Setting<>("Packet","bot", true).withParent(rotate));

    public Blocker() {
        super("Blocker", "bot", Category.COMBAT);
    }
    BlockPos placePos = null;
    BlockPos placePos2 = null;
    BlockPos placePos3 = null;
    boolean switched = false;
    @Override
    public void onDisable()
    {
        placePos = null;
        placePos2 = null;
        placePos3 = null;
        switched = false;
    }
    @EventListener(priority = ListenerPriority.HIGH)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockBreakAnim && HoleUtil.isHole(RotationManager.getPlayerPos())) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
            BlockPos pos = packet.getPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;
            BlockPos playerPos = RotationManager.getPlayerPos();;
            if (diag.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos3 = (playerPos.north().west());

                if (pos.equals(playerPos.west()))
                    placePos3 = (playerPos.north().east());

                if (pos.equals(playerPos.east()))
                    placePos3 = (playerPos.south().east());

                if (pos.equals(playerPos.south()))
                    placePos3 = (playerPos.south().west());
            }

            if (extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }

            if (face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos2 = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos2 = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos2 = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos2 = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                placeBlock(placePos);
            }
            if (placePos2 != null)
            {
                placeBlock(placePos2);
            }
            if (placePos3 != null)
            {
                placeBlock(placePos3);
            }
        }
        else if (event.getPacket() instanceof SPacketBlockChange && HoleUtil.isHole(RotationManager.getPlayerPos())) {
            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            BlockPos pos = packet.getBlockPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = RotationManager.getPlayerPos();
            if (diag.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos3 = (playerPos.north().west());

                if (pos.equals(playerPos.west()))
                    placePos3 = (playerPos.north().east());

                if (pos.equals(playerPos.east()))
                    placePos3 = (playerPos.south().east());

                if (pos.equals(playerPos.south()))
                    placePos3 = (playerPos.south().west());
            }

            if (extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }

            if (face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos2 = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos2 = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos2 = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos2 = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                placeBlock(placePos);
            }
            if (placePos2 != null)
            {
                placeBlock(placePos2);
            }
            if (placePos3 != null)
            {
                placeBlock(placePos3);
            }
        }

    }
    @Override
    public void onUpdate()
    {
        InventoryUtil.check(this);
    }

    private void placeBlock(BlockPos pos){
        int old = mc.player.inventory.currentItem;
        if (!switched)
        {
            doSwitch(InventoryUtil.findHotbarBlock(BlockObsidian.class));
            switched = true;
        }
        PlacementManager.placeBlock(pos, rotate.getValue(), packet.getValue(), true, false, true);
        if (placePos == null && placePos2 == null && placePos3 == null && (switched && switchMode.getValue() == Mode.SILENT))
        {
            doSwitch(old);
        }
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
        SILENT
    }
}