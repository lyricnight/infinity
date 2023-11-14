package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;
import java.util.function.Predicate;

/**
 * @author lyric and vikas!!
 */

public class FloatSetting extends Setting<Float> {
    float minimum;
    float maximum;

    public FloatSetting(String name, float value, float minimum, float maximum) {
        super(name, Float.valueOf(value));
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public FloatSetting(String name, float value, float minimum, float maximum, Predicate<Float> shown) {
        super(name, Float.valueOf(value), shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Float getValue() {
        return (Float)this.value;
    }

    public float getMaximum() {
        return this.maximum;
    }

    public float getMinimum() {
        return this.minimum;
    }
}