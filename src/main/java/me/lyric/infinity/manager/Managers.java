package me.lyric.infinity.manager;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.gl.SplashProgress;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.manager.client.*;
import me.lyric.infinity.manager.forge.ForgeEventManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author lyric
 * @apiNote used to de-clutter main class, and make things less terrible.
 */

public class Managers implements IGlobals {

    public static final ModuleManager MODULES = new ModuleManager();

    public static final ConfigManager CONFIG = new ConfigManager();

    public static final CommandManager COMMANDS = new CommandManager();

    public static final FontManager FONT = new FontManager();

    public static final FriendManager FRIENDS = new FriendManager();

    public static final HoleManager HOLES = new HoleManager();

    public static final PlacementManager PLACEMENTS = new PlacementManager();

    public static final RotationManager ROTATIONS = new RotationManager();

    public static final ThreadManager THREADS = new ThreadManager();

    public static final TPSManager TPS = new TPSManager();

    public static final ForgeEventManager FORGE = new ForgeEventManager();

    /**
     * @apiNote loads everything.
     */

    public static void init()
    {
        Infinity.LOGGER.info("Subscribing all managers.");
        subscribe(MODULES, CONFIG, COMMANDS, FONT, FRIENDS, HOLES, PLACEMENTS, ROTATIONS, THREADS, TPS, FORGE);
        Infinity.LOGGER.info("Subscribing done.");
        MODULES.init();
        COMMANDS.init();
        TPS.init();
        THREADS.init();
        FONT.init();
        Infinity.LOGGER.info("Initialising done.");
        SplashProgress.setProgress(2, "Loading Infinity's Configs...");
        ConfigManager.loadPlayer();
        CommandManager.setPrefix(ConfigManager.getPrefix());
        Infinity.LOGGER.info("ConfigManager has loaded config and prefix.");
    }


    /**
     * @apiNote unloads all managers.
     */
    public static void unload()
    {
        Infinity.LOGGER.info("Unloading Managers.");
        TPSManager.unload();
        ConfigManager.savePlayer();
        ConfigManager.save(ConfigManager.getActiveConfig());
        Infinity.LOGGER.info("Done.");
    }


    /**
     * @apiNote this subscribes all managers to Infinity's and Forge's eventbuses.
     * @param subscribers - the manager to subscribe
     */

    public static void subscribe(Object...subscribers)
    {
        for (Object subscriber : subscribers)
        {
            Infinity.eventBus.subscribe(subscriber);
            MinecraftForge.EVENT_BUS.register(subscriber);
        }
    }



}
