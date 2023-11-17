package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.FloatSetting;

import java.text.DecimalFormat;


/**
 * @author lyricccc
 */
@ModuleInformation(name = "CModifier", description = "Changes things about crystal rendering.", category = Category.Render)
public class CModifier extends Module {

    public FloatSetting scale = createSetting("Scale",  1f, 0f, 2f);
    public FloatSetting spinSpeed = createSetting("SpinSpeed",  1.0f, 0f, 5f);
    public FloatSetting bounceFactor = createSetting("BounceFactor", 1.0f, 0.0f, 2f);

    DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public String getDisplayInfo()
    {
        if (!nullSafe()) return "";
        return format.format(scale.getValue()) + ", " + format.format(spinSpeed.getValue()) + ", " + format.format(bounceFactor.getValue());
    }
}
