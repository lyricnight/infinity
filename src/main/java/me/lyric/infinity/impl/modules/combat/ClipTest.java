package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Objects;

/**
 * @author lyric
 * this is weird
 */
public class ClipTest extends Module {

    public int packets = 0;
    public ClipTest ()
    {
        super("ClipTest", "new!!!", Category.COMBAT);
    }
    @Override
    public void onDisable()
    {
        packets = 0;
    }

    @Override
    public void onUpdate() {
        double rad = Math.toRadians(mc.player.rotationYaw);

        double x = mc.player.posX + (0.056f * -Math.sin(rad));
        double z = mc.player.posZ + (0.056f * Math.cos(rad));
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(x, mc.player.posY, z, true));
        packets++;
        mc.player.setPosition(x, mc.player.posY, z);
    }
    @Override
    public String getDisplayInfo() {
        return ChatFormatting.GRAY + "[" + ChatFormatting.RESET+ChatFormatting.WHITE + packets + ChatFormatting.RESET+ ChatFormatting.GRAY + "]";
    }


}
