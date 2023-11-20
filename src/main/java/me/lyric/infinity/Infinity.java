package me.lyric.infinity;

import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.LambdaHandler;
import me.lyric.infinity.api.util.gl.SplashProgress;
import me.lyric.infinity.api.util.string.ClientFont;
import me.lyric.infinity.api.util.string.Renderer;
import me.lyric.infinity.manager.client.*;
import me.lyric.infinity.manager.forge.ForgeEventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(
        modid = "infinity",
        version = "v5"
)

public class Infinity {
    public static final Logger LOGGER = LogManager.getLogger("Infinity");

    public ClientFont infinityFont;

    public EventBus eventBus = new EventBus(LambdaHandler.class, Infinity.LOGGER::error, Infinity.LOGGER::info);
    public TPSManager tpsManager;

    public String version = "v5";
    public ForgeEventManager forgeEventManager;

    public ModuleManager moduleManager;
    public ThreadManager threadManager;
    public PlacementManager placementManager;
    public HoleManager holeManager;
    public CommandManager commandManager;
    public FriendManager friendManager;
    public ConfigManager configManager;
    public RotationManager rotationManager;

    public static void startup() {
        ConfigManager.loadPlayer();
        CommandManager.setPrefix(ConfigManager.getPrefix());
        SplashProgress.setProgress(3, "Loading Infinity's Configs...");
    }

    public static void shutdown() {
        TPSManager.unload();
        ConfigManager.savePlayer();
        ConfigManager.save(ConfigManager.getActiveConfig());
    }

    @Mod.Instance
    public static Infinity INSTANCE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.whoosh("Infinity");
    }

    public void whoosh(final String whoosh) {
        SplashProgress.setProgress(1, "Initializing Minecraft");
        LOGGER.info("Initialising Infinity.");
        Display.setTitle("Infinity " + version);
        LOGGER.info("Attempted to set window title.");
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();
        LOGGER.info("ModuleManager initialised!");
        this.commandManager = new CommandManager();
        this.commandManager.init();
        LOGGER.info("CommandManager initialised!");
        this.forgeEventManager = new ForgeEventManager();
        this.forgeEventManager.init();
        LOGGER.info("ForgeEventManager initialised!");
        this.configManager = new ConfigManager();
        LOGGER.info("ConfigManager initialised!");
        this.rotationManager = new RotationManager();
        this.rotationManager.init();
        LOGGER.info("RotationManager initialised!");
        this.tpsManager = new TPSManager();
        this.tpsManager.load();
        LOGGER.info("TPSManager initialised!");
        this.friendManager = new FriendManager();
        LOGGER.info("FriendManager initialised!");
        this.threadManager = new ThreadManager();
        threadManager.init();
        LOGGER.info("ThreadManager initialised!");
        this.holeManager = new HoleManager();
        this.holeManager.init();
        LOGGER.info("HoleManager initialised!");
        this.placementManager = new PlacementManager();
        this.placementManager.init();
        LOGGER.info("PlacementManager initialised!");
        LOGGER.info("All Managers loaded successfully!");
        infinityFont = new ClientFont("Comfortaa-Regular", 17.0f);
        LOGGER.info("Infinity has set its font.");
        startup();
        LOGGER.info("Running config startup.");
        LOGGER.info("Infinity fully initialised!");

        Runtime.getRuntime().addShutdownHook(new Thread("Infinity ShutdownHook") {
            @Override
            public void run() {
                Infinity.shutdown();
                LOGGER.info("Infinity shutdown success!");
            }
        });
    }
}