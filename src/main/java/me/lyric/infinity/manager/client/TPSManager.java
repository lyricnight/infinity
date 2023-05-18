package me.lyric.infinity.manager.client;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.mixin.mixins.accessors.ITimer;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class TPSManager
        implements IGlobals {
    private long prevTime;
    private final float[] ticks = new float[20];
    private int currentTick;
    private float TPS = 20.0f;
    static float timer2 = 1.0f;
    private long lastUpdate = -1L;
    private final Timer timer = new Timer();
    private final float[] tpsCounts = new float[10];
    private final DecimalFormat format = new DecimalFormat("##.00");

    public void load() {
        this.prevTime = -1L;
        int len = this.ticks.length;
        for (int i = 0; i < len; ++i) {
            this.ticks[i] = 0.0f;
        }
        Infinity.INSTANCE.eventBus.subscribe(this);
    }

    public static void unload()
    {
        timer2 = 1.0f;
        ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
    }
    public Float getTickRate() {
        int tickCount = 0;
        float tickRate = 0.0f;
        for (float tick : this.ticks) {
            if (!(tick > 0.0f)) continue;
            tickRate += tick;
            ++tickCount;
        }

        return MathHelper.clamp((tickRate / (float)tickCount), 0.0f, 20.0f);
    }
    public String getTickRateRound() {
        int tickCount = 0;
        float tickRate = 0.0f;
        for (float tick : this.ticks) {
            if (!(tick > 0.0f)) continue;
            tickRate += tick;
            ++tickCount;
        }

        return format.format(MathHelper.clamp((tickRate / (float)tickCount), 0.0f, 20.0f));
    }


    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        timer.reset();
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            long currentTime = System.currentTimeMillis();

            if (lastUpdate == -1) {
                lastUpdate = currentTime;
                return;
            }

            long timeDiff = currentTime - lastUpdate;

            float tickTime = timeDiff / 20.0F;
            if (tickTime == 0) {
                tickTime = 50;
            }

            float tps = 1000 / tickTime;

            System.arraycopy(tpsCounts, 0, tpsCounts, 1, tpsCounts.length - 1);
            tpsCounts[0] = tps;

            this.TPS = tps;
            lastUpdate = currentTime;
        }
    }


    public void reset() {
        this.TPS = 20.0f;
    }

    public float getTPS() {
        return this.TPS;
    }

    @EventListener
    public void receivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (this.prevTime != -1L) {
                this.ticks[this.currentTick % this.ticks.length] = MathHelper.clamp(20.0f / ((float)(System.currentTimeMillis() - this.prevTime) / 1000.0f), 0.0f, 20.0f);
                ++this.currentTick;
            }
            this.prevTime = System.currentTimeMillis();
        }
    }
    public void set(float timer) {
        if (timer > 0.0f) {
            ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / timer);
        }
    }
    public void reset2() {
        timer2 = 1.0f;
        ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
    }

}