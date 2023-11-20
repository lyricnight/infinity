package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.StringSetting;
import me.lyric.infinity.api.util.string.ChatFormat;
import me.lyric.infinity.api.util.string.Renderer;

import java.awt.*;
import java.util.function.Predicate;

@ModuleInformation(name = "Internals", description = "Handles internal settings that don't fit anywhere else.", category = Category.Client)
public class Internals extends Module {

    public BooleanSetting cfont = createSetting("CustomFont", false);

    public BooleanSetting aalias = createSetting("AntiAlias", false, v -> cfont.getValue());

    public BooleanSetting frac = createSetting("Metrics", false, v -> cfont.getValue());

    public FloatSetting size = createSetting("Size", 18.0f, 10.0f, 25.0f, v -> cfont.getValue());

    public StringSetting commandBracket = createSetting("Bracket", "[");
    public StringSetting commandBracket2 = createSetting("Bracket 2",  "]");
    public BooleanSetting unf = createSetting("UnfocusedFPS", false);
    public IntegerSetting fps = createSetting("FPS", 60, 10, 200, (Predicate<Integer>) v -> unf.getValue());
    public BooleanSetting fov = createSetting("FOVModifier", false);
    public FloatSetting fovslider = createSetting("FOV", 130f, 30f, 180f, v -> fov.getValue());

    public Font font = new Font("Comfortaa-Regular", 0, 18);

    public Renderer renderer = new Renderer(font, aalias.getValue(), frac.getValue());

    @Override
    public void onUpdate() {
        Infinity.INSTANCE.commandManager.setClientMessage(getCommandMessage());
        if(fov.getValue())
        {
            mc.gameSettings.fovSetting = fovslider.getValue();
        }
        if(cfont.getValue())
        {
            setFonts();
        }
    }

    private void setFonts()
    {
        font = font.deriveFont(size.getValue());
        font = font.deriveFont(0);
        renderer = new Renderer(font, aalias.getValue(), frac.getValue());
    }

    public String getCommandMessage() {
        return ChatFormat.coloredString(this.commandBracket.getValue(), ChatFormat.DARK_PURPLE) + ChatFormat.coloredString("Infinity", ChatFormat.LIGHT_PURPLE) + ChatFormat.coloredString(this.commandBracket2.getValue(), ChatFormat.DARK_PURPLE);
    }


}
