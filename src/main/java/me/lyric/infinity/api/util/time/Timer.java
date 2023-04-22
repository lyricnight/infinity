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

    public long getMS(long time) {
        return time / 1000000L;
    }

    public boolean hasPassed(int millis) {
        return (System.currentTimeMillis() - this.time) >= millis;
    }

    public boolean passAndReset(int millis) {
        if (System.currentTimeMillis() - this.time >= millis) {
            reset();
            return true;
        } else return false;
    }

    public void setTimer(long time) {
        if (time > 0.0f) {
            this.time = time;
        }
    }

    public boolean sleep(long time) {
        if ((System.nanoTime() / 1000000L - time) >= time) {
            reset();
            return true;
        }

        return false;
    }
    public boolean passedS(double s) {
        return passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return passedNS(convertToNS(ms));
    }
    public long convertToNS(long time) {
        return time * 1000000L;
    }
    public void setMs(long ms) {
        time = System.nanoTime() - convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - time >= ns;
    }


    public void reset() {
        this.time = System.nanoTime();
    }
}
