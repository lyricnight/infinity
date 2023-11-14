package me.lyric.infinity.api.util.gl;

import me.lyric.infinity.api.util.minecraft.IGlobals;

/**
 * @author lyric
 * @apiNote this is used for the GUI animations.
 */
public class AnimationUtils implements IGlobals {
    public static Integer increaseNumber(int input, int target, int delta) {
        if (input < target) {
            return input + delta;
        }
        return target;
    }

    public static Float increaseNumber(float input, float target, float delta) {
        if (input < target) {
            return Float.valueOf(input + delta);
        }
        return Float.valueOf(target);
    }

    public static Double increaseNumber(double input, double target, double delta) {
        if (input < target) {
            return input + delta;
        }
        return target;
    }

    public static Integer decreaseNumber(int input, int target, int delta) {
        if (input > target) {
            return input - delta;
        }
        return target;
    }

    public static Float decreaseNumber(float input, float target, float delta) {
        if (input > target) {
            return Float.valueOf(input - delta);
        }
        return Float.valueOf(target);
    }
}
