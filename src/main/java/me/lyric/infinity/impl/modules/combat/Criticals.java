package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

public class Criticals extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "Mode used for critical damage.", Mode.PACKET));

    public Criticals() {
        super("Criticals", "Allows you to always hit critical damage.", Category.COMBAT);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) {
                    switch (mode.getValue()) {
                        case JUMP: {
                            mc.player.jump();
                            break;
                        }
                        case PACKET: {
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                            break;
                        }
                    }
                }
            }
        }
    }

    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + mode.getValue().toString().toLowerCase() +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

    enum Mode {
        PACKET,
        JUMP
    }
}