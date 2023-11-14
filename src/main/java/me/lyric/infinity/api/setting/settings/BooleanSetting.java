/*
 * Decompiled with CFR 0.150.
 */
package me.lyric.infinity.api.setting.settings;

import me.lyric.infinity.api.setting.Setting;
import java.util.function.Predicate;

/**
 * @author lyric and vikas !!!
 */

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean value) {
        super(name, value);
    }

    public BooleanSetting(String name, boolean value, Predicate<Boolean> shown) {
        super(name, value, shown);
    }

    @Override
    public Boolean getValue() {
        return (Boolean)this.value;
    }
}

