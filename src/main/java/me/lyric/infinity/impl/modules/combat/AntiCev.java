package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;


/**
 * @author lyric (o_o)
 */

public class AntiCev extends Module {
    public AntiCev() {
        super("AntiCev", "Prevents cevbreaker.", Category.COMBAT);
    }
    public Setting<Mode> switchMode = register(new Setting<>("SwitchMode", "Mode for switch.", Mode.SILENT));
    public Setting<Boolean> rot = register(new Setting<>("Rotate", "Rotations.", false));
    public Setting<Boolean> jump = register(new Setting<>("JumpCheck", "doesnt place when in the air.", false));


    @Override
    public String getDisplayInfo() {
        if (CombatUtil.isBlockAbovePlayerHead() && CombatUtil.isAlreadyPrevented())
        {
            return ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "prevented" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" + ChatFormatting.RED + "invalid" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";

    }

    @Override
    public void onUpdate()
    {
        InventoryUtil.check(this);
        if (mc.player == null)
        {
            return;
        }
        if(jump.getValue() && !mc.player.onGround)
        {
            return;
        }
        if (CombatUtil.isBlockAbovePlayerHead() && !CombatUtil.isAlreadyPrevented())
        {
            int oldSlot = mc.player.inventory.currentItem;
            int blockSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int chestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            doSwitch(blockSlot == -1 ? chestSlot : blockSlot);
            PlacementManager.placeBlock(CombatUtil.getAntiCevPlacement(), rot.getValue());
            doSwitch(oldSlot);
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
