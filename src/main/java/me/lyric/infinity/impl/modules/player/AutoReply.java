package me.lyric.infinity.impl.modules.player;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;


/**
 * @author lyric
 */
@ModuleInformation(name = "AutoReply", description = "we REPLYING out here", category = Category.Player)
public class AutoReply extends Module {

    public BooleanSetting ignoreY = createSetting("IgnoreY", true);

    @EventListener(priority = ListenerPriority.LOW)
    public void onReceivePacket(PacketEvent.Receive e)  {
        if (!nullSafe()) {
            return;
        }
        DecimalFormat format = new DecimalFormat("#.#");
        if (e.getPacket() instanceof SPacketChat) {
            SPacketChat p = (SPacketChat) e.getPacket();
            String msg = p.getChatComponent().getUnformattedText();
            if (msg.contains("says: ") || msg.contains("whispers: ")) {
                String ign = msg.split(" ")[0];
                if (mc.player.getName().equals(ign)) {
                    return;
                }
                if (Infinity.INSTANCE.friendManager.isFriend((ign))) {
                    String lowerCaseMsg = msg.toLowerCase();
                    if (lowerCaseMsg.contains("cord") || lowerCaseMsg.contains("coord") || lowerCaseMsg.contains("coords") || lowerCaseMsg.contains("cords") || lowerCaseMsg.contains("wya") || lowerCaseMsg.contains("where are you") || lowerCaseMsg.contains("where r u") || lowerCaseMsg.contains("where ru")) {
                        if (lowerCaseMsg.contains("discord") || lowerCaseMsg.contains("record")) {
                            return;
                        }
                        Vec3d pos = mc.player.getPositionVector();
                        mc.player.sendChatMessage("/msg " + ign + (" " + format.format(pos.x) + "x " + (ignoreY.getValue() ? "" : format.format(pos.y) + "y ") + format.format(pos.z) + "z"));
                    }
                }
            }
        }
    }
}