package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.manager.client.RotationManager;
import me.lyric.infinity.mixin.mixins.accessors.ITimer;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

/**
 * @author lyric
 * better version of cascade holesnap
 */

public class HoleSnap extends Module {
    public Setting<Float> range = register(new Setting<>("Range","Range to snap.", 4.5f, 0.1f, 12.0f));
    public Setting<Float> factor = register(new Setting<>("Factor","Factor for the ", 2.5f, 1.0f, 15.0f));
    public Setting<Boolean> debug = register(new Setting<>("Debug", "For testing.", false));

    Timer timer = new Timer();
    HoleUtil.Hole holes;

    public HoleSnap() {
        super("HoleSnap","TPs you to closest hole.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        timer.reset();
        holes = null;
    }
    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        timer.reset();
        holes = null;
        if (((ITimer) ((IMinecraft) mc).getTimer()).getTickLength() != 50.0f) {
            Infinity.INSTANCE.tpsManager.reset2();
        }
        Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (!mc.player.onGround)
        {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in the air! Disabling ..");
            toggle();
        }
        Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = true;
        if (EntityUtil.isInLiquid()) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in liquid! Disabling ..");
            Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = false;
            toggle();
            return;
        }
        holes = RotationManager.getTargetHoleVec3D(range.getValue());
        if (debug.getValue())
        {
            ChatUtils.sendMessage("Reached holegetter!" + " " + holes.pos1);
        }
        if (holes == null || HoleUtil.isHole(RotationManager.getPlayerPos()) || CombatUtil.isBurrow(mc.player)) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in hole, or no holes in range, disabling...");
            Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = false;
            toggle();
            return;
        }
        if (timer.passedMs(500L) && SpeedUtil.anyMovementKeys()) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "HoleSnap timed out, disabling...");
            Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = false;
            toggle();
            return;
        }
        Vec3d playerPos = mc.player.getPositionVector();
        Vec3d targetPos = new Vec3d((double)holes.pos1.getX() + 0.5, mc.player.posY, (double)holes.pos1.getZ() + 0.5);
        double yawRad = Math.toRadians(RotationManager.getRotationTo(playerPos, targetPos).x);
        double dist = playerPos.distanceTo(targetPos);
        double speed = mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -SpeedUtil.getSpeed() + 0.02;
        if (debug.getValue())
        {
            String cout = Objects.requireNonNull(String.valueOf(yawRad));
            String cout2 = Objects.requireNonNull(String.valueOf(dist));
            String cout3 = Objects.requireNonNull(String.valueOf(speed));
            ChatUtils.sendMessage(cout);
            ChatUtils.sendMessage(cout2);
            ChatUtils.sendMessage(cout3);
        }
        Infinity.INSTANCE.tpsManager.set(factor.getValue());
        mc.player.motionX = -Math.sin(yawRad) * speed;
        mc.player.motionZ = Math.cos(yawRad) * speed;
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "HoleSnap lagged you back! Preventing snapping...");
            toggle();
        }
    }
}