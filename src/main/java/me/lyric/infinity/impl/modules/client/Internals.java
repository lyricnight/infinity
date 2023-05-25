package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.string.TextColorUtils;

public class Internals extends Module {

    public Setting<TextColorUtils.Color> bracketColor = register(new Setting<>("BracketColor","Colour of the brackets.", TextColorUtils.Color.DARK_PURPLE));
    public Setting<TextColorUtils.Color> commandColor = register(new Setting<>("NameColor","Colour of Infinity's name.", TextColorUtils.Color.LIGHT_PURPLE));
    public Setting<String> commandBracket = register(new Setting<>("Bracket","Symbol to use for the first bracket.", "["));
    public Setting<String> commandBracket2 = register(new Setting<>("Bracket 2","Symbol to use for the 2nd bracket.",  "]"));
    public Setting<Boolean> thread = register(new Setting<>("ThreadModifier", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", false));
    public Setting<Integer> threadCount = register(new Setting<>("Threads", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", 2, 1, 20).withParent(thread));
    public Setting<Boolean> debug = register(new Setting<>("Debug", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", false).withParent(thread));
    public Setting<Boolean> reload = register(new Setting<>("Reload", " ", false).withParent(thread));
    public Setting<Boolean> unf = register(new Setting<>("UnfocusedFPS", "FPS when mc is minimised.", false));
    public Setting<Integer> fps = register(new Setting<>("FPS", "FPS when unfocused.", 60, 10, 200).withParent(unf));



    public Internals()
    {
        super("Internals", "Handles settings that don't fit anywhere else.", Category.CLIENT);
    }
    @Override
    public void onUpdate() {
        Infinity.INSTANCE.commandManager.setClientMessage(getCommandMessage());
        if(debug.getValue())
        {
            ChatUtils.sendMessage(Infinity.INSTANCE.threadManager.num + " ");
        }
        if(reload.getValue())
        {
            Infinity.INSTANCE.threadManager.reload();
            reload.setValue(false);
        }
    }
    public String getCommandMessage() {
        return TextColorUtils.coloredString(this.commandBracket.getValue(), this.bracketColor.getValue()) + TextColorUtils.coloredString("Infinity", this.commandColor.getValue()) + TextColorUtils.coloredString(this.commandBracket2.getValue(), this.bracketColor.getValue());
    }


}
