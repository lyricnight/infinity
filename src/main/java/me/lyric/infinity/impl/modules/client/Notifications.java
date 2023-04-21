package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

public class Notifications extends Module {

    public Setting<Boolean> modules = register(new Setting<>("Modules", "Chat notifications when a module is enabled or disabled.", true));

    public Notifications() {
        super("Notifications", "Handle various notifications.", Category.CLIENT);
    }
}
