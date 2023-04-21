package me.lyric.infinity.api.util.minecraft.chat;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;


public class ChatUtils implements IGlobals {

    public static final String INFINITY = ChatFormatting.AQUA + "[" + ChatFormatting.BLUE + "Infinity" + ChatFormatting.AQUA + "]" + ChatFormatting.RESET;

    public static final String SPACE = " ";

    public static void sendMessage(String message) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) {
            return;
        }
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(INFINITY + SPACE + message));
    }

    public static void sendOverwriteMessage(String message) {
        if (mc.ingameGUI == null) return;
        String commandMessage = INFINITY + SPACE + message;
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(commandMessage), 69);
    }
}
