package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;

import java.awt.*;

/**
 * @author lyric
 */

public class Ambience extends Module {

    public Setting<ColorPicker> color = createSetting("Colour", "The colour to use for ambience.", new ColorPicker(Color.WHITE)));
    public Ambience() {
        super("Ambience", "Changes the color of everything.", Category.RENDER);
    }
}
