package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.gui.Csgo.CsgoGui;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import me.lyric.infinity.impl.modules.client.ClickGUI;

import java.awt.*;

public class CsgoMode extends CsgoSetting
{
    public ModeSetting setting;
    public int index;
    public boolean isOpened;
    public float extendedAmount;
    
    public CsgoMode(final ModeSetting setting) {
        super(setting);
        this.setting = setting;
        this.index = setting.modes.indexOf(setting.getValue());
        this.isOpened = false;
        this.extendedAmount = 0.0f;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        RenderUtils.rectangle((float)(this.x + 1), (float)(this.y + 1), (float)(this.x + this.width - 1), (float)(this.y + this.height - 1), new Color(0, 0, 0, this.isInside(mouseX, mouseY) ? 40 : 20).getRGB());
        RenderUtils.outline((float)(this.x + 1), (float)(this.y + 1), (float)(this.x + this.width - 1), (float)(this.y + this.height - 1), new Color(2894892), 1.0f);
        RenderUtils.drawArrow((float)(this.x + this.width - 10), this.y + this.height / 2.0f + 2.0f, 5.0f, 1.0f, 20.0f, 1.0f);
        final String name = this.setting.getName() + ":";
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(name, (float)(this.x + 1), this.y + this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, -1);
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(this.setting.getValue(), this.x + Infinity.INSTANCE.infinityFont.getStringWidth(name + " ") + 1.0f, this.y + this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        final int j = this.setting.modes.stream().mapToInt(string -> this.height + 1).sum();
        if (this.isOpened) {
            this.extendedAmount = AnimationUtils.increaseNumber(this.extendedAmount, (float)(j + 5), (j + 5 - this.extendedAmount) / CsgoGui.getAnimationSpeedAccordingly(100));
        }
        else {
            this.extendedAmount = AnimationUtils.decreaseNumber(this.extendedAmount, 0.0f, this.extendedAmount / CsgoGui.getAnimationSpeedAccordingly(25));
        }
        if (this.extendedAmount > 0.1f) {
            final boolean isOverLimit = this.y + this.height + this.extendedAmount > CsgoGui.y + CsgoGui.height - 6;
            RenderUtils.prepareScissor(this.x, isOverLimit ? CsgoGui.y : (this.y + this.height), this.width, isOverLimit ? (CsgoGui.height - 6) : ((int)this.extendedAmount));
            RenderUtils.outline((float)(this.x + 1), (float)(this.y + this.height), (float)(this.x + this.width - 1), (float)(this.y + j + this.height + 1), new Color(2302755), 1.0f);
            int i = this.y;
            for (final String string : this.setting.modes) {
                i += this.height + 1;
                final boolean inside = mouseX > this.x + 1 && mouseX < this.x + this.width - 1 && mouseY > i && mouseY < i + this.height;
                RenderUtils.rectangle((float)(this.x + 2), (float)i, (float)(this.x + this.width - 2), (float)(i + this.height), new Color(0, 0, 0, inside ? 40 : 20).getRGB());
                RenderUtils.outline((float)(this.x + 2), (float)i, (float)(this.x + this.width - 2), (float)(i + this.height), new Color(2894892), 1.0f);
                Infinity.INSTANCE.infinityFont.drawStringWithShadow(string, (float)(this.x + 3), i + this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(string) / 2.0f, (this.setting.getValue()).equals(string) ? (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB() : -1);
            }
            RenderUtils.releaseScissor();
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        if (this.isInside(mouseX, mouseY) && clickedButton == 0) {
            this.isOpened = !this.isOpened;
        }
        if (clickedButton == 0 && this.isOpened) {
            int i = this.y;
            for (final String string : this.setting.modes) {
                i += this.height + 1;
                final boolean inside = mouseX > this.x + 1 && mouseX < this.x + this.width - 1 && mouseY > i && mouseY < i + this.height;
                if (inside) {
                    this.setting.setValue(string);
                    this.isOpened = false;
                }
            }
        }
    }
}
