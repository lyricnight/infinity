package me.lyric.infinity;

import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.LambdaHandler;
import me.lyric.infinity.gui.panelstudio.PanelStudioGUI;
import me.lyric.infinity.manager.client.*;
import me.lyric.infinity.manager.forge.ForgeEventManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = "infinity",
        version = "v4"
)

public class Infinity {

    public static final String PATH = "Infinity/";
    public static final String CONFIG_PATH = PATH + "configs/";
    public static final Logger LOGGER = LogManager.getLogger("Infinity");
    File directory = new File(Minecraft.getMinecraft().gameDir, "Infinity");
    public EventBus eventBus = new EventBus(LambdaHandler.class, Infinity.LOGGER::error, Infinity.LOGGER::info);
    public static PanelStudioGUI gui;
    public TPSManager tpsManager;
    public String version = "v4";
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
        ConfigManager.refresh();
        ConfigManager.reload();
        ConfigManager.process(ConfigManager.LOAD);
    }

    public static void shutdown() {
        TPSManager.unload();
        FriendManager.unload();
        ConfigManager.reload();
        ConfigManager.process(ConfigManager.SAVE);
    }

    @Mod.Instance
    public static Infinity INSTANCE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.whoosh("Infinity");
    }

    public void whoosh(final String whoosh) {
        LOGGER.info("Initialising Infinity.");
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
        this.configManager.init();
        LOGGER.info("ConfigManager initialised!");
        this.rotationManager = new RotationManager();
        this.rotationManager.init();
        LOGGER.info("RotationManager initialised!");
        this.tpsManager = new TPSManager();
        this.tpsManager.load();
        LOGGER.info("TPSManager initialised!");
        this.friendManager = new FriendManager();
        friendManager.setDirectory(new File(this.directory, "friends.json"));
        friendManager.init();
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
        gui = new PanelStudioGUI();
        LOGGER.info("GUI initialised!");
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