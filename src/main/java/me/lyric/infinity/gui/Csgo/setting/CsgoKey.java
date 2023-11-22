package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.KeySetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class CsgoKey extends CsgoSetting
{
    KeySetting setting;
    String value;
    String settingString;
    Timer timer;
    
    public CsgoKey(final KeySetting setting) {
        super(setting);
        this.timer = new Timer();
        this.setting = setting;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        final String name = this.setting.getName();
        Infinity.infinityFont.drawStringWithShadow(name, (float)(this.x + 1), this.y + this.height / 2.0f - Infinity.infinityFont.getHeight(name) / 2.0f, -1);
        final String settingValue = Keyboard.getKeyName(this.setting.getValue());
        this.value = (settingValue.equals("NONE") ? "None" : settingValue);
        this.settingString = "Key: " + (this.setting.isOpen ? this.getTypingIcon() : this.value);
        RenderUtils.rectangle(this.x + this.width - 4 - Infinity.infinityFont.getStringWidth(this.settingString), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(0, 0, 0, this.isInsideBox(mouseX, mouseY) ? 40 : 20).getRGB());
        RenderUtils.outline(this.x + this.width - 4 - Infinity.infinityFont.getStringWidth(this.settingString), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(2894892), 1.0f);
        Infinity.infinityFont.drawStringWithShadow(this.settingString, this.x + this.width - 4 - Infinity.infinityFont.getStringWidth(this.settingString) + (Infinity.infinityFont.getStringWidth(this.settingString) + 2.0f) / 2.0f - Infinity.infinityFont.getStringWidth(this.settingString) / 2.0f, this.y + this.height / 2.0f - Infinity.infinityFont.getHeight(this.settingString) / 2.0f, -1);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isInside(mouseX, mouseY)) {
            this.setting.isOpen = !this.setting.isOpen;
        }
    }
    
    public void keyTyped(final char typedChar, final int keyCode) {
        if (this.setting.isOpen) {
            if (keyCode == 211 || keyCode == 1) {
                this.setting.setValue(0);
            }
            else {
                this.setting.setValue(keyCode);
            }
            this.setting.isOpen = !this.setting.isOpen;
        }
    }
    
    public boolean isInsideBox(final int mouseX, final int mouseY) {
        return mouseX > this.x + this.width - 4 - Infinity.infinityFont.getStringWidth(this.settingString) && mouseX < this.x + this.width - 2 && mouseY > this.y + 4 && mouseY < this.y + this.height - 4;
    }
    
    public String getTypingIcon() {
        if (this.timer.passedMs(1000L)) {
            this.timer.reset();
            return "";
        }
        if (this.timer.passedMs(500L)) {
            return "_";
        }
        return "";
    }
}
