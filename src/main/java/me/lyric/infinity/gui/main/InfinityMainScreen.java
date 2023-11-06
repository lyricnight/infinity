package me.lyric.infinity.gui.main;

import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InfinityMainScreen extends GuiScreen implements IGlobals {
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/background.jpeg");

    private int x, y;

    private float xOffset, yOffset;

    public void initGui() {
        x = width / 2;
        y = height / 4 + 48;
        buttonList.add(new TextButton(0, x, y + 20, "Singleplayer"));
        buttonList.add(new TextButton(1, x, y + 44, "Multiplayer"));
        buttonList.add(new TextButton(2, x, y + 66, "Settings"));
        buttonList.add(new TextButton(2, x, y + 88, "Exit"));
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        xOffset = -1.0f * (( mouseX -  width / 2.0f) / ( width / 32.0f));
        yOffset = -1.0f * (( mouseY -  height / 2.0f) / ( height / 18.0f));
        x = width / 2;
        y = height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        IGlobals.mc.getTextureManager().bindTexture(resourceLocation);
        drawCompleteImage(-16.0f + xOffset, -9.0f + yOffset, width + 32, height + 18);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(x - IGlobals.mc.fontRenderer.getStringWidth("Singleplayer") / 2, y + 20, IGlobals.mc.fontRenderer.getStringWidth("Singleplayer"), IGlobals.mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
            IGlobals.mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (isHovered(x - IGlobals.mc.fontRenderer.getStringWidth("Multiplayer") / 2, y + 44, IGlobals.mc.fontRenderer.getStringWidth("Multiplayer"), IGlobals.mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
            IGlobals.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (isHovered(x - IGlobals.mc.fontRenderer.getStringWidth("Settings") / 2, y + 66, IGlobals.mc.fontRenderer.getStringWidth("Settings"), IGlobals.mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
            IGlobals.mc.displayGuiScreen(new GuiOptions(this, IGlobals.mc.gameSettings));
        } else if (isHovered(x - IGlobals.mc.fontRenderer.getStringWidth("Exit") / 2, y + 88, IGlobals.mc.fontRenderer.getStringWidth("Exit"), IGlobals.mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
            IGlobals.mc.shutdown();
        }
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }
    public BufferedImage parseBackground(BufferedImage background) {
        int height;
        int width = 1920;
        int srcWidth = background.getWidth();
        int srcHeight = background.getHeight();
        for (height = 1080; width < srcWidth || height < srcHeight; width *= 2, height *= 2) {
        }
        BufferedImage imgNew = new BufferedImage(width, height, 2);
        Graphics g = imgNew.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.dispose();
        return imgNew;
    }
    public void updateScreen() {
        super.updateScreen();
    }
    private static class TextButton extends GuiButton {
        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, IGlobals.mc.fontRenderer.getStringWidth(buttonText), IGlobals.mc.fontRenderer.FONT_HEIGHT, buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                enabled = true;
                hovered = mouseX >= x - mc.fontRenderer.getStringWidth(displayString) / 2.0f && mouseY >= y && mouseX < x + width && mouseY < y + height;
                mc.fontRenderer.drawString(displayString,x - mc.fontRenderer.getStringWidth(displayString) / 2.0f, y, Color.WHITE.getRGB(), true);
                if (hovered) {
                    RenderUtils.drawLine((x - 1) - mc.fontRenderer.getStringWidth(displayString) / 2.0f, y + 2 + mc.fontRenderer.FONT_HEIGHT,x + mc.fontRenderer.getStringWidth(displayString) / 2.0f + 1.0f, y + 2 + mc.fontRenderer.FONT_HEIGHT, 1.0f, Color.WHITE.getRGB());
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return enabled && visible && mouseX >= x - IGlobals.mc.fontRenderer.getStringWidth(displayString) / 2.0f && mouseY >= y && mouseX < x + width && mouseY < y + height;
        }
    }
}
