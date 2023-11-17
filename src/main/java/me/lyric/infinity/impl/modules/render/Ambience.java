package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.ColorSetting;


/**
 * @author lyric
 */

@ModuleInformation(name = "Ambience", description = "Changes the color of your game.", category = Category.Render)
public class Ambience extends Module {

    public ColorSetting color = createSetting("Colour", defaultColor);

}
