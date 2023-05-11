package me.lyric.infinity.impl.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import event.bus.EventListener;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.rotation.Rotation;
import me.lyric.infinity.mixin.mixins.accessors.ICPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lyric :)
 * this is alright I guess
 */

public class AntiAim extends Module {

    public Setting<Yaw> yaw = register(new Setting<>("Yaw", "Changes how your yaw is rotated.", Yaw.LINEAR));
    public Setting<Pitch> pitch = register(new Setting<>("Pitch", "Changes how your pitch is rotated.", Pitch.NONE));

    int aimTicks = 0;
    float aimYaw = 0;
    float aimPitch = 0;
    boolean aimToggle = false;

    public AntiAim() {
        super("AntiAim", "Spoofs your head making you harder to hit.", Category.MISC);
    }

    @Override
    public void onEnable() {
        aimTicks = 0;
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) return;
        switch (yaw.getValue()) {
            case LINEAR:
                aimYaw += 5;
                break;
            case REVERSE:
                aimYaw -= 5;
                break;
            case RANDOM:
                aimYaw = ThreadLocalRandom.current().nextInt(0, 360);
                break;
            case TOGGLE:
                aimYaw += ThreadLocalRandom.current().nextInt(-360, 360);
                break;
            case NONE:
                break;
        }

        switch (pitch.getValue()) {
            case TOGGLE:
                if (aimPitch == -90 || aimPitch == 90) {
                    aimToggle = !aimToggle;
                }

                aimPitch = aimPitch + (aimToggle ? 5 : -5);
                break;
            case RANDOM:
                aimPitch = ThreadLocalRandom.current().nextInt(-90, 90);
                break;
            case MINMAX:
                aimPitch = (aimTicks % 2 == 0) ? 90 : -90;
                break;
            case NONE:
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
    public String getDisplayInfo()
    {
        if(yaw.getValue() == Yaw.LINEAR)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET  + "linear" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        if(yaw.getValue() == Yaw.REVERSE)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + "reverse" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";

        }
        if(yaw.getValue() == Yaw.RANDOM)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + "random" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";

        }
        if(yaw.getValue() == Yaw.TOGGLE)
        {
            return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + "toggle" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
        }
        return ChatFormatting.GRAY + "[" +ChatFormatting.RESET + "none" + ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

    public enum Yaw {
        LINEAR, REVERSE, RANDOM, TOGGLE, NONE
    }

    public enum Pitch {
        TOGGLE, RANDOM, MINMAX, NONE
    }
}
