package me.lyric.infinity.manager.forge;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.event.render.Render3DEvent;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.impl.modules.client.Internals;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.manager.client.CommandManager;
import me.lyric.infinity.manager.client.ModuleManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

public class ForgeEventManager implements IGlobals {

    public long frameTime;
    public long previous;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Render3DEvent render3DEvent = new Render3DEvent(event.getPartialTicks());
        Infinity.eventBus.post(render3DEvent);
        frameTime = System.currentTimeMillis() - previous;
        previous = System.currentTimeMillis();

        for (Module module : ModuleManager.getModuleManager().getModules()) {
            if (module.isEnabled()) {
                module.onRender3D(event.getPartialTicks());
            }
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Managers.MODULES.onLogout();
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
            Managers.MODULES.onUpdate();
            if (Managers.MODULES.getModuleByClass(Internals.class).isDisabled())
            {
                Managers.MODULES.getModuleByClass(Internals.class).enable();
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        Managers.THREADS.threadDeath();
    }
    @SubscribeEvent()
    public void onTick(TickEvent.ClientTickEvent e)
    {
        Managers.MODULES.onTick();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeath(LivingDeathEvent event){
        if (event.getEntity().equals(mc.player)){
            Managers.THREADS.threadDeath();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if(mc.player == null || mc.world == null) return;
        Managers.MODULES.getModules().stream().filter(module -> Keyboard.getEventKeyState() && module.bind.getValue().equals(Keyboard.getEventKey())).forEach(module -> {
            if (module.isEnabled()) {
                module.disable();
            }
            else {
                module.enable();
            }
        });
    }
    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith(Managers.COMMANDS.getPrefix())) {
            event.setCanceled(true);

            String[] arguments = message.replaceFirst(Managers.COMMANDS.getPrefix(), "").split(" ");

            mc.ingameGUI.getChatGUI().addToSentMessages(message);

            boolean isCommand = false;

            for (Command commands : CommandManager.getCommands()) {
                if (commands.getCommand().equals(arguments[0])) {
                    commands.onCommand(arguments);

                    isCommand = true;

                    break;
                }
            }
            if (!isCommand) {
                ChatUtils.sendMessage(ChatFormatting.RED + "Unknown command. Try " + Managers.COMMANDS.getPrefix() + "commands for a list of available commands.");
            }
        }
    }
}