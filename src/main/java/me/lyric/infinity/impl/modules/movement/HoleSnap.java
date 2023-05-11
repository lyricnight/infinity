package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.EntityUtil;
import me.lyric.infinity.api.util.minecraft.HoleUtil;
import me.lyric.infinity.api.util.minecraft.MovementUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.manager.client.RotationManager;
import me.lyric.infinity.mixin.mixins.accessors.ITimer;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;

public class HoleSnap
        extends Module {
    public Setting<Float> range = register(new Setting<>("Range","Range to snap.", 4.5f, 0.1f, 12.0f));
    public Setting<Float> factor = register(new Setting<>("Factor","Factor for the holesnap.", 2.5f, 1.0f, 15.0f));

    Timer timer = new Timer();
    HoleUtil.Hole holes;

    public HoleSnap() {
        super("HoleSnap","TPs you to closest hole.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }
    @Override
    public String getDisplayInfo()
    {
        return ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "snapping" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        this.timer.reset();
        this.holes = null;
        if (((ITimer) ((IMinecraft) mc).getTimer()).getTickLength() != 50.0f) {
            Infinity.INSTANCE.tpsManager.reset2();
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }
        if (EntityUtil.isInLiquid()) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in liquid! Disabling HoleSnap...");
            this.toggle();
            return;
        }
        this.holes = RotationManager.getTargetHoleVec3D(this.range.getValue());
        if (this.holes == null || HoleUtil.isObbyHole(RotationManager.getPlayerPos()) || HoleUtil.isBedrockHoles(RotationManager.getPlayerPos())) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in hole, or no holes in range, disabling...");
            this.toggle();
            return;
        }
        if (this.timer.passedMs(500L)) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "HoleSnap timed out, disabling...");
            this.toggle();
            return;
        }
        Vec3d playerPos = mc.player.getPositionVector();
        Vec3d targetPos = new Vec3d((double)this.holes.pos1.getX() + 0.5, HoleSnap.mc.player.posY, (double)this.holes.pos1.getZ() + 0.5);
        double yawRad = Math.toRadians(RotationManager.getRotationTo((Vec3d)playerPos, (Vec3d)targetPos).x);
        double dist = playerPos.distanceTo(targetPos);
        double speed = HoleSnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -MovementUtil.getSpeed() + 0.02;
        Infinity.INSTANCE.tpsManager.set(this.factor.getValue());
        HoleSnap.mc.player.motionX = -Math.sin(yawRad) * speed;
        HoleSnap.mc.player.motionZ = Math.cos(yawRad) * speed;
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "HoleSnap lagged you back! Preventing snapping...");
            toggle();
        }
    }
}