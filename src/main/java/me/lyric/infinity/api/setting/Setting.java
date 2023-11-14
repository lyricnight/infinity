package me.lyric.infinity.api.setting;

import me.lyric.infinity.api.module.Module;

import java.util.function.Predicate;

/**
 * @author lyric and vikas!!!
 */

public class Setting<T> {

    public String name;
    public Module module;
    public T value;
    public Predicate<T> shown;
    public boolean isOpen = false;

    public Setting(String name) {
        this.name = name;
    }

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public Setting(String name, T value, Predicate<T> shown) {
        this.name = name;
        this.value = value;
        this.shown = shown;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isVisible() {
        if (this.shown == null) {
            return true;
        }
        return this.shown.test(this.getValue());
    }
}
