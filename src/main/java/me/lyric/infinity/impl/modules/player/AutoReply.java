package me.lyric.infinity.impl.modules.player;

import event.bus.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;


// @author lyric
public class AutoReply extends Module {
    private static AutoReply INSTANCE = new AutoReply();
    public static AutoReply getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoReply();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }
    public AutoReply()
    {
        super("AutoReply","Automatically replies your coords to people you have added.", Category.PLAYER);
    }

    Setting<Boolean> ignoreY = register(new Setting("IgnoreY","Doesn't send your Y coordinate.", true));

    @EventListener
    public void onReceivePacket(PacketEvent.Receive e)  {
        if (!nullSafe() || isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketChat) {
            SPacketChat p = (SPacketChat) e.getPacket();
            String msg = p.getChatComponent().getUnformattedText();
            if (msg.contains("says: ") || msg.contains("whispers: ")) {
                String ign = msg.split(" ")[0];
                if (mc.player.getName() == ign) {
                    return;
                }
                if (Infinity.INSTANCE.friendManager.isFriend((ign))) {
                    String lowerCaseMsg = msg.toLowerCase();
                    if (lowerCaseMsg.contains("cord") || lowerCaseMsg.contains("coord") || lowerCaseMsg.contains("coords") || lowerCaseMsg.contains("cords") || lowerCaseMsg.contains("wya") || lowerCaseMsg.contains("where are you") || lowerCaseMsg.contains("where r u") || lowerCaseMsg.contains("where ru")) {
                        if (lowerCaseMsg.contains("discord") || lowerCaseMsg.contains("record")) {
                            return;
                        }
                        int x = (int) mc.player.posX;
                        int y = (int) mc.player.posY;
                        int z = (int) mc.player.posZ;
                        mc.player.sendChatMessage("/msg " + ign + (" " + x + "x " + (ignoreY.getValue() ? "" : y + "y ") + z + "z"));
                    }
                }
            }
        }
    }
}