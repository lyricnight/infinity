package me.lyric.infinity.api.event.misc;


import me.bush.eventbus.event.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RightClickItemEvent extends Event {
    private final EntityPlayer player;
    private final World worldIn;
    private final EnumHand hand;

    public RightClickItemEvent(EntityPlayer player, World worldIn, EnumHand hand) {
        this.player = player;
        this.worldIn = worldIn;
        this.hand = hand;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public World getWorldIn() {
        return worldIn;
    }

    public EnumHand getHand() {
        return hand;
    }

    @Override
    public boolean isCancellable()
    {
        return true;
    }
}