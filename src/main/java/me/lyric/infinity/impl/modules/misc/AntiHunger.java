package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;

/**
 * @author lyric ;)
 */

public class AntiHunger extends Module {

    public AntiHunger() {
        super("AntiHunger", "Reduces the hunger effect.", Category.MISC);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();
            if (packet.getAction() == CPacketEntityAction.Action.START_SPRINTING || packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                event.setCancelled(true);
            }
        }
    }
}
