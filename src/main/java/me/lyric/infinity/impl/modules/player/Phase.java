package me.lyric.infinity.impl.modules.player;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.SpeedUtil;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.impl.modules.movement.InstantSpeed;
import me.lyric.infinity.manager.Managers;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author lyric
 */

@ModuleInformation(name = "Phase", description = "Phases around in blocks.", category = Category.Player)
public class Phase extends Module {

    public BooleanSetting onEdge = createSetting("Edge", false);

    public ModeSetting mode = createSetting("Mode", "Clip", Arrays.asList("Clip", "Smooth"));

    public IntegerSetting delay = createSetting("Delay", 200, 0, 1000);

    public IntegerSetting attempts = createSetting("Attempts", 5, 0, 10);

    public BooleanSetting player = createSetting("Cancel", false);

    public ModeSetting handle = createSetting("Handle-Teleports", "All", Arrays.asList("All", "Below", "Above", "NoBand", "Last", "Cancel", "None"));

    public FloatSetting limit = createSetting("Limit-Amount", 0.3f, 0f, 1f);

    public IntegerSetting speed = createSetting("Speed", 2, 1, 10);

    public BooleanSetting auto = createSetting("Auto-Speed", false);

    public BooleanSetting up = createSetting("Up", false);

    private final Timer timer = new Timer();

    boolean cancel = false;

    protected int teleportID = 0;

    @Override
    public void onEnable()
    {
        Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = true;
    }

    @Override
    public void onDisable()
    {
        Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = false;
    }


    /**
     * @apiNote this method is used for managing when we get tp'd by server.
     * @param event - packet event
     */

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event)
    {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            teleportID = ((SPacketPlayerPosLook)event.getPacket()).getTeleportId();
            if (handle.getValue().equals("All")) {
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID - 1));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }
            if (handle.getValue().equals("Below")) {
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID - 1));
            }
            if (handle.getValue().equals("Above")) {
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }
            if (handle.getValue().equals("NoBand")) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(0.0, 1337.0, 0.0, mc.player.onGround));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }
        }
    }

    /**
     * @apiNote this method is used to cancel CPackets we don't want.
     * @param event - packet event
     */

    @EventListener
    public void onPacketSend(PacketEvent.Send event)
    {
        if (!nullSafe()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && cancel && player.getValue()) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport && handle.getValue() == "Cancel") {
            event.setCancelled(true);
        }
    }

    /**
     * @apiNote this method is used for our other movement mode, that being "smooth"
     * @param event - movement event.
     */


    @EventListener
    public void onMove(MoveEvent event)
    {
        if (!nullSafe()) {
            return;
        }
        if (shouldPacket()) {
            if (mode.getValue() == "Smooth") {
                double[] forward = EntityUtil.forward(getSpeed());
                for (int i = 0; i < attempts.getValue(); i++) {
                    sendPackets(mc.player.posX + forward[0], mc.player.posY + getUpMovement(), mc.player.posZ + forward[1]);
                }
            }
            event.setMotionX(0.0);
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setMotionY(0.05);
            }
            else {
                event.setMotionY(0.0);
            }
            event.setMotionZ(0.0);
        }
    }

    /**
     * @apiNote this is used to clip in case we select it.
     */
    @Override
    public void onUpdate()
    {
        if (!nullSafe()) {
            return;
        }
        mc.player.motionX = 0.0;
        mc.player.motionY = 0.0;
        mc.player.motionZ = 0.0;
        if (mode.getValue().equals("Clip")) {
            if (shouldPacket()) {
                if (timer.passedMs(delay.getValue())) {
                    double[] forward = EntityUtil.forward(getSpeed());
                    for (int i = 0; i < ((Number)this.attempts.getValue()).intValue(); ++i) {
                        sendPackets(mc.player.posX + forward[0], mc.player.posY + getUpMovement(), mc.player.posZ + forward[1]);
                    }
                    timer.reset();
                }
            }
            else {
                cancel = false;
            }
        }
    }

    private double getUpMovement() {
        return (mc.gameSettings.keyBindJump.isKeyDown() ? 1 : (mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0)) * getSpeed();
    }

    private void sendPackets(double x, double y, double z)
    {
        cancel = false;
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(0.0, 1337.0, 0.0, mc.player.onGround));
        cancel = true;
    }


    private double getSpeed() {
        return auto.getValue() ? (SpeedUtil.getDefaultMoveSpeed() / 10.0) : ((speed.getValue()).doubleValue() / 100.0);
    }

    private boolean shouldPacket() {
        return !onEdge.getValue() || mc.player.collidedHorizontally;
    }

    @Override
    public String getDisplayInfo()
    {
        return speed + ", "+ handle.getValue();
    }
}
