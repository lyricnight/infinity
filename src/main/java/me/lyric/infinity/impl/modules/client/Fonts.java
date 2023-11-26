package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.setting.settings.StringSetting;
import me.lyric.infinity.manager.Managers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ModuleInformation(name = "Fonts", description = "Font stuff.", category = Category.Client)
public class Fonts extends Module {

    public StringSetting fontName = createSetting("Font", "Arial");

    public ModeSetting fontStyle = createSetting("Font-Style", "Plain", Arrays.asList("Plain", "Bold", "Italic", "All"));

    public IntegerSetting fontSize = createSetting("Font-Size", 18, 1, 30);

    public BooleanSetting shadow = createSetting("Shadow", true);

    public BooleanSetting aalias = createSetting("AntiAlias", false);

    public BooleanSetting frac = createSetting("Metrics", false);

    public BooleanSetting changeHeight = createSetting("Change-Height", false);

    public IntegerSetting heightSub = createSetting("Height-Minus", 8, -10, 10);

    public IntegerSetting heightFactor = createSetting("Height-Factor", 2, 1, 4);

    public IntegerSetting heightAdd = createSetting("Height-Add", 0, -10, 10);

    public final List<String> fonts = new ArrayList<>();


    @Override
    public void onEnable()
    {
        Collections.addAll(fonts, GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        setFont();
    }

    private void setFont()
    {
        //Font doesn't like using the int value! Too bad.
        //noinspection magicConstant
        Managers.FONT.setFontRenderer(new Font(fontName.getValue(), getStyleFromString(fontStyle.getValue()), fontSize.getValue()), aalias.getValue(), frac.getValue());
    }

    private int getStyleFromString(String string)
    {
        switch (string)
        {
            case "Plain":
                return 0;
            case "Bold":
                return 1;
            case "Italic":
                return 2;
            case "All":
                return 3;
        }
        Infinity.LOGGER.error("Infinity's font didn't set properly! Unable to read getStyleFromString. Value passed: " + string);
        return 0;
    }
}
