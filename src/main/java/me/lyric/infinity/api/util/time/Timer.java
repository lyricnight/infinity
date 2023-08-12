package me.lyric.infinity.api.util.time;

/**
 * @author lyric
 */

public class Timer {

    public long time;
    public Timer() {
        this.time = System.currentTimeMillis();
    }
    public Timer reset(long time) {
        this.time = time;
        return this;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTimer(long time) {
        if (time > 0.0f) {
            this.time = time;
        }
    }
    public boolean passedMs(long ms) {
        return passedNS(convertToNS(ms));
    }
    public long convertToNS(long time) {
        return time * 1000000L;
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - time >= ns;
    }
    public void reset() {
        this.time = System.nanoTime();
    }
}
