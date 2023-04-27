package me.lyric.infinity.impl.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import io.netty.util.internal.ConcurrentSet;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UnicodeResolver extends Module {
    private static UnicodeResolver INSTANCE = new UnicodeResolver();
    public Setting<Boolean> ignore = this.register(new Setting<Boolean>("Ignore","Prevents rendering of unicode.", true));
    public Setting<Boolean> lag = this.register(new Setting<Boolean>("Botnet","Sends unicode yourself.", false));
    public Setting<Integer> pop_delay = this.register(new Setting<Integer>("Delay","Delay for unicode spam", 2500, 0, 10000));
    private final Timer timer = new Timer();
    protected final Set<String> sent = new ConcurrentSet<>();
    public List<String> unicodeChars = new ArrayList<>();
    public UnicodeResolver()
    {
        super("UnicodeResolver", "Blocks unicode and sends on pop." , Category.MISC);
    }

    public String getUnicodeMessage() {
        StringBuffer sb = new StringBuffer();
        for (String u : unicodeChars) {
            sb.append(u);
        }
        return sb.toString();
    }

    public boolean hasUnicode(String message) {
        for (String u : unicodeChars) {
            if (message.contains(u)) {
                return true;
            }
        }
        return false;
    }

    public void onLoad() {
        unicodeChars.add("\u0101");
        unicodeChars.add("\u0201");
        unicodeChars.add("\u0301");
        unicodeChars.add("\u0401");
        unicodeChars.add("\u0601");
        unicodeChars.add("\u0701");
        unicodeChars.add("\u0801");
        unicodeChars.add("\u0901");
        unicodeChars.add("\u0A01");
        unicodeChars.add("\u0B01");
        unicodeChars.add("\u0E01");
        unicodeChars.add("\u0F01");
        unicodeChars.add("\u1001");
        unicodeChars.add("\u1101");
        unicodeChars.add("\u1201");
        unicodeChars.add("\u1301");
        unicodeChars.add("\u1401");
        unicodeChars.add("\u1501");
        unicodeChars.add("\u1601");
        unicodeChars.add("\u1701");
        unicodeChars.add("\u1801");
        unicodeChars.add("\u1901");
        unicodeChars.add("\u1A01");
        unicodeChars.add("\u1B01");
        unicodeChars.add("\u1C01");
        unicodeChars.add("\u1D01");
        unicodeChars.add("\u1E01");
        unicodeChars.add("\u1F01");
        unicodeChars.add("\u2101");
        unicodeChars.add("\u2201");
        unicodeChars.add("\u2301");
        unicodeChars.add("\u2401");
        unicodeChars.add("\u2501");
        unicodeChars.add("\u2701");
        unicodeChars.add("\u2801");
        unicodeChars.add("\u2901");
        unicodeChars.add("\u2A01");
        unicodeChars.add("\u2B01");
        unicodeChars.add("\u2C01");
        unicodeChars.add("\u2D01");
        unicodeChars.add("\u2E01");
        unicodeChars.add("\u2F01");
        unicodeChars.add("\u3001");
        unicodeChars.add("\u3101");
        unicodeChars.add("\u3201");
        unicodeChars.add("\u3301");
        unicodeChars.add("\u3401");
        unicodeChars.add("\u3501");
        unicodeChars.add("\u3601");
        unicodeChars.add("\u3701");
        unicodeChars.add("\u3801");
        unicodeChars.add("\u3901");
        unicodeChars.add("\u3A01");
        unicodeChars.add("\u3B01");
        unicodeChars.add("\u3C01");
        unicodeChars.add("\u3D01");
        unicodeChars.add("\u3E01");
        unicodeChars.add("\u3F01");
        unicodeChars.add("\u4001");
        unicodeChars.add("\u4101");
        unicodeChars.add("\u4201");
        unicodeChars.add("\u4301");
        unicodeChars.add("\u4401");
        unicodeChars.add("\u4501");
        unicodeChars.add("\u4601");
        unicodeChars.add("\u4701");
        unicodeChars.add("\u4801");
        unicodeChars.add("\u4901");
        unicodeChars.add("\u4A01");
        unicodeChars.add("\u4B01");
        unicodeChars.add("\u4C01");
        unicodeChars.add("\u4D01");
        unicodeChars.add("\u4E01");
        unicodeChars.add("\u4F01");
        unicodeChars.add("\u5001");
        unicodeChars.add("\u5101");
        unicodeChars.add("\u5201");
        unicodeChars.add("\u5301");
        unicodeChars.add("\u5401");
        unicodeChars.add("\u5501");
        unicodeChars.add("\u5601");
        unicodeChars.add("\u5701");
        unicodeChars.add("\u5801");
        unicodeChars.add("\u5901");
        unicodeChars.add("\u5A01");
        unicodeChars.add("\u5B01");
        unicodeChars.add("\u5C01");
        unicodeChars.add("\u5D01");
        unicodeChars.add("\u5E01");
        unicodeChars.add("\u5F01");
        unicodeChars.add("\u6001");
        unicodeChars.add("\u6101");
        unicodeChars.add("\u6201");
        unicodeChars.add("\u6301");
        unicodeChars.add("\u6401");
        unicodeChars.add("\u6501");
        unicodeChars.add("\u6601");
        unicodeChars.add("\u6701");
        unicodeChars.add("\u6801");
        unicodeChars.add("\u6901");
        unicodeChars.add("\u6A01");
        unicodeChars.add("\u6B01");
        unicodeChars.add("\u6C01");
        unicodeChars.add("\u6D01");
        unicodeChars.add("\u6E01");
        unicodeChars.add("\u6F01");
        unicodeChars.add("\u7001");
        unicodeChars.add("\u7101");
        unicodeChars.add("\u7201");
        unicodeChars.add("\u7301");
        unicodeChars.add("\u7401");
        unicodeChars.add("\u7501");
        unicodeChars.add("\u7601");
        unicodeChars.add("\u7701");
        unicodeChars.add("\u7801");
        unicodeChars.add("\u7901");
        unicodeChars.add("\u7A01");
        unicodeChars.add("\u7B01");
        unicodeChars.add("\u7C01");
        unicodeChars.add("\u7D01");
        unicodeChars.add("\u7E01");
        unicodeChars.add("\u7F01");
        unicodeChars.add("\u8001");
        unicodeChars.add("\u8101");
        unicodeChars.add("\u8201");
        unicodeChars.add("\u8301");
        unicodeChars.add("\u8401");
        unicodeChars.add("\u8501");
        unicodeChars.add("\u8601");
        unicodeChars.add("\u8701");
        unicodeChars.add("\u8801");
        unicodeChars.add("\u8901");
        unicodeChars.add("\u8A01");
        unicodeChars.add("\u8B01");
        unicodeChars.add("\u8C01");
        unicodeChars.add("\u8D01");
        unicodeChars.add("\u8E01");
        unicodeChars.add("\u8F01");
        unicodeChars.add("\u9001");
        unicodeChars.add("\u9101");
        unicodeChars.add("\u9201");
        unicodeChars.add("\u9301");
        unicodeChars.add("\u9401");
        unicodeChars.add("\u9501");
        unicodeChars.add("\u9601");
        unicodeChars.add("\u9701");
        unicodeChars.add("\u9801");
        unicodeChars.add("\u9901");
        unicodeChars.add("\u9A01");
        unicodeChars.add("\u9B01");
        unicodeChars.add("\u9C01");
        unicodeChars.add("\u9D01");
        unicodeChars.add("\u9E01");
        unicodeChars.add("\u9F01");
        unicodeChars.add("\uA001");
        unicodeChars.add("\uA101");
        unicodeChars.add("\uA201");
        unicodeChars.add("\uA301");
        unicodeChars.add("\uA401");
        unicodeChars.add("\uA501");
        unicodeChars.add("\uA601");
        unicodeChars.add("\uA701");
        unicodeChars.add("\uA801");
        unicodeChars.add("\uA901");
        unicodeChars.add("\uAA01");
        unicodeChars.add("\uAB01");
        unicodeChars.add("\uAC01");
        unicodeChars.add("\uAD01");
        unicodeChars.add("\uAE01");
        unicodeChars.add("\uAF01");
        unicodeChars.add("\uB001");
        unicodeChars.add("\uB101");
        unicodeChars.add("\uB201");
        unicodeChars.add("\uB301");
        unicodeChars.add("\uB401");
        unicodeChars.add("\uB501");
        unicodeChars.add("\uB601");
        unicodeChars.add("\uB701");
        unicodeChars.add("\uB801");
        unicodeChars.add("\uB901");
        unicodeChars.add("\uBA01");
        unicodeChars.add("\uBB01");
        unicodeChars.add("\uBC01");
        unicodeChars.add("\uBD01");
    }


    public static UnicodeResolver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UnicodeResolver();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    @Override
    public void onDisable () { this.sent.clear(); }
    public void onLogout () { this.sent.clear(); }

    @EventListener
    public void onReceive(PacketEvent.Receive e) {
        String chat;
        if (e.getPacket() instanceof SPacketChat && this.ignore.getValue()) {
            chat = ((SPacketChat) e.getPacket()).chatComponent.getUnformattedText();
            if (this.hasUnicode(chat)) {
                e.setCancelled(true);
                ChatUtils.sendMessage(ChatFormatting.GRAY +"§l"+"Unicode detected, message removed!");
            }
        }
    }

    @Override
    public void onTotemPop(EntityPlayer player) {
        if (player.entityId != mc.player.entityId && !Infinity.INSTANCE.friendManager.isFriend(player) && !this.sent.contains(player.getName()) && this.timer.passedMs(this.pop_delay.getValue()) && this.lag.getValue() && this.isEnabled()) {
            mc.player.sendChatMessage("/msg " + player.getName() + " " + this.getUnicodeMessage());
            this.sent.add(player.getName());
            this.timer.reset();
        }
    }
}
