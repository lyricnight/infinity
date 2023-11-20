package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.Setting;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lyric and vikas!!
 */

public final class ModeSetting extends Setting<String> {
    public List<String> modes;

    public ModeSetting(String name, String value, List<String> modeList) {
        super(name, value);
        this.modes = modeList;
    }

    public ModeSetting(String name, String value, List<String> modeList, Predicate<String> shown) {
        super(name, value, shown);
        this.modes = modeList;
    }

    @Override
    public void setValue(String value) {
        for (String mode : modes)
        {
            if (mode.equalsIgnoreCase(value))
            {
                this.value = mode;
            }
            else {
                Infinity.LOGGER.error("A ModeSetting may have been set incorrectly! Module: " + this.getModule() + " Value passed to the method: " + value + " All the possible values: " + modes.toString());
            }
        }
    }
}