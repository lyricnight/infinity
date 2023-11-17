package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.StringSetting;
import me.lyric.infinity.manager.client.PresenceManager;
@ModuleInformation(name = "RPC", description = "Displays a Rich Presence on discord.", category = Category.Client)
public class RPC extends Module {

    public StringSetting largeImageText = createSetting("Large Image Text",  "Infinity v5");
    public StringSetting details = createSetting("Details","yung n rich");
    public BooleanSetting showIP = createSetting("Show IP", true);
    public BooleanSetting ign = createSetting("Show IGN", false);
    @Override
    public void onEnable() {
        PresenceManager.start();
    }

    @Override
    public void onDisable() {
        PresenceManager.shutdown();
    }
}
