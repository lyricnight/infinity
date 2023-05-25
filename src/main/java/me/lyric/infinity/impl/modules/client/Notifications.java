package me.lyric.infinity.impl.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.Potion;

import java.util.*;

public class Notifications extends Module {

    public Setting<Boolean> modules = register(new Setting<>("Modules", "Chat notifications when a module is enabled or disabled.", true));
    public Setting<Boolean> totem = register(new Setting<>("Totem Counter" , "Notifies you when a player pops a totem.",true ));
    public Setting<Boolean> potions = register(new Setting<>("Potion Detector", "Detects players potion effects.", true));

    public Setting<Integer> checkrate = register(new Setting<>("Checkrate", "How often to check potions.", 500,  0, 5000));

    public Notifications() {
        super("Notifications", "Handle various notifications.", Category.CLIENT);
    }
    public static HashMap<String, Integer> totemPops = new HashMap<>();
    private final Set<EntityPlayer> str = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<EntityPlayer> spd = Collections.newSetFromMap(new WeakHashMap<>());
    private boolean last;

    Timer weak = new Timer();
    Timer strgh = new Timer();
    Timer speed = new Timer();

    @Override
    public void onEnable() {
        totemPops.clear();
    }


    @Override
    public void onUpdate()
    {
        if(mc.player == null || mc.world == null)
        {
            return;
        }
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == null || player.getHealth() > 0.0F) continue;
            this.onDeath(player);
        }
        if (potions.getValue()) {
            if (!this.weak.passedMs(this.checkrate.getValue())) {
                return;
            }
            if (Notifications.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation((String)"weakness"))) && !this.last) {
                ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " have been" + ChatFormatting.DARK_GRAY + " weaknessed" + ChatFormatting.BOLD + "!");
                this.last = true;
            }
            if (!Notifications.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation((String)"weakness"))) && this.last) {

                ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " no longer have" + ChatFormatting.DARK_GRAY + "weakness" + ChatFormatting.BOLD + "!");

                this.last = false;
            }
            this.weak.reset();
        }
        if (this.potions.getValue()) {
            for (EntityPlayer entityPlayer : Notifications.mc.world.playerEntities) {
                if (!this.strgh.passedMs(this.checkrate.getValue())) {
                    return;
                }
                if (entityPlayer.equals((Object)Notifications.mc.player))
                {
                    if (entityPlayer.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(entityPlayer)) {
                        ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " have"  + ChatFormatting.RED + " strength" + ChatFormatting.BOLD + "!");
                        this.str.add(entityPlayer);
                    }
                    if (!this.str.contains(entityPlayer) || entityPlayer.isPotionActive(MobEffects.STRENGTH)) continue;
                    ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " no longer have" + ChatFormatting.RED + " strength"+ ChatFormatting.BOLD + "!");
                    this.str.remove(entityPlayer);
                }
                else
                {
                    if (entityPlayer.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(entityPlayer)) {
                        ChatUtils.sendMessage(ChatFormatting.BOLD + entityPlayer.getDisplayNameString() + ChatFormatting.WHITE + " has" + ChatFormatting.RED + " strength" + ChatFormatting.BOLD + "!");
                        this.str.add(entityPlayer);
                    }
                    if (!this.str.contains(entityPlayer) || entityPlayer.isPotionActive(MobEffects.STRENGTH)) continue;
                    ChatUtils.sendMessage(ChatFormatting.BOLD + entityPlayer.getDisplayNameString() + ChatFormatting.WHITE + " no longer has" + ChatFormatting.RED + " strength"+ ChatFormatting.BOLD + "!");
                    this.str.remove(entityPlayer);
                }

            }
            this.strgh.reset();
        }
        if (potions.getValue())
        {
            for (EntityPlayer entityPlayer : Notifications.mc.world.playerEntities) {
                if (!this.speed.passedMs(this.checkrate.getValue())) {
                    return;
                }
                if (entityPlayer.equals((Object)Notifications.mc.player))
                {
                    if (entityPlayer.isPotionActive(MobEffects.SPEED) && !this.spd.contains(entityPlayer)) {
                        ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " have" + ChatFormatting.AQUA + " speed" + ChatFormatting.BOLD + "!");
                        this.spd.add(entityPlayer);
                    }
                    if (!this.spd.contains(entityPlayer) || entityPlayer.isPotionActive(MobEffects.SPEED)) continue;
                    ChatUtils.sendMessage(ChatFormatting.BOLD + "You" + ChatFormatting.WHITE + " no longer have" + ChatFormatting.AQUA + " speed" + ChatFormatting.BOLD + "!");
                    this.spd.remove(entityPlayer);
                }
                else
                {
                    if (entityPlayer.isPotionActive(MobEffects.SPEED) && !this.spd.contains(entityPlayer)) {
                        ChatUtils.sendMessage(ChatFormatting.BOLD + entityPlayer.getDisplayNameString() + ChatFormatting.WHITE + " has" + ChatFormatting.AQUA + " speed" + ChatFormatting.BOLD + "!");
                        this.spd.add(entityPlayer);
                    }
                    if (!this.spd.contains(entityPlayer) || entityPlayer.isPotionActive(MobEffects.SPEED)) continue;
                    ChatUtils.sendMessage(ChatFormatting.BOLD + entityPlayer.getDisplayNameString() + ChatFormatting.WHITE + " no longer has" + ChatFormatting.AQUA + " speed" + ChatFormatting.BOLD + "!");
                    this.spd.remove(entityPlayer);
                }

            }
            this.speed.reset();
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

        if (totemPops.containsKey(player.getName())){
            popCount = totemPops.get(player.getName());
            totemPops.put(player.getName(), ++popCount);
        } else {
            totemPops.put(player.getName(), popCount);
        }
        ChatUtils.sendMessage((isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE) +"§l"+ player.getName() + ChatFormatting.RESET + " has popped " +(isFriend ? ChatFormatting.AQUA : ChatFormatting.WHITE) +"§l"+ popCount +ChatFormatting.RESET +ChatFormatting.BOLD + (popCount == 1 ? " totem!" : " totems!") );
    }



}



