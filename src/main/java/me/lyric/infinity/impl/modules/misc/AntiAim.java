package me.lyric.infinity.impl.modules.misc;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.minecraft.rotation.Rotation;
import me.lyric.infinity.mixin.mixins.accessors.ICPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lyric :)
 * this is alright I guess
 */

@ModuleInformation(getName = "AntiAim", getDescription = "Have a mental breakdown", category = Category.Misc)
public class AntiAim extends Module {

    public ModeSetting yaw = createSetting("Yaw", "Linear", Arrays.asList("Linear", "Reverse", "Random", "Toggle", "None"));
    public ModeSetting pitch = createSetting("Pitch", "MinMax", Arrays.asList("Toggle", "Random", "MinMax", "None"));

    public ModeSetting mode = createSetting("Rotation Mode", "Client", Arrays.asList("Client", "Packet"));

    int aimTicks = 0;
    float aimYaw = 0;
    float aimPitch = 0;
    boolean aimToggle = false;

    @Override
    public void onEnable() {
        aimTicks = 0;
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        switch (yaw.getValue()) {
            case "Linear":
                aimYaw += 5;
                break;
            case "Reverse":
                aimYaw -= 5;
                break;
            case "Random":
                aimYaw = ThreadLocalRandom.current().nextInt(0, 360);
                break;
            case "Toggle":
                aimYaw += ThreadLocalRandom.current().nextInt(-360, 360);
                break;
            case "None":
                break;
        }

        switch (pitch.getValue()) {
            case "Toggle":
                if (aimPitch == -90 || aimPitch == 90) {
                    aimToggle = !aimToggle;
                }
                aimPitch = aimPitch + (aimToggle ? 5 : -5);
                break;
            case "Random":
                aimPitch = ThreadLocalRandom.current().nextInt(-90, 90);
                break;
            case "MinMax":
                aimPitch = (aimTicks % 2 == 0) ? 90 : -90;
                break;
            case "None":
                break;
        }

        aimPitch = MathHelper.clamp(aimPitch, -90, 90);

        Rotation aimRotation = new Rotation(aimYaw, aimPitch, Rotation.Rotate.PACKET);
        aimRotation.updateModelRotations();

        aimTicks++;
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketPlayer) {
            ((ICPacketPlayer) event.getPacket()).setYaw(aimYaw);
            ((ICPacketPlayer) event.getPacket()).setPitch(aimPitch);
        }
    }

    @Override
    public String getDisplayInfo() {
        return yaw.getValue().toLowerCase() + ", " + pitch.getValue().toLowerCase();
    }
}

