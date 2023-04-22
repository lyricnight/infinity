package me.lyric.infinity;

import me.lyric.infinity.gui.panelstudio.PanelStudioGUI;
import me.lyric.infinity.manager.client.*;
import me.lyric.infinity.manager.forge.ForgeEventManager;
import event.bus.EventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = "infinity",
        version = "0.0.1"
)

public class Infinity {

    public static final String PATH = "Infinity/";
    public static final String CONFIG_PATH = PATH + "configs/";

    public static EventBus EVENT_BUS = EventBus.INSTANCE;
    public static PanelStudioGUI gui;
    public TPSManager tpsManager;

    public ForgeEventManager forgeEventManager;
    public ModuleManager moduleManager;
    public InteractionManager interactionManager;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public RotationManager rotationManager;

    public static void startup() {
        ConfigManager.refresh();
        ConfigManager.reload();
        ConfigManager.process(ConfigManager.LOAD);
    }

    public static void shutdown() {
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
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();

        this.commandManager = new CommandManager();
        this.commandManager.init();

        this.forgeEventManager = new ForgeEventManager();
        this.forgeEventManager.init();

        this.configManager = new ConfigManager();
        this.configManager.init();

        this.rotationManager = new RotationManager();
        this.rotationManager.init();

        this.tpsManager = new TPSManager();
        this.tpsManager.load();

        gui = new PanelStudioGUI();

        MinecraftForge.EVENT_BUS.register(this.forgeEventManager);

        startup();

        Runtime.getRuntime().addShutdownHook(new Thread("Infinity ShutdownHook") {
            @Override
            public void run() {
                Infinity.shutdown();
            }
        });
    }
}