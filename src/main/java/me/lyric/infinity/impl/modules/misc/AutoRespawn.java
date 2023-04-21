package me.lyric.infinity.impl.modules.misc;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Paupro (Thanks zzurio for help)
 */

public class AutoRespawn extends Module {

    //public Setting<Integer> delay = register(new Setting<>("Delay", "The time taken to respawn after you die."));
    public Setting<Boolean> message = register(new Setting<>("Message", "Sends your death coordinates in chat after you die.", true));

    public AutoRespawn() {
        super("AutoRespawn", "Respawns automatically when you die.", Category.MISC);
    }

    @SubscribeEvent
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