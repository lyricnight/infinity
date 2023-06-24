package me.lyric.infinity.impl.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
/**
    @author lyric :P
 */
public class KickPrevent extends Module {
    public KickPrevent ()
    {
        super("KickPrevent", "Prevents packet kicks, by cancelling IOException.", Category.PLAYER);
    }

    @Override
    public String getDisplayInfo()
    {
        return ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "cancel" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

}
