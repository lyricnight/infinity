package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.string.ChatFormat;
import me.lyric.infinity.mixin.mixins.accessors.ICPacketChat;
import me.lyric.infinity.mixin.mixins.accessors.ISPacketChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
    @author lyric
 */

@ModuleInformation(getName = "BetterChat", getDescription = "the chat is now better guys", category = Category.Misc)
public class BetterChat extends Module {
    public final Setting<Boolean> timeStamps = register(new Setting<>("Timestamps","Does what it says on the tin lad",  true));
    public final Setting<Boolean> rect = register(new Setting<>("NoRect","Removes rectangle", true));
    public Setting<ChatFormat.Color> bracketColor = register(new Setting<>("BracketColor","Colour of the brackets.", ChatFormat.Color.DARK_PURPLE));
    public Setting<ChatFormat.Color> commandColor = register(new Setting<>("NameColor","Colour of timestamps", ChatFormat.Color.LIGHT_PURPLE));

    public Setting<Boolean> inf = register(new Setting<>("Infinite", "Makes chat infinite.", false));

    public Setting<Boolean> append = register(new Setting<>("Append", "Add a suffix to your messages.", false));

    public Setting<String> str = register(new Setting<>("Append-String", "String to append.", "infinity"));

    @EventListener(priority = ListenerPriority.LOW)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            if (this.timeStamps.getValue()) {
                ((ISPacketChat)packet).setChatComponent(new TextComponentString(getTimeString() +" "+  packet.getChatComponent().getFormattedText()));
            }
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event)
    {
        if (event.getPacket() instanceof CPacketChatMessage && !((CPacketChatMessage)(event.getPacket())).getMessage().startsWith("/"))
        {
            CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
            ((ICPacketChat)packet).setMessage(packet.getMessage() + " | " +  str.getValue());
        }
    }


    public String getTimeString() {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        final String bracket = "<";
        final String bracket2 = ">";
        return ChatFormat.coloredString(bracket, this.bracketColor.getValue()) + ChatFormat.coloredString(date, this.commandColor.getValue()) + ChatFormat.coloredString(bracket2, this.bracketColor.getValue());
    }

}

