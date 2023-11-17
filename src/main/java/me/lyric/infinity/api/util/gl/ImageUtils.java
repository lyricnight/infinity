package me.lyric.infinity.api.util.gl;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageUtils implements IGlobals {
    /**
     * Reads the image to a byte buffer that works with LWJGL.
     * @author func16
     */
    public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage){
        int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
        for(int rgb : rgbArray){
            byteBuffer.putInt(rgb << 8 | rgb >> 24 & 255);
        }
        byteBuffer.flip();

        return byteBuffer;
    }

    public static void image(ResourceLocation resourceLocation, int x, int y, int width, int height) {
        GL11.glPushMatrix();
        GlStateManager.enableAlpha();
        mc.getTextureManager().bindTexture(resourceLocation);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GuiScreen.drawScaledCustomSizeModalRect(x, y, 0.0f, 0.0f, width, height, width, height, (float)width, (float)height);
        GlStateManager.disableAlpha();
    }


    /**
     * perhaps a way to get around icons not loading? update - doesn't work properly
     * @param resourceLocation
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void imageGL(ResourceLocation resourceLocation, int x, int y, int width, int height)
    {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, RenderUtils.loadGlTexture(resourceLocation));

        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, width, height, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), width, height);
    }
}
