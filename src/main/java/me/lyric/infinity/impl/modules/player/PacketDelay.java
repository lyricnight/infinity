package me.lyric.infinity.impl.modules.player;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.manager.client.ThreadManager;
import me.lyric.infinity.mixin.transformer.INetworkManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 3arth, lyric
 */

public class PacketDelay extends Module {

    public Setting<Integer> delay = register(new Setting<>("Delay", "Delay in ms.", 1, 1, 5000));

    Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    ScheduledExecutorService service;
    public PacketDelay()
    {
        super("PacketDelay", "Applies a delay to all packets you send.", Category.PLAYER);
        service = ThreadManager.newDaemonScheduledExecutor("Infinity PacketDelay");
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent ignored)
    {
        packets.clear();
        service.shutdown();
    }
    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!mc.isSingleplayer() && event.getPacket() instanceof CPacketKeepAlive) {
            onPacket(event.getPacket());
            event.setCancelled(true);
        }
    }
    @Override
    public void onDisable()
    {
        if (!packets.isEmpty())
        {
            packets.forEach(packet ->
            {
                if(packet != null)
                {
                    mc.player.connection.sendPacket(packet);
                }
            });
            packets.clear();
        }
    }
    protected void onPacket(Packet<?> packet) {
        packets.add(packet);
        service.schedule(() -> {
            if (mc.player != null) {
                Packet<?> p = packets.poll();
                if (p != null) {
                    NetHandlerPlayClient con = mc.getConnection();
                    if (con != null)
                    {
                        INetworkManager manager = (INetworkManager) con.getNetworkManager();
                        manager.sendPacketNoEvent(p);
                    }
                }
            }
        }, delay.getValue(), TimeUnit.MILLISECONDS);
    }
}
