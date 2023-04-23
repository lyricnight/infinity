package me.lyric.infinity.impl.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.mixins.accessors.ISPacketChat;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BetterChat
        extends Module {
    public final Setting<Boolean> timeStamps = this.register(new Setting<Boolean>("Timestamps","Does what it says on the tin lad",  true));
    public final Setting<Boolean> giantBeetleSoundsLikeJackhammer = this.register(new Setting<Boolean>("NoRect","Removes rectangle", true));
    private static BetterChat INSTANCE = new BetterChat();

    public BetterChat() {
        super("BetterChat", "Improves Minecraft's chat", Category.MISC);
        INSTANCE = this;
    }

    public static BetterChat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BetterChat();
        }
        return INSTANCE;
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            if (this.timeStamps.getValue()) {
                ((ISPacketChat)packet).setChatComponent((ITextComponent) new TextComponentString(getTimeString() + packet.getChatComponent().getFormattedText()));
            }
        }
    }

    public String getTimeString() {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        final String bracket = "<";
        final String bracket2 = ">";
        return ChatFormatting.DARK_PURPLE + bracket +  ChatFormatting.LIGHT_PURPLE + date + ChatFormatting.DARK_PURPLE + bracket2 + " " + ChatFormatting.RESET;
    }
}