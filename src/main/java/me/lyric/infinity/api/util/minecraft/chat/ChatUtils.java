package me.lyric.infinity.api.util.minecraft.chat;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils implements IGlobals {

    public static void ChatMessage(String text) {
        Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group().substring(1);
            matcher.appendReplacement(stringBuffer, replacement);
        }
        matcher.appendTail(stringBuffer);
        text = stringBuffer.toString();
    }

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

    public static void sendMessageWithID(String message, int id) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) {
            return;
        }
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatMessage(Infinity.INSTANCE.commandManager.getClientMessage() +" "+ message), id);
    }


    public static class ChatMessage
            extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return this.text;
        }

        public ITextComponent createCopy() {
            return null;
        }

        public ITextComponent shallowCopy() {
            return new ChatMessage(this.text);
        }
    }
}

