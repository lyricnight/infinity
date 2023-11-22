package me.lyric.infinity.impl.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.mixin.mixins.accessors.ITimer;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

/**
 * @author lyric
 */

@ModuleInformation(name = "HoleSnap", description = "we SNAPPING out here", category = Category.Movement)
public class HoleSnap extends Module {
    public FloatSetting range = createSetting("Range", 4.5f, 0.1f, 12.0f);

    public FloatSetting factor = createSetting("Factor", 2.5f, 1.0f, 15.0f);

    public IntegerSetting vdist = createSetting("Vertical-Cutoff", 2, 1, 4);

    public BooleanSetting debug = createSetting("Debug", false);

    public BooleanSetting step = createSetting("Step", false);

    public FloatSetting stepHeight = createSetting("Height", 2.0f, 1.0f, 4.0f, v -> step.getValue());

    Timer timer = new Timer();
    HoleUtil.Hole holes;

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        timer.reset();
        holes = null;
    }
    @Override
    public void onDisable()
    {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (step.getValue())
        {
            mc.player.stepHeight = 0.6f;
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
        if (EntityUtil.isInLiquid()) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in liquid! Disabling ..");
            disable();
            return;
        }
        holes = EntityUtil.getTargetHoleVec3D(range.getValue(), vdist.getValue());
        Infinity.INSTANCE.moduleManager.getModuleByClass(InstantSpeed.class).pause = true;
        if (debug.getValue())
        {
            ChatUtils.sendMessage("Reached holegetter, and disabled instantspeed.");
        }
        if (holes == null || HoleUtil.isHole(EntityUtil.getPlayerPos()) || CombatUtil.isBurrow(mc.player)) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player reached hole, or no holes in range, disabling...");
            disable();
            return;
        }
        if (timer.passedMs(500L) && SpeedUtil.anyMovementKeys()) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "HoleSnap timed out, disabling...");
            disable();
            return;
        }
        if (step.getValue()) {
            step(stepHeight.getValue());
        }
        Vec3d playerPos = mc.player.getPositionVector();
        Vec3d targetPos = new Vec3d((double)holes.pos1.getX() + 0.5, mc.player.posY, (double)holes.pos1.getZ() + 0.5);
        double yawRad = Math.toRadians(RotationUtil.getRotationTo(playerPos, targetPos).x);
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
            disable();
        }
    }
    public void step(float height) {
        if (mc.player.isOnLadder()) {
            return;
        }
        mc.player.stepHeight = height;
    }
}