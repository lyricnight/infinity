package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;

import java.util.function.Predicate;

/**
 * @author lyric
 * delays
 */
@ModuleInformation(name = "Delays", description = "we GOING FASTER out here", category = Category.Player)
public class Delays extends Module {
    public BooleanSetting eat = createSetting("Eating", false);
    public BooleanSetting bk = createSetting("Breaking", false);
    public BooleanSetting drop = createSetting("Dropping", false);
    public IntegerSetting dropspeed = createSetting("Speed", 5, 0, 5, (Predicate<Integer>) v -> drop.getValue());
    int delay = 0;
    @Override
    public void onUpdate()
    {
        if (bk.getValue())
        {
            IPlayerControllerMP controllerMP = (IPlayerControllerMP) mc.playerController;
            controllerMP.setBlockHitDelay(0);
        }
        if (drop.getValue())
        {
            if(mc.gameSettings.keyBindDrop.isKeyDown())
            {
                delay++;
                if(delay > dropspeed.getValue())
                {
                    mc.player.dropItem(false);
                    delay = 0;
                }
            }
            else
            {
                delay = 0;
            }
        }
    }


}
