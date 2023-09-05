package me.lyric.infinity.impl.modules.player;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;

/**
 * @author lyric
 * delays
 */
public class Delays extends Module {
    public Setting<Boolean> eat = register(new Setting<>("Eating", "Whether to remove eating delay or not.", false));

    public Setting<Boolean> bk = register(new Setting<>("Breaking", "Whether to remove breaking delay or not.", false));


    public Delays()
    {
        super("Delays", "Module that handles all delays.", Category.PLAYER);
    }
    @Override
    public void onUpdate()
    {
        if (bk.getValue())
        {
            IPlayerControllerMP controllerMP = (IPlayerControllerMP) mc.playerController;
            controllerMP.setBlockHitDelay(0);
        }
    }


}
