package me.lyric.infinity.manager.forge;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.event.render.Render3DEvent;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.impl.modules.render.HoleESP;
import me.lyric.infinity.manager.client.ModuleManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.Executors;

public class ForgeEventManager implements IGlobals {

    public static ForgeEventManager forgeEventManager;

    public ForgeEventManager() {
        forgeEventManager = this;

    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        Infinity.INSTANCE.eventBus.subscribe(this);
    }
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Render3DEvent render3DEvent = new Render3DEvent(event.getPartialTicks());
        Infinity.INSTANCE.eventBus.post(render3DEvent);

        for (Module module : ModuleManager.getModuleManager().getModules()) {
            if (module.isEnabled()) {
                module.onRender3D(event.getPartialTicks());
            }
        }
    }


    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onClientTick(TickEvent e) {
        for (Module module : ModuleManager.getModuleManager().getModules()) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Infinity.INSTANCE.moduleManager.onLogout();

    }
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.isCanceled()) {
            return;
        }

        RenderGameOverlayEvent.ElementType target = RenderGameOverlayEvent.ElementType.EXPERIENCE;

        if (!mc.player.isCreative() && mc.player.getRidingEntity() instanceof AbstractHorse) {
            target = RenderGameOverlayEvent.ElementType.HEALTHMOUNT;
        }

        if (event.getType() == target) {
            for (Module module : ModuleManager.getModuleManager().getModules()) {
                if (module.isEnabled()) {
                    module.onRender3D(event.getPartialTicks());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (mc.player != null && mc.world != null && event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals(mc.player)) {
            Infinity.INSTANCE.moduleManager.onUpdate();
        }
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        boolean isOn = Infinity.INSTANCE.moduleManager.getModuleByClass(HoleESP.class).isEnabled();
        if(isOn)
        {
            Infinity.INSTANCE.threadManager.setExecutorService(Executors.newFixedThreadPool(2));

        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeath(LivingDeathEvent event){
        if (event.getEntity().equals(mc.player)){
            boolean isOn = Infinity.INSTANCE.moduleManager.getModuleByClass(HoleESP.class).isEnabled();
            if(isOn)
            {
                Infinity.INSTANCE.threadManager.setExecutorService(Executors.newFixedThreadPool(2));

            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            for (Module modules : ModuleManager.getModuleManager().getModules()) {
                if (modules.getBind().getKey() == Keyboard.getEventKey()) {
                    modules.toggle();
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith(Infinity.INSTANCE.commandManager.getPrefix())) {
            event.setCanceled(true);

            String[] arguments = message.replaceFirst(Infinity.INSTANCE.commandManager.getPrefix(), "").split(" ");

            mc.ingameGUI.getChatGUI().addToSentMessages(message);

            boolean isCommand = false;

            for (Command commands : Infinity.INSTANCE.commandManager.getCommands()) {
                if (commands.getCommand().equals(arguments[0])) {
                    commands.onCommand(arguments);

                    isCommand = true;

                    break;
                }
            }
            if (!isCommand) {
                ChatUtils.sendMessage(ChatFormatting.RED + "Unknown command.");
            }
        }
    }
}