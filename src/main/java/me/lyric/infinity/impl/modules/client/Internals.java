package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.string.TextColorUtils;

public class Internals extends Module {

    public Setting<TextColorUtils.Color> bracketColor = register(new Setting<>("BracketColor","Color of the brackets.", TextColorUtils.Color.BLUE));
    public Setting<TextColorUtils.Color> commandColor = register(new Setting<>("NameColor","Color of Infinity's name.", TextColorUtils.Color.BLUE));
    public Setting<String> commandBracket = register(new Setting<>("Bracket","Symbol to use for the first bracket.", "["));
    public Setting<String> commandBracket2 = register(new Setting<>("Bracket 2","Symbol to use for the 2nd bracket.",  "]"));


    public Internals()
    {
        super("Internals", "Handles settings that don't fit anywhere else.", Category.CLIENT);
    }
    @Override
    public void onDisable()
    {
        toggle();
    }
    @Override
    public void onUpdate()
    {
        Infinity.INSTANCE.commandManager.setClientMessage(getCommandMessage());
    }
    public String getCommandMessage() {
        return TextColorUtils.coloredString(this.commandBracket.getValue(), this.bracketColor.getValue()) + TextColorUtils.coloredString("Infinity", this.commandColor.getValue()) + TextColorUtils.coloredString(this.commandBracket2.getValue(), this.bracketColor.getValue());
    }


}
