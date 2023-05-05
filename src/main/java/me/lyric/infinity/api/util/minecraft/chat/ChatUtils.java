package me.lyric.infinity.api.util.minecraft.chat;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.text.TextComponentString;


public class ChatUtils implements IGlobals {

    public static void sendMessage(String message) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) {
            return;
        }
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(Infinity.INSTANCE.commandManager.getClientMessage() +" "+ message));
    }

    public static void sendOverwriteMessage(String message) {
        if (mc.ingameGUI == null) return;
        String commandMessage = message;
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Infinity.INSTANCE.commandManager.getClientMessage() +" "+ commandMessage), 69);
    }
}
