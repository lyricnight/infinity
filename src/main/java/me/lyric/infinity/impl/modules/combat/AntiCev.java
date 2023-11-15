package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;

import java.util.Arrays;
import java.util.Objects;


/**
 * @author lyric (o_o)
 */
//TODO: make this activated on a keybind so that you don't double trap yourself everytime somebody traps you if they dont have cev
@ModuleInformation(getName = "AntiCev", getDescription = "Prevents cevbreaker.", category = Category.Combat)
public class AntiCev extends Module {
    public ModeSetting switchMode = createSetting("SwitchMode","Silent",  Arrays.asList("Silent", "SilentPacket", "Slot"));
    public BooleanSetting rot = createSetting("Rotate", false);

    public ModeSetting type = createSetting("Rotation Type","Packet", Arrays.asList("Packet", "Normal"), v -> rot.getValue());
    public BooleanSetting jump = createSetting("JumpCheck",true);

    public BooleanSetting cd = createSetting("Slot-Cooldown", false);


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
        if (!nullSafe())
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
            int slot = blockSlot == -1 ? chestSlot : blockSlot;
            Switch.doSwitch(slot, switchMode.getValue());
            PlacementManager.placeBlock(CombatUtil.getAntiCevPlacement(), rot.getValue(), type.getValue());
            if(Objects.equals(switchMode.getValue(), "Slot"))
            {
                if(cd.getValue())
                {
                    Switch.switchBackAlt(slot);
                }
                else
                {
                    Switch.doSwitch(slot, "Slot");
                }
            }
            else
            {
                Switch.doSwitch(oldSlot, switchMode.getValue());
            }
        }
    }
}
