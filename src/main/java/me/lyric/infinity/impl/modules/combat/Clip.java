package me.lyric.infinity.impl.modules.combat;

import io.netty.util.internal.ConcurrentSet;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Set;
/**
*  @author some mexican I think
 *  I kinda changed it a bit
*/

public class Clip extends Module {

    public Setting<Float> factor = register(new Setting<>("Factor", "Speed of packet sending.", 0.4f, 0f, 1f));

    public Setting<Boolean> update = register(new Setting<>("Update", "Updates real position.", true));

    public Setting<Boolean> extra = register(new Setting<>("Extra", "Sends an extra packet.", false));

    public Setting<Boolean> canceller = register(new Setting<>("Cancel", "Cancels extra packets.", true));

    public Setting<Boolean> bandhu = register(new Setting<>("BandhuMethod", "", false));

    public Setting<Boolean> shift = register(new Setting<>("Sneak", "", false));

    public Setting<Boolean> removeHitbox = register(new Setting<>("RemoveHitbox", "", false));

    private Timer timer = new Timer();

    private Set<CPacketPlayer> packets = new ConcurrentSet<>();

    public Clip() {
        super("Clip", "Clips into blocks nearby to prevent crystal damage.", Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
        timer.reset();
    }

    @Override
    public void onUpdate() {
        mc.player.setVelocity(0.0, 0.0, 0.0);
        double offset = (mc.player.movementInput.sneak && shift.getValue()) ? -0.062 : 0.0;
        double[] strafing = getMotion();
        for (int i = 1; i < 1 + 1; ++i) {
            mc.player.motionX = strafing[0] * (double) i * factor.getValue();
            mc.player.motionY = offset * (double) i;
            mc.player.motionZ = strafing[1] * (double) i * factor.getValue();
            Vec3d pos = mc.player.getPositionVector().add(new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ));
            if (extra.getValue())
            {
                send(new CPacketPlayer.Position(pos.x, pos.y, pos.z, mc.player.onGround));
            }
            if (bandhu.getValue()) {
                mc.player.setPosition(pos.x, pos.y, pos.z);
            }
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer)
        {
            if (!packets.remove(event.getPacket()) && canceller.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onMove(MoveEvent event) {
        if (update.getValue()) {
            event.setMotionX(mc.player.motionX);
            event.setMotionY(mc.player.motionY);
            event.setMotionZ(mc.player.motionZ);
            if (removeHitbox.getValue() && checkHitBoxes()) {
                mc.player.noClip = true;
            }
        }
    }
    public void send(Packet<?> packet) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null) {
            packets.add((CPacketPlayer) packet);
            connection.sendPacket(packet);
        }
    }
    protected double[] getMotion() {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double) moveForward * 0.031 * -Math.sin(Math.toRadians(rotationYaw)) + (double) moveStrafe * 0.031 * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double) moveForward * 0.031 * Math.cos(Math.toRadians(rotationYaw)) - (double) moveStrafe * 0.031 * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    protected boolean checkHitBoxes() {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }
}