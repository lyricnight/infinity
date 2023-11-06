package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.rotation.RotationType;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.minecraft.switcher.SwitchType;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;


/**
 * @author lyric (o_o)
 */
//TODO: make this activated on a keybind so that you don't double trap yourself everytime somebody traps you if they dont have cev
public class AntiCev extends Module {
    public AntiCev() {
        super("AntiCev", "Prevents cevbreaker.", Category.COMBAT);
    }
    public Setting<SwitchType> switchMode = register(new Setting<>("SwitchMode", "Mode for switch.", SwitchType.SILENT));
    public Setting<Boolean> rot = register(new Setting<>("Rotate", "Rotations.", false));
    public Setting<RotationType> type = register(new Setting<>("Rotation Type", "Type of rotation.", RotationType.PACKET).withParent(rot));
    public Setting<Boolean> jump = register(new Setting<>("JumpCheck", "doesnt place when in the air.", false));

    @Override
    public String getDisplayInfo() {
        if (CombatUtil.isBlockAbovePlayerHead() && CombatUtil.isAlreadyPrevented())
        {
            return ChatFormatting.GREEN + "prevented" + ChatFormatting.RESET;
        }
        return ChatFormatting.RED + "invalid" + ChatFormatting.RESET;

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
            Switch.doSwitch(blockSlot == -1 ? chestSlot : blockSlot, switchMode.getValue());
            PlacementManager.placeBlock(CombatUtil.getAntiCevPlacement(), rot.getValue(), type.getValue());
            Switch.doSwitch(oldSlot, switchMode.getValue());
        }
    }
}
