package me.lyric.infinity.api.setting.settings;

import java.awt.*;

/**
 * @author zzurio
 */

public class ColorPicker {

    private float saturation;
    private float brightness;

    private Color color;
    private boolean isRGB;
    //private boolean alpha;

    public ColorPicker(Color color) {
        this.color = color;
        this.updateSB();
    }

    public void updateSB() {
        final float[] hsb = this.toHSB();

        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    public void setRGB() {
        this.isRGB = true;
    }

    public void unsetRGB() {
        this.isRGB = false;
    }

    /*public void toggleRGB() {
        if (this.isRGB()) {
            this.unsetRGB();
        } else {
            this.setRGB();
        }
    }*/

    public boolean isRGB() {
        return isRGB;
    }

    /*public boolean getAlpha() {
        return alpha;
    }*/

    public Color getColor(int alpha) {
        if (this.isRGB()) {
            return this.getCycleColors(alpha);
        }

        return new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), alpha);
    }

    public Color getCycleColors(int alpha) {
        float[] currentSystemCycle = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32f)
        };

        //int currentColorCycle = Color.HSBtoRGB(currentSystemCycle[0], this.getSaturation(), this.getBrightness());
        int currentColorCycle = Color.HSBtoRGB(currentSystemCycle[0], toHSB()[1], toHSB()[2]);

        return new Color(((currentColorCycle >> 16) & 0xFF), ((currentColorCycle >> 8) & 0xFF), (currentColorCycle & 0xFF), alpha);
    }

    public Color getColor() {
        if (this.isRGB()) {
            return this.getCycleColors(this.color.getAlpha());
        }

        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getRawColor() {
        return color;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float[] toHSB() {
        return Color.RGBtoHSB(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), null);
    }
}
