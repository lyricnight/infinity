package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Objects;

/**
 * @author lyric
 * 2bpvp beef
 */
public class ChatColours extends Module {
    public Setting<Color> colour = register(new Setting<>("Colour", "Which colour to use.", Color.Aqua));
    public Setting<Modifier> modifier = register(new Setting<>("Modifier", "Modifies how the message will be displayed.", Modifier.None));
    private String[] disallowed = new String[] {".", "/", ",", "-"};

    public ChatColours()
    {
        super("ChatColours", "Allows you to use ChatFormatting in chat. Doesn't work on most servers.", Category.MISC);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event)
    {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketChatMessage && isAllowed(((CPacketChatMessage) event.getPacket()).getMessage()))
        {
            CPacketChatMessage message = (CPacketChatMessage) event.getPacket();
            String the = message.getMessage();
            the = colour.getValue().getValue() + modifier.getValue().getValue() + " " + the;
            message.message = the;
        }
    }
    public boolean isAllowed(final String message) {
        boolean allow = true;
        for (final String s : disallowed) {
            if (message.startsWith(s)) {
                allow = false;
                break;
            }
        }
        return allow;
    }
    public enum Color {
        None('f',false),
        Black('0',false),
        BlackNormal(']',true),
        DarkGray('8',false),
        Gray('7',false),
        GrayNormal('[',true),
        DarkBlue('1',false),
        Blue('9',false),
        DarkGreen('2',false),
        Green('a',false),
        NormalGreen('>',true),
        DarkAqua('3',false),
        DarkAquaNormal(';',true),
        Aqua('b',false),
        AquaNormal(':',true),
        DarkRed('4',false),
        Red('c',false),
        RedNormal('<',true),
        DarkPurple('5',false),
        Purple('d',false),
        Gold('6',false),
        GoldNormal(',',true),
        Yellow('e',false),
        YellowNormal('!',true),
        Obfuscated('k',false);

        private final String value;

        Color(char c,boolean bypass) {
            if(bypass) value = String.valueOf(c);
            else value = "&" + c;
        }

        public String getValue() {
            return value;
        }
    }
    public enum Modifier {
        None(" "),
        Bold("l"),
        Italic("o"),
        BoldItalic("l&o"),
        StrikeThrough("m"),
        Underline("n");

        private final String value;

        Modifier(String c) {
            if(Objects.equals(c, " ")) value = "";
            else value = "&" + c;
        }

        public String getValue() {
            return value;
        }
    }



}
