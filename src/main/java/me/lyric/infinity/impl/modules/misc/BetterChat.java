package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.setting.settings.StringSetting;
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
    public final BooleanSetting timeStamps = createSetting("Timestamps", true);
    public final BooleanSetting rect = createSetting("NoRect", true);
    public ModeSetting bracketColor = createSetting("BracketColor", ChatFormat.DARK_PURPLE, ChatFormat.getAll());
    public ModeSetting commandColor = createSetting("NameColor", ChatFormat.LIGHT_PURPLE, ChatFormat.getAll());

    public BooleanSetting inf = createSetting("Infinite", false);

    public BooleanSetting append = createSetting("Append", false);

    public StringSetting str = createSetting("Append-String", "infinity");

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
        if (event.getPacket() instanceof CPacketChatMessage && !((CPacketChatMessage)(event.getPacket())).getMessage().startsWith("/") && append.getValue())
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

