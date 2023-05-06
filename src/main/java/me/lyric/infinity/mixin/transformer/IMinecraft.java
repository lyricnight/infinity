package me.lyric.infinity.mixin.transformer;

import net.minecraft.util.Timer;

public interface IMinecraft {
    int getGameLoop();
    Timer getTimer();
}
