package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;
import java.util.function.Predicate;

/**
 * @author lyric and vikas!!
 */
public class IntegerSetting extends Setting<Integer> {
    int minimum;
    int maximum;

    public IntegerSetting(String name, int value, int minimum, int maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public IntegerSetting(String name, int value, int minimum, int maximum, Predicate<Integer> shown) {
        super(name, value, shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Integer getValue() {
        return (Integer)this.value;
    }

    public int getMaximum() {
        return this.maximum;
    }

    public int getMinimum() {
        return this.minimum;
    }
}