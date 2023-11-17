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

    public static void image(ResourceLocation image, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(image);
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
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
