package me.lyric.infinity.manager.client;

import event.bus.EventBus;
import event.bus.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class TotemPopManager implements IGlobals {
    private final HashMap<String, Integer> poppedUsers;

    public TotemPopManager() {
        poppedUsers = new HashMap<>();
    }
    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public void updatePopped(String user) {
        boolean postEvent = false;
        if(poppedUsers.get(user) != null) {
            poppedUsers.put(user, (poppedUsers.get(user)+1));
            postEvent = true;
        }
        if(poppedUsers.get(user) == null) {
            poppedUsers.put(user, 1);
            postEvent = true;
        }
    }

    public HashMap<String, Integer> getPoppedUsers() {
        return this.poppedUsers;
    }

    public boolean containsKey(String user) {
        return poppedUsers.containsKey(user);
    }

    public boolean hasPops(String user) {
        return poppedUsers.get(user) != null;
    }

    public int getPops(String user) {
        if(poppedUsers.get(user) != null) {
            return poppedUsers.get(user);
        }
        return 0;
    }

    public void clearUser(String user) {
        if(poppedUsers.get(user) != null) {
            poppedUsers.put(user, 0);
        }
    }

    public void clear() {
        poppedUsers.clear();
    }


    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if(mc.player == null || mc.world == null) {
            return;
        }
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player.getHealth() <= 0) {
                if(Infinity.INSTANCE.totemPopManager.containsKey(player.getName())) {
                    Infinity.INSTANCE.totemPopManager.clearUser(player.getName());
                }
            }
        }
    }
    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if(mc.world == null || mc.player == null) {
            return;
        }
        if(event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packetEntityStatus = (SPacketEntityStatus)event.getPacket();
            if(packetEntityStatus.getOpCode() == 35) {
                if(packetEntityStatus.getEntity(mc.world) != null && packetEntityStatus.getEntity(mc.world) instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)packetEntityStatus.getEntity(mc.world);
                    Infinity.INSTANCE.totemPopManager.updatePopped(player.getName());
                }
            }
        }
    }


}
