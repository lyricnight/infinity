package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.manager.client.PresenceManager;

/**
 * @author zzurio
 */

public class RPC extends Module {

    public Setting<String> largeImageText = register(new Setting("Large Image Text", "The large image text for the RPC.", "Solaros 0.0.1"));
    public Setting<String> details = register(new Setting<>("Details", "The details for the RPC.", "Winning with Solaros!"));
    public Setting<Boolean> showIP = register(new Setting<>("Show IP", "Show the server name you are playing on.", true));

    public RPC() {
        super("RPC", "Displays a rich presence on Discord to show you're using Solaros.", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        PresenceManager.start();
    }

    @Override
    public void onDisable() {
        PresenceManager.shutdown();
    }
}
