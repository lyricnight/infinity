package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.mixins.entity.MixinEntityRenderer;

/**
 * @author zzurio
 * {@link MixinEntityRenderer}
 */

public class CameraClip extends Module {

    public Setting<Boolean> extend = register(new Setting<>("Extend", "Choose whether to extend the camera clipping.", false));
    public Setting<Double> distance = register(new Setting<>("Distance", "The distance to clip the camera.", 10.0, 0.0, 50.0));

    public CameraClip() {
        super("CameraClip", "Lets the camera clip through blocks in third person.", Category.RENDER);
    }
}
