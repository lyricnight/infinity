package me.lyric.infinity.api.setting;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cpacketcustompayload
 */

public class Register {
    //TODO: figure out why string vals don't save
    private final Map<String, Setting> settings = new HashMap<>();

    public Map<String, Setting> getSettings() {
        return settings;
    }

    public Setting register(Setting setting) {
        this.settings.put(setting.getName(), setting);

        return setting;
    }

    public Setting getSetting(final String name) {
        return this.settings.get(name);
    }
}