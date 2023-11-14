package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.string.ChatFormat;

@ModuleInformation(getName = "Internals", getDescription = "Handles internal settings that don't fit anywhere else.", category = Category.Client)
public class Internals extends Module {

    public Setting<ChatFormat.Color> bracketColor = register(new Setting<>("BracketColor","Colour of the brackets.", ChatFormat.Color.DARK_PURPLE));
    public Setting<ChatFormat.Color> commandColor = register(new Setting<>("NameColor","Colour of Infinity's name.", ChatFormat.Color.LIGHT_PURPLE));
    public Setting<String> commandBracket = register(new Setting<>("Bracket","Symbol to use for the first bracket.", "["));
    public Setting<String> commandBracket2 = register(new Setting<>("Bracket 2","Symbol to use for the 2nd bracket.",  "]"));
    public Setting<Boolean> thread = register(new Setting<>("ThreadModifier", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", false));
    public Setting<Integer> threadCount = register(new Setting<>("Threads", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", 2, 1, 20).withParent(thread));
    public Setting<Boolean> debug = register(new Setting<>("Debug", "THIS IS EXPERIMENTAL - 2 IS RECOMMENDED", false).withParent(thread));
    public Setting<Boolean> reload = register(new Setting<>("Reload", " ", false).withParent(thread));
    public Setting<Boolean> unf = register(new Setting<>("UnfocusedFPS", "FPS when mc is minimised.", false));
    public Setting<Integer> fps = register(new Setting<>("FPS", "FPS when unfocused.", 60, 10, 200).withParent(unf));
    public Setting<Boolean> fov = register(new Setting<>("FOVModifier", "Whether to modify fov or not.", false));
    public Setting<Float> fovslider = register(new Setting<>("FOV", "FOV to set to.", 130f, 30f, 180f).withParent(fov));

    @Override
    public void onUpdate() {
        if(this.isDisabled())
        {
            this.toggle();
        }
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
        if(fov.getValue())
        {
            mc.gameSettings.fovSetting = fovslider.getValue();
        }
    }
    public String getCommandMessage() {
        return ChatFormat.coloredString(this.commandBracket.getValue(), this.bracketColor.getValue()) + ChatFormat.coloredString("Infinity", this.commandColor.getValue()) + ChatFormat.coloredString(this.commandBracket2.getValue(), this.bracketColor.getValue());
    }


}
