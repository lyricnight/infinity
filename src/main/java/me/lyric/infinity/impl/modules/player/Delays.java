package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;

/**
 * @author lyric
 * delays
 */
@ModuleInformation(getName = "Delays", getDescription = "we GOING FASTER out here", category = Category.Player)
public class Delays extends Module {
    public Setting<Boolean> eat = register(new Setting<>("Eating", "Whether to remove eating delay or not.", false));
    public Setting<Boolean> bk = register(new Setting<>("Breaking", "Whether to remove breaking delay or not.", false));
    public Setting<Boolean> drop = register(new Setting<>("Dropping", "Modifies the delay while dropping a stack using Q.", false));
    public Setting<Integer> dropspeed = register(new Setting<>("Speed", "Speed of dropping items.", 5, 0, 5).withParent(drop));
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
