package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;
import java.util.function.Predicate;

/**
 * @author lyric and vikas!!
 */

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String value) {
        super(name, value);
    }

    public StringSetting(String name, String value, Predicate<String> shown) {
        super(name, value, shown);
    }

    @Override
    public String getValue() {
        return (String)this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}