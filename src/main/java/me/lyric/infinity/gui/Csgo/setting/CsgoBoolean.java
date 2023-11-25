package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import me.lyric.infinity.manager.Managers;

import java.awt.*;

public class CsgoBoolean extends CsgoSetting
{
    BooleanSetting setting;
    
    public CsgoBoolean(BooleanSetting setting) {
        super(setting);
        this.setting = setting;
    }
    
    public void drawScreen(int mouseX, int mouseY) {
        String name = this.setting.getName();
        Infinity.infinityFont.drawStringWithShadow(name, (float)(this.x + 1), this.y + this.height / 2.0f - Infinity.infinityFont.getHeight(name) / 2.0f, -1);
        RenderUtils.rectangle((float)(this.x + this.width - 18), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(0, 0, 0, this.isInsideBox(mouseX, mouseY) ? 40 : 20).getRGB());
        String value = this.setting.getValue() ? "On" : "Off";
        Infinity.infinityFont.drawStringWithShadow(value, this.x + this.width - 10.0f - Infinity.infinityFont.getStringWidth(value) / 2.0f, this.y + 2 + (this.height - 2) / 2.0f - Infinity.infinityFont.getHeight(name) / 2.0f, (this.setting.getValue()) ? (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB() : -1);
        RenderUtils.outline((float)(this.x + this.width - 18), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(2894892), 1.0f);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        if (this.isInsideBox(mouseX, mouseY) && clickedButton == 0) {
            this.setting.setValue(!this.setting.getValue());
        }
    }
    
    public boolean isInsideBox(final int mouseX, final int mouseY) {
        return mouseX > this.x + this.width - 18 && mouseX < this.x + this.width - 2 && mouseY > this.y + 4 && mouseY < this.y + this.height - 4;
    }
}
