package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import net.minecraft.block.BlockObsidian;


/**
 * @author lyric (o_o)
 */

public class AntiCev extends Module {
    public AntiCev() {
        super("AntiCev", "Prevents cevbreaker.", Category.COMBAT);
    }
    public Setting<Boolean> rot = register(new Setting<>("Rotate", "Rotations.", false));
    public Setting<Boolean> jump = register(new Setting<>("JumpCheck", "doesnt place when in the air.", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet Rotations", "Uses packet rotations.", true).withParent(rot));


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
            Switch.placeBlockWithSwitch(InventoryUtil.findHotbarBlock(BlockObsidian.class), rot.getValue(), packet.getValue(), CombatUtil.getAntiCevPlacement(), true);
        }
    }
}
