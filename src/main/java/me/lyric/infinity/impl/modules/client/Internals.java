package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.*;
import me.lyric.infinity.api.util.string.StringUtils;
import me.lyric.infinity.manager.Managers;

import java.util.Arrays;
import java.util.function.Predicate;

@ModuleInformation(name = "Internals", description = "Handles internal settings that don't fit anywhere else.", category = Category.Client)
public class Internals extends Module {

    public ModeSetting bracketColor = createSetting("BracketColor", "Black", Arrays.asList("None", "Black", "DarkGray", "Gray", "DarkBlue", "Blue", "DarkGreen", "Green", "DarkAqua", "Aqua", "DarkRed", "Red", "DarkPurple", "Purple", "Gold", "Yellow"));

    public ModeSetting commandColor = createSetting("NameColor", "DarkGray", Arrays.asList("None", "Black", "DarkGray", "Gray", "DarkBlue", "Blue", "DarkGreen", "Green", "DarkAqua", "Aqua", "DarkRed", "Red", "DarkPurple", "Purple", "Gold", "Yellow"));

    public StringSetting commandBracket = createSetting("Bracket", "[");
    public StringSetting commandBracket2 = createSetting("Bracket 2",  "]");
    public BooleanSetting unf = createSetting("UnfocusedFPS", false);
    public IntegerSetting fps = createSetting("FPS", 60, 10, 200, (Predicate<Integer>) v -> unf.getValue());
    public BooleanSetting fov = createSetting("FOVModifier", false);
    public FloatSetting fovslider = createSetting("FOV", 130f, 30f, 180f, v -> fov.getValue());

    @Override
    public void onUpdate() {
        Managers.COMMANDS.setClientMessage(getCommandMessage());
        if(fov.getValue())
        {
            mc.gameSettings.fovSetting = fovslider.getValue();
        }
    }

    public String getCommandMessage() {
        return StringUtils.coloredString(this.commandBracket.getValue(), bracketColor.getValue()) + StringUtils.coloredString("Infinity", commandColor.getValue()) + StringUtils.coloredString(this.commandBracket2.getValue(), bracketColor.getValue());
    }
}
