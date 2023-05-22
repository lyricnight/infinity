package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.CombatUtil;
import me.lyric.infinity.api.util.minecraft.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import static net.minecraft.util.EnumHand.MAIN_HAND;

/**
 * @author lyric (o_o)
 */

public class AntiCev extends Module {
    public AntiCev() {
        super("AntiCev", "Prevents cevbreaker.", Category.COMBAT);
    }

    public Setting<Boolean> attack = register(new Setting<>("Attack","Attacks crystals to place.", true));
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
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            if (obbySlot == -1)
            {
                ChatUtils.sendMessage(ChatFormatting.BOLD + "No Obsidian! Disabling AntiCev...");
                toggle();
                return;
            }
            int originalSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = obbySlot;
            mc.playerController.updateController();
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(CombatUtil.getAntiCevPlacement()))) {
                if (entity instanceof EntityEnderCrystal) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            }
            PlacementManager.placeBlock(CombatUtil.getAntiCevPlacement(), rot.getValue(), packet.getValue(), attack.getValue(), false);
            if (mc.player.inventory.currentItem != originalSlot) {
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            }
            mc.player.swingArm(MAIN_HAND);
            mc.player.inventory.currentItem = originalSlot;
        }
    }
}
