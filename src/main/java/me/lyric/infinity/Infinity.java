package me.lyric.infinity;

import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.LambdaHandler;
import me.lyric.infinity.api.util.gl.SplashProgress;
import me.lyric.infinity.api.util.string.ClientFont;
import me.lyric.infinity.manager.Managers;
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

    public Managers managers;

    public static ClientFont infinityFont;

    public static EventBus eventBus = new EventBus(LambdaHandler.class, Infinity.LOGGER::error, Infinity.LOGGER::info);

    public String version = "v5";

    @Mod.Instance
    public static Infinity INSTANCE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.whoosh("Infinity");
    }

    public void whoosh(String whoosh) {
        managers = new Managers();
        SplashProgress.setProgress(1, "Initializing Minecraft");
        LOGGER.info("Initialising Infinity.");
        Display.setTitle("Infinity " + version);
        LOGGER.info("Attempted to set window title.");
        Managers.init();
        infinityFont = new ClientFont("Comfortaa-Regular", 17.0f);
        LOGGER.info("Infinity has set its font.");
        LOGGER.info("Infinity fully initialised!");

        Runtime.getRuntime().addShutdownHook(new Thread("Infinity ShutdownHook") {
            @Override
            public void run() {
                Managers.unload();
                LOGGER.info("Infinity shutdown success!");
            }
        });
    }
}