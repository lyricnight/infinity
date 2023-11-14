package me.lyric.infinity.impl.modules.combat;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.transformer.ICPacketUseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

/**
 * @author lyric
 */

@ModuleInformation(getName = "Criticals", getDescription = "sword moment", category = Category.Combat)
public class Criticals extends Module {

    public Setting<Mode> mode = register(new Setting<>("Mode", "Mode used for critical damage.", Mode.STRICT));
    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!(event.getPacket() instanceof CPacketUseEntity)) return;
        Entity entity = ((ICPacketUseEntity) event.getPacket()).getAttackedEntity();
        if (entity instanceof EntityEnderCrystal) return;
        if (mode.getValue() == Mode.NORMAL) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.player.isInWater() || mc.player.isInLava())) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.10000000149011612, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
        if (mode.getValue() == Mode.STRICT) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.player.isInWater() || mc.player.isInLava())) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.06260280169278, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0726027996066, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
        if (mode.getValue() == Mode.JOHN) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.player.isInWater() || mc.player.isInLava())) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.08260280169278, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0826027996066, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
        if (mode.getValue() == Mode.EXTRA) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.player.isInWater() || mc.player.isInLava())) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.06260280169278, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0726027996066, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0336027996066, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString().toLowerCase();
    }

    enum Mode {
        NORMAL,
        STRICT,
        JOHN,
        EXTRA
    }
}