package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

import java.text.DecimalFormat;


/**
 * @author lyricccc
 */
public class CModifier extends Module {

    public CModifier(){
        super("CModifier", "Changes things about crystal rendering.", Category.RENDER);
    }
    public FloatSetting scale = createSetting("Scale", "Scale for the crystal.", 1f, 0f, 2f));
    public FloatSetting spinSpeed = createSetting("SpinSpeed", "Changes spin speed of crystal.", 1.0f, 0f, 5f));
    public final FloatSetting bounceFactor = createSetting("BounceFactor","Factor for bounce of crystal.", 1.0f, 0.0f, 2f));

    DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public String getDisplayInfo()
    {
        if (!nullSafe()) return "";
        return format.format(scale.getValue()) + ", " + format.format(spinSpeed.getValue()) + ", " + format.format(bounceFactor.getValue());
    }
}
