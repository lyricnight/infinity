package me.lyric.infinity.impl.modules.client;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.gui.Csgo.CsgoGui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

@ModuleInformation(name = "ClickGUI", description = "what do you think", category = Category.Client)
public class ClickGUI extends Module {
    public IntegerSetting scrollSpeed = createSetting("ScrollSpeed", 5, 1, 20);

    public FloatSetting animationSpeed = createSetting("AnimationSpeed", 5.0f, 0.0f, 25.0f);

    public ColorSetting color = createSetting("Colour", new Color(255, 0, 0));

    public ClickGUI()
    {
        this.bind.setValue(54);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen((GuiScreen) new CsgoGui());
    }

    @Override
    public void onUpdate() {
        if (!(this.mc.currentScreen instanceof CsgoGui)) {
            this.disable();
        }
    }

}
