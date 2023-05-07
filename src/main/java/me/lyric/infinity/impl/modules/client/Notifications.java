package me.lyric.infinity.impl.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import java.util.HashMap;

public class Notifications extends Module {

    public Setting<Boolean> modules = register(new Setting<>("Modules", "Chat notifications when a module is enabled or disabled.", true));
    public Setting<Boolean> totem = register(new Setting<>("Totem Counter" , "Notifies you when a player pops a totem.",true ));

    public Notifications() {
        super("Notifications", "Handle various notifications.", Category.CLIENT);
    }
    public static HashMap<String, Integer> totemPops = new HashMap<>();
    @Override
    public void onEnable() {
        totemPops.clear();
    }


    @Override
    public void onTick()
    {
        if(mc.player == null || mc.world == null)
        {
            return;
        }
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == null || player.getHealth() > 0.0F) continue;
            this.onDeath(player);
        }
    }


    @EventListener
    public void onDeath(EntityPlayer player) {

        boolean isFriend = (Infinity.INSTANCE.friendManager.isFriend(player.getName()));
        if (totemPops.containsKey(player.getName()) && totem.getValue()) {
            int popCount = totemPops.get(player.getName());
            totemPops.remove(player.getName());
                ChatUtils.sendMessage((isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE) +"§l"+ player.getName() +ChatFormatting.RESET + ChatFormatting.BOLD+ " died after popping " +(isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE)+ChatFormatting.BOLD+ popCount +ChatFormatting.RESET+ChatFormatting.BOLD + (popCount == 1 ? " totem!" : " totems!"));
        }
    }
    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();

            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                this.onTotemPop(player);

            }
        }
    }


    public void onTotemPop(EntityPlayer player) {
        int popCount = 1;
        boolean isFriend = (Infinity.INSTANCE.friendManager.isFriend(player.getName()));

        if (mc.player == null || mc.player.equals(player) && !totem.getValue()) {
            return;
        }

        if (totemPops.containsKey(player.getName())) {
            popCount = totemPops.get(player.getName());
            totemPops.put(player.getName(), ++popCount);
        } else {
            totemPops.put(player.getName(), popCount);
        }
        ChatUtils.sendMessage((isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE) +"§l"+ player.getName() + ChatFormatting.RESET + " has popped " +(isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE) +"§l"+ popCount +ChatFormatting.RESET +ChatFormatting.BOLD + (popCount == 1 ? " totem!" : " totems!") );
    }
}



