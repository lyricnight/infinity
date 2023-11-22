package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.gui.Csgo.CsgoGui;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import me.lyric.infinity.manager.Managers;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CsgoInteger extends CsgoSetting
{
    public int extension;
    public float indicator;
    IntegerSetting setting;
    
    public CsgoInteger(final IntegerSetting setting) {
        super(setting);
        this.setting = setting;
        this.extension = 0;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        this.dragSlider(mouseX, mouseY);
        final int x = this.x - 1;
        RenderUtils.rectangle((float)(x + 1), (float)(this.y + 1), (float)(x + this.width - 1), (float)(this.y + this.height - 1), new Color(0, 0, 0, this.isInside(mouseX, mouseY) ? 40 : 20).getRGB());
        RenderUtils.outline((float)(x + 1), (float)(this.y + 1), (float)(x + this.width - 1), (float)(this.y + this.height - 1), new Color(2894892), 1.0f);
        final float sliderWidth = this.width * this.sliderWidthValue();
        if (this.indicator < sliderWidth) {
            this.indicator = AnimationUtils.increaseNumber(this.indicator, sliderWidth, (sliderWidth - this.indicator) / CsgoGui.getAnimationSpeedAccordingly(50));
        }
        else if (this.indicator > sliderWidth) {
            this.indicator = AnimationUtils.decreaseNumber(this.indicator, sliderWidth, (this.indicator - sliderWidth) / CsgoGui.getAnimationSpeedAccordingly(50));
        }
        RenderUtils.rectangle(x + this.indicator - 1.0f, (float)this.y, x + this.indicator + 1.0f, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        final String string = this.setting.getName() + ": " + this.setting.getValue();
        Infinity.infinityFont.drawStringWithShadow(string, x + this.width / 2.0f - Infinity.infinityFont.getStringWidth(string) / 2.0f, this.y + this.height / 2.0f - Infinity.infinityFont.getHeight(string) / 2.0f, -1);
    }
    
    public float sliderWidthValue() {
        return (this.setting.getValue() - (float)this.setting.getMinimum()) / (this.setting.getMaximum() - this.setting.getMinimum());
    }
    
    public void dragSlider(final int mouseX, final int mouseY) {
        if (this.isInsideExtended(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSliderValue(mouseX);
            this.extension = 400;
        }
        else {
            this.extension = 0;
        }
    }
    
    public boolean isInsideExtended(final int mouseX, final int mouseY) {
        return mouseX > this.x - this.extension && mouseX < this.x + this.width + this.extension && mouseY > this.y - this.extension && mouseY < this.y + this.height + this.extension;
    }
    
    public void setSliderValue(final int mouseX) {
        this.setting.setValue(this.setting.getMinimum());
        final float diff = (float)Math.min(this.width, Math.max(0, mouseX - this.x));
        final float min = (float)this.setting.getMinimum();
        final float max = (float)this.setting.getMaximum();
        if (diff == 0.0f) {
            this.setting.setValue(this.setting.getMinimum());
        }
        else {
            final float value = this.roundNumber(diff / this.width * (max - min) + min, 1);
            this.setting.setValue((int)value);
        }
    }
    
    public float roundNumber(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }
}
