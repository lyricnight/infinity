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

import java.util.function.Predicate;

@ModuleInformation(getName = "Internals", getDescription = "Handles internal settings that don't fit anywhere else.", category = Category.Client)
public class Internals extends Module {
    public StringSetting commandBracket = createSetting("Bracket", "[");
    public StringSetting commandBracket2 = createSetting("Bracket 2",  "]");
    public BooleanSetting unf = createSetting("UnfocusedFPS", false);
    public IntegerSetting fps = createSetting("FPS", 60, 10, 200, (Predicate<Integer>) v -> unf.getValue());
    public BooleanSetting fov = createSetting("FOVModifier", false);
    public FloatSetting fovslider = createSetting("FOV", 130f, 30f, 180f, v -> fov.getValue());

    @Override
    public void onUpdate() {
        if(this.isDisabled())
        {
            this.enable();
        }
        Infinity.INSTANCE.commandManager.setClientMessage(getCommandMessage());
        if(fov.getValue())
        {
            mc.gameSettings.fovSetting = fovslider.getValue();
        }
    }
    public String getCommandMessage() {
        return ChatFormat.coloredString(this.commandBracket.getValue(), ChatFormat.Color.DARK_PURPLE) + ChatFormat.coloredString("Infinity", ChatFormat.Color.LIGHT_PURPLE) + ChatFormat.coloredString(this.commandBracket2.getValue(), ChatFormat.Color.DARK_PURPLE);
    }


}
