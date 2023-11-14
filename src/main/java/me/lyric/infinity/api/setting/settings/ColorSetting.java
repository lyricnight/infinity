package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;
import java.awt.*;
import java.util.function.Predicate;

/**
 * @author lyric and vikas!!!
 */

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String name, Color value) {
        super(name, value);
    }

    public ColorSetting(String name, Color value, Predicate<Color> shown) {
        super(name, value, shown);
    }

    public void setColor(Color value) {
        this.value = value;
    }
}