package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.StringSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class CsgoString extends CsgoSetting
{
    StringSetting setting;
    String settingString;
    Timer timer;
    
    public CsgoString(final StringSetting setting) {
        super(setting);
        this.timer = new Timer();
        this.setting = setting;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        final String name = this.setting.getName();
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(name, (float)(this.x + 1), this.y + this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, -1);
        this.settingString = (this.setting.isOpen ? (this.setting.getValue() + this.getTypingIcon()) : this.setting.getValue());
        RenderUtils.rectangle(this.x + this.width - 4 - Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(0, 0, 0, this.isInsideBox(mouseX, mouseY) ? 40 : 20).getRGB());
        RenderUtils.outline(this.x + this.width - 4 - Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString), (float)(this.y + 2), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), new Color(2894892), 1.0f);
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(this.settingString, this.x + this.width - 4 - Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString) + (Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString) + 2.0f) / 2.0f - Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString) / 2.0f, this.y + this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(this.settingString) / 2.0f, -1);
    }
    
    public boolean isInsideBox(final int mouseX, final int mouseY) {
        return mouseX > this.x + this.width - 4 - Infinity.INSTANCE.infinityFont.getStringWidth(this.settingString) && mouseX < this.x + this.width - 2 && mouseY > this.y + 4 && mouseY < this.y + this.height - 4;
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.isInsideBox(mouseX, mouseY) && mouseButton == 0) {
            this.setting.isOpen = !this.setting.isOpen;
        }
    }
    
    public void keyTyped(final char typedChar, final int keyCode) {
        if (!this.setting.isOpen) {
            return;
        }
        if (keyCode == 14) {
            if (Keyboard.isKeyDown(29)) {
                this.setting.setValue("");
            }
            if (this.setting.getValue() != null && this.setting.getValue().length() > 0) {
                this.setting.setValue(this.setting.getValue().substring(0, this.setting.getValue().length() - 1));
            }
        }
        else if (keyCode == 28 || keyCode == 27) {
            this.setting.isOpen = false;
        }
        else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            this.setting.setValue(this.setting.getValue() + "" + typedChar);
        }
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
