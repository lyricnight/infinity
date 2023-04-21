package me.lyric.infinity.api.setting.settings;

import org.lwjgl.input.Keyboard;

/**
 * @author zzurio
 */

public class Bind {

    public int key;
    public boolean state;

    public Bind() {
        this.key = -1;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key == -1 ? "NONE" : Keyboard.getKeyName(this.key).toUpperCase();
    }
}
