

package me.lyric.infinity.gui.Csgo.setting;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.gui.Csgo.CsgoGui;
import me.lyric.infinity.gui.Csgo.CsgoSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class CsgoColor extends CsgoSetting implements IGlobals
{
    public static Tessellator tessellator;
    public static BufferBuilder builder;
    ColorSetting setting;
    boolean pickingColor;
    boolean pickingHue;
    boolean pickingAlpha;
    private Color finalColor;
    
    public CsgoColor(ColorSetting setting) {
        super(setting);
        this.pickingColor = false;
        this.pickingHue = false;
        this.pickingAlpha = false;
        this.setting = setting;
        this.finalColor = (Color)setting.getValue();
    }
    
    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }
    
    public static Color getColor(Color color, float alpha) {
        float red = color.getRed() / 255.0f;
        float green = color.getGreen() / 255.0f;
        float blue = color.getBlue() / 255.0f;
        return new Color(red, green, blue, alpha);
    }
    
    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(9);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glColor4f(red, green, blue, 255.0f);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glDisable(3008);
        GL11.glBegin(9);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glEnable(3008);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    public static void drawGradientRect(double leftpos, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0f;
        float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        float f4 = (col1 & 0xFF) / 255.0f;
        float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        float f8 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    
    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        CsgoColor.builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        CsgoColor.builder.pos((double)right, (double)top, 0.0).color((endColor >> 24 & 0xFF) / 255.0f, (endColor >> 16 & 0xFF) / 255.0f, (endColor >> 8 & 0xFF) / 255.0f, (endColor >> 24 & 0xFF) / 255.0f).endVertex();
        CsgoColor.builder.pos((double)left, (double)top, 0.0).color((startColor >> 16 & 0xFF) / 255.0f, (startColor >> 8 & 0xFF) / 255.0f, (startColor & 0xFF) / 255.0f, (startColor >> 24 & 0xFF) / 255.0f).endVertex();
        CsgoColor.builder.pos((double)left, (double)bottom, 0.0).color((startColor >> 16 & 0xFF) / 255.0f, (startColor >> 8 & 0xFF) / 255.0f, (startColor & 0xFF) / 255.0f, (startColor >> 24 & 0xFF) / 255.0f).endVertex();
        CsgoColor.builder.pos((double)right, (double)bottom, 0.0).color((endColor >> 24 & 0xFF) / 255.0f, (endColor >> 16 & 0xFF) / 255.0f, (endColor >> 8 & 0xFF) / 255.0f, (endColor >> 24 & 0xFF) / 255.0f).endVertex();
        CsgoColor.tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public static void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            float startA = (startColor >> 24 & 0xFF) / 255.0f;
            float startR = (startColor >> 16 & 0xFF) / 255.0f;
            float startG = (startColor >> 8 & 0xFF) / 255.0f;
            float startB = (startColor & 0xFF) / 255.0f;
            float endA = (endColor >> 24 & 0xFF) / 255.0f;
            float endR = (endColor >> 16 & 0xFF) / 255.0f;
            float endG = (endColor >> 8 & 0xFF) / 255.0f;
            float endB = (endColor & 0xFF) / 255.0f;
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glShadeModel(7425);
            GL11.glBegin(9);
            GL11.glColor4f(startR, startG, startB, startA);
            GL11.glVertex2f((float)minX, (float)minY);
            GL11.glVertex2f((float)minX, (float)maxY);
            GL11.glColor4f(endR, endG, endB, endA);
            GL11.glVertex2f((float)maxX, (float)maxY);
            GL11.glVertex2f((float)maxX, (float)minY);
            GL11.glEnd();
            GL11.glShadeModel(7424);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
        }
        else {
            drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
        }
    }
    
    public static int gradientColor(int color, int percentage) {
        int r = ((color & 0xFF0000) >> 16) * (100 + percentage) / 100;
        int g = ((color & 0xFF00) >> 8) * (100 + percentage) / 100;
        int b = (color & 0xFF) * (100 + percentage) / 100;
        return new Color(r, g, b).hashCode();
    }
    
    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor, boolean hovered) {
        if (hovered) {
            startColor = gradientColor(startColor, -20);
            endColor = gradientColor(endColor, -20);
        }
        float c = (startColor >> 24 & 0xFF) / 255.0f;
        float c2 = (startColor >> 16 & 0xFF) / 255.0f;
        float c3 = (startColor >> 8 & 0xFF) / 255.0f;
        float c4 = (startColor & 0xFF) / 255.0f;
        float c5 = (endColor >> 24 & 0xFF) / 255.0f;
        float c6 = (endColor >> 16 & 0xFF) / 255.0f;
        float c7 = (endColor >> 8 & 0xFF) / 255.0f;
        float c8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0.0).color(c2, c3, c4, c).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0).color(c2, c3, c4, c).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0).color(c6, c7, c8, c5).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0).color(c6, c7, c8, c5).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtils.rectangle((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + (this.setting.isOpen ? 122 : this.height)), new Color(0, 0, 0, this.isInside(mouseX, mouseY) ? 40 : 20).getRGB());
        RenderUtils.outline((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + (this.setting.isOpen ? 122 : this.height)), new Color(2894892), 1.0f);
        try {
            RenderUtils.rectangle((float)(this.x + this.width - 12), (float)(this.y + 1), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), this.finalColor.getRGB());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        RenderUtils.outline((float)(this.x + this.width - 12), (float)(this.y + 1), (float)(this.x + this.width - 2), (float)(this.y + this.height - 2), Color.BLACK, 0.1f);
        Infinity.infinityFont.drawStringWithShadow(this.setting.getName(), (float)this.x, (float)this.y, -1);
        if (this.setting.isOpen) {
            this.drawPicker(this.setting, this.x, this.y + 15, this.x, this.y + 103, this.x, this.y + 93, mouseX, mouseY);
            RenderUtils.rectangle((float)this.x, (float)(this.y + 111), this.x + 41.0f - 1.0f, (float)(this.y + 121), new Color(0, 0, 0, 20).getRGB());
            RenderUtils.rectangle(this.x + 41.0f + 1.0f, (float)(this.y + 111), (float)(this.x + 93), (float)(this.y + 121), new Color(0, 0, 0, 20).getRGB());
            RenderUtils.outline((float)this.x, (float)(this.y + 111), this.x + 41.0f - 1.0f, (float)(this.y + 121), new Color(2894892), 1.0f);
            RenderUtils.outline(this.x + 41.0f + 1.0f, (float)(this.y + 111), (float)(this.x + 93), (float)(this.y + 121), new Color(2894892), 1.0f);
            Infinity.infinityFont.drawStringWithShadow("Copy", this.x + 20.5f - Infinity.infinityFont.getStringWidth("Copy") / 2.0f, (float)(this.y + 112), this.isInsideCopy(mouseX, mouseY) ? new Color(128, 128, 128, 255).getRGB() : -1);
            Infinity.infinityFont.drawStringWithShadow("Paste", this.x + 61.5f - Infinity.infinityFont.getStringWidth("Paste") / 2.0f, (float)(this.y + 112), this.isInsidePaste(mouseX, mouseY) ? new Color(128, 128, 128, 255).getRGB() : -1);
            this.setting.setValue(this.finalColor);
        }
    }
    
    public void readClipBoard() {
        String string;
        try {
            string = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (IOException | UnsupportedFlavorException ex2) {
            return;
        }
        try {
            String[] color = string.split("-");
            this.setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]), Integer.parseInt(color[3])));
        }
        catch (Exception exception) {
            mc.player.sendMessage(new TextComponentString("Wrong color format" + exception.getLocalizedMessage()));
        }
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && this.isInside(mouseX, mouseY)) {
            this.setting.isOpen = !this.setting.isOpen;
        }
        if (mouseButton == 0 && this.isInsideCopy(mouseX, mouseY) && this.setting.isOpen) {
            String hex = this.finalColor.getRed() + "-" + this.finalColor.getGreen() + "-" + this.finalColor.getBlue() + "-" + this.finalColor.getAlpha();
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            this.mc.player.sendMessage(new TextComponentString("Color has been successfully copied to clipboard!"));
        }
        if (mouseButton == 0 && this.isInsidePaste(mouseX, mouseY) && this.setting.isOpen) {
            this.readClipBoard();
        }
    }
    
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        boolean pickingColor = false;
        this.pickingAlpha = pickingColor;
        this.pickingHue = pickingColor;
        this.pickingColor = pickingColor;
    }
    
    public boolean isInsideCopy(int mouseX, int mouseY) {
        return mouseX > this.x + 1 && mouseX < this.x + 41.0f && mouseY > this.y + 111 && mouseY < this.y + 121;
    }
    
    public boolean isInsidePaste(int mouseX, int mouseY) {
        return mouseX > this.x + 41.0f && mouseX < this.x + 93 && mouseY > this.y + 111 && mouseY < this.y + 121;
    }
    
    public void drawPicker(ColorSetting setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = { 0.0f, 0.0f, 0.0f, 0.0f };
        try {
            color = new float[] { Color.RGBtoHSB((setting.getValue()).getRed(), (setting.getValue()).getGreen(), (setting.getValue()).getBlue(), null)[0], Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), null)[1], Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), null)[2], ((Color)setting.getValue()).getAlpha() / 255.0f };
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        int pickerWidth = this.width - 2;
        int pickerHeight = 78;
        int hueSliderWidth = pickerWidth + 5;
        int hueSliderHeight = 7;
        int alphaSliderHeight = 7;
        if (this.pickingColor && (!Mouse.isButtonDown(0) || !mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))) {
            this.pickingColor = false;
        }
        if (this.pickingHue && (!Mouse.isButtonDown(0) || !mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))) {
            this.pickingHue = false;
        }
        if (this.pickingAlpha && (!Mouse.isButtonDown(0) || !mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))) {
            this.pickingAlpha = false;
        }
        if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) {
            this.pickingColor = true;
        }
        if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY)) {
            this.pickingHue = true;
        }
        if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY)) {
            this.pickingAlpha = true;
        }
        if (this.pickingHue) {
            float restrictedX = (float)Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
            color[0] = (restrictedX - hueSliderX) / hueSliderWidth;
        }
        if (this.pickingAlpha) {
            float restrictedX = (float)Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1.0f - (restrictedX - alphaSliderX) / pickerWidth;
        }
        if (this.pickingColor) {
            float restrictedX = (float)Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float)Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - pickerX) / pickerWidth;
            color[2] = 1.0f - (restrictedY - pickerY) / pickerHeight;
        }
        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);
        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;
        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue);
        RenderUtils.prepareScissor(CsgoGui.x + 245, CsgoGui.y + 30, hueSliderWidth - 5, CsgoGui.height - 36);
        this.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth - 2, hueSliderHeight, color[0]);
        RenderUtils.releaseScissor();
        int cursorX = (int)(pickerX + color[1] * pickerWidth);
        int cursorY = (int)(pickerY + pickerHeight - color[2] * pickerHeight);
        RenderUtils.outline((float)(cursorX - 2), (float)(cursorY - 2), (float)(cursorX + 2), (float)(cursorY + 2), Color.black, 1.0f);
        Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
        this.drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth - 1, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        this.finalColor = getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }
    
    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtils.rectangle((float)x, (float)y, (float)(x + width), (float)(y + 4), -65536);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB(step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((step + 1) / 6.0f, 1.0f, 1.0f);
                drawGradientRect((float)x, y + step * (height / 6.0f), (float)(x + width), y + (step + 1) * (height / 6.0f), previousStep, nextStep, false);
                ++step;
            }
            int sliderMinY = (int)(y + height * hue) - 4;
            RenderUtils.rectangle((float)x, (float)(sliderMinY - 1), (float)(x + width), (float)(sliderMinY + 1), -1);
            RenderUtils.outline((float)x, (float)(sliderMinY - 1), (float)(x + width), (float)(sliderMinY + 1), Color.BLACK, 1.0f);
        }
        else {
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB(step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((step + 1) / 6.0f, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                ++step;
            }
            int sliderMinX = (int)(x + width * hue);
            RenderUtils.rectangle((float)(sliderMinX - 1), (float)y, (float)(sliderMinX + 1), (float)(y + height), -1);
            RenderUtils.outline((float)(sliderMinX - 1), (float)y, (float)(sliderMinX + 1), (float)(y + height), Color.BLACK, 1.0f);
        }
    }
    
    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        drawLeftGradientRect(x, y, x + width + 13, y + height, new Color(red, green, blue, 1.0f).getRGB(), 0);
        int sliderMinX = (int)(x + width - width * alpha);
        RenderUtils.rectangle((float)(sliderMinX - 1), (float)y, (float)(sliderMinX + 1), (float)(y + height), -1);
        RenderUtils.outline((float)(sliderMinX - 1), (float)y, (float)(sliderMinX + 1), (float)(y + height), Color.BLACK, 1.0f);
    }
    
    static {
        CsgoColor.tessellator = Tessellator.getInstance();
        CsgoColor.builder = CsgoColor.tessellator.getBuffer();
    }
}
