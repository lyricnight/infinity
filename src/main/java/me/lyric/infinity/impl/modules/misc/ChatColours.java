package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Arrays;

/**
 * @author lyric
 */
@ModuleInformation(name = "ChatColours", description = "only for 2b2tpvp", category = Category.Misc)
public class ChatColours extends Module {

    public ModeSetting colour = createSetting("Colour", "None", Arrays.asList("None", "Black", "DarkGray", "Gray", "DarkBlue", "Blue", "DarkGreen", "Green", "DarkAqua", "Aqua", "DarkRed", "Red", "DarkPurple", "Purple", "Gold", "Yellow"));
    public ModeSetting modifier = createSetting("Modifier", "None", Arrays.asList("None", "Bold", "Italic", "BoldItalic", "LineThrough", "Underline"));

    public BooleanSetting space = createSetting("Space", true);

    private final String[] disallowed = new String[] {".", "/", ",", "-", "!"};
    @EventListener
    public void onPacketSend(PacketEvent.Send event)
    {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketChatMessage && isAllowed(((CPacketChatMessage) event.getPacket()).getMessage()))
        {
            CPacketChatMessage message = (CPacketChatMessage) event.getPacket();
            String the = message.getMessage();
            the = getCodeFromSetting(colour.getValue()) + getModifierFromSetting(modifier.getValue()) + (space.getValue() ? " " : "") + the;
            message.message = the;
        }
    }

    /**
     * @apiNote this method checks if the message contains a banned prefix.
     * @param message - the message input
     * @return - is the message valid to be modified?
     */

    public boolean isAllowed(String message) {
        boolean allow = true;
        for (String s : disallowed) {
            if (message.startsWith(s)) {
                allow = false;
                break;
            }
        }
        return allow;
    }

    /**
     * These methods get the codes to put into chat.
     * @param value - settings
     * @return - the code.
     */

    public String getCodeFromSetting(String value)
    {
        switch (value)
        {
            case "None":
                return "";
            case "Black":
                return "&0";
            case "DarkGray":
                return "&8";
            case "Gray":
                return "&7";
            case "DarkBlue":
                return "&1";
            case "Blue":
                return "&9";
            case "DarkGreen":
                return "&2";
            case "Green":
                return "&a";
            case "DarkAqua":
                return "&3";
            case "Aqua":
                return "&b";
            case "DarkRed":
                return "&4";
            case "Red":
                return "&c";
            case "DarkPurple":
                return "&5";
            case "Purple":
                return "&d";
            case "Gold":
                return "&6";
            case "Yellow":
                return "&e";
        }
        Infinity.LOGGER.error("ChatColours couldn't return a proper colour! String value passed to method: " + value);
        return "";
    }

    public String getModifierFromSetting(String value)
    {
        switch (value)
        {
            case "None":
                return "";
            case "Bold":
                return "&l";
            case "Italic":
                return "&o";
            case "BoldItalic":
                return "&l&o";
            case "LineThrough":
                return "&m";
            case "Underline":
                return "&n";
        }
        Infinity.LOGGER.error("ChatColours couldn't return a proper modifier! String value passed to the method: " + value);
        return "";
    }
}
