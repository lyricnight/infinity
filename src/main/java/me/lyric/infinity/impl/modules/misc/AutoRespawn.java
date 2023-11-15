package me.lyric.infinity.impl.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author who knows
 */

@ModuleInformation(getName = "AutoRespawn", getDescription = "useful", category = Category.Misc)
public class AutoRespawn extends Module {

    public BooleanSetting message = createSetting("Message", true);
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDisplayDeathScreen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            if (message.getValue()) {
                ChatUtils.sendOverwriteMessage(ChatFormatting.WHITE + "[" + ChatFormatting.BOLD + "AutoRespawn" + ChatFormatting.RESET + ChatFormatting.WHITE + "]" + ChatFormatting.DARK_RED + " You died at: " + ChatFormatting.RESET + ChatFormatting.GREEN + ChatFormatting.BOLD + "X: " + (int) mc.player.posX + " " + "Y: " + (int) mc.player.posY + " " + "Z: " + (int) mc.player.posZ + ChatFormatting.RESET);
            }
            if ((mc.player.getHealth() <= 0) || (mc.player.getHealth() > 0)
            ) {
                event.setCanceled(true);
                mc.player.respawnPlayer();
            }
        }
    }
}