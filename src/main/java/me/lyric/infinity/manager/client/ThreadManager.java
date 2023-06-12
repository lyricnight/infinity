package me.lyric.infinity.manager.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.impl.modules.client.Internals;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    public ExecutorService executorService;
    public int num = 2;

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
        executorService = Executors.newFixedThreadPool(num);
    }
    public void reload()
    {
        executorService.shutdownNow();
        executorService = Executors.newFixedThreadPool(num);
        ChatUtils.sendMessage("Thread pool reloaded.");
    }
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent e)
    {
        num = Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).threadCount.getValue();
    }

    public void run(Runnable command) {
        try {
            executorService.execute(command);
        } catch (Exception ignored){
        }
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
