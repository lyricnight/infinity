package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;

import java.util.function.Predicate;

/**
 * @author lyric and vikas!!
 */

public class KeySetting extends Setting<Integer> {
    public KeySetting(String name, int value) {
        super(name, value);
    }

    public KeySetting(String name, int value, Predicate<Integer> shown) {
        super(name, value, shown);
    }
}