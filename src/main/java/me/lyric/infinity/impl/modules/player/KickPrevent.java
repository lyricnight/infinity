package me.lyric.infinity.impl.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;

public class KickPrevent extends Module {
    public static KickPrevent INSTANCE = new KickPrevent();
    public KickPrevent ()
    {
        super("KickPrevent", "Prevents packet kicks.", Category.PLAYER);
    }
    public static KickPrevent getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KickPrevent();
        }
        return INSTANCE;
    }

    @Override
    public String getDisplayInfo()
    {
        return ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "cancel" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }

}
