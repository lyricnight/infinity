package me.lyric.infinity.api.util.gl;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.Color;

import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author
 */

public class RenderUtils implements IGlobals {

    private final static Tessellator tessellator = Tessellator.getInstance();

    private final static BufferBuilder bufferBuilder = tessellator.getBuffer();

    public static void drawBBSlab(AxisAlignedBB bb, double height, Color color) {
        final int r = color.getRed();
        final int g = color.getGreen();
        final int b = color.getBlue();
        final int a = color.getAlpha();
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY + height;
        double maxZ = bb.maxZ;
        pushMatrix();

        disableTexture2D();
        enableBlend();
        disableAlpha();
        glDisable(GL_DEPTH_TEST);
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        disableCull();
        shadeModel(GL_SMOOTH);

        bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        tessellator.draw();

        glEnable(GL_DEPTH_TEST);
        shadeModel(GL_FLAT);
        disableBlend();
        enableCull();
        enableAlpha();
        enableTexture2D();
        popMatrix();
    }

    public static void drawBBOutline(AxisAlignedBB bb, float width, Color start, Color end) {
        disableTexture2D();
        enableBlend();
        disableAlpha();
        disableDepth();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        shadeModel(GL_SMOOTH);
        width(width);

        final int r = start.getRed();
        final int b = start.getBlue();
        final int g = start.getGreen();
        final int a = start.getAlpha();

        final int r0 = end.getRed();
        final int b0 = end.getBlue();
        final int g0 = end.getGreen();
        final int a0 = end.getAlpha();

        bufferBuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vertex(bb.minX, bb.minY, bb.minZ, r, g, b, a);
        vertex(bb.minX, bb.minY, bb.maxZ, r, g, b, a);
        vertex(bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        vertex(bb.maxX, bb.minY, bb.minZ, r, g, b, a);
        vertex(bb.minX, bb.minY, bb.minZ, r, g, b, a);
        vertex(bb.minX, bb.maxY, bb.minZ, r0, g0, b0, a0);
        vertex(bb.minX, bb.maxY, bb.maxZ, r0, g0, b0, a0);
        vertex(bb.minX, bb.minY, bb.maxZ, r, g, b, a);
        vertex(bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        vertex(bb.maxX, bb.maxY, bb.maxZ, r0, g0, b0, a0);
        vertex(bb.minX, bb.maxY, bb.maxZ, r0, g0, b0, a0);
        vertex(bb.maxX, bb.maxY, bb.maxZ, r0, g0, b0, a0);
        vertex(bb.maxX, bb.maxY, bb.minZ, r0, g0, b0, a0);
        vertex(bb.maxX, bb.minY, bb.minZ, r, g, b, a);
        vertex(bb.maxX, bb.maxY, bb.minZ, r0, g0, b0, a0);
        vertex(bb.minX, bb.maxY, bb.minZ, r0, g0, b0, a0);
        tessellator.draw();

        enableDepth();
        shadeModel(GL_FLAT);
        disableBlend();
        enableAlpha();
        enableTexture2D();
    }

    public static void drawBBFill(AxisAlignedBB bb, Color color, Color bottom) {
        pushMatrix();
        disableTexture2D();
        enableBlend();
        disableAlpha();
        disableCull();
        disableDepth();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        shadeModel(GL_SMOOTH);
        width(1);

        final int r = color.getRed();
        final int b = color.getBlue();
        final int g = color.getGreen();
        final int a = color.getAlpha();
        final int r0 = bottom.getRed();
        final int b0 = bottom.getBlue();
        final int g0 = bottom.getGreen();
        final int a0 = bottom.getAlpha();

        bufferBuilder.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY;
        double maxZ = bb.maxZ;
        vertex(minX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(minX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, minY, minZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(maxX, minY, maxZ).color(r0, g0, b0, a0).endVertex();
        vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();

        enableDepth();
        shadeModel(GL_FLAT);
        disableBlend();
        enableCull();
        enableAlpha();
        enableTexture2D();
        popMatrix();
    }

    public static void drawBBClaw(AxisAlignedBB bb, float width, float height, Color color) {
        pushMatrix();
        start();
        width(width);

        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY;
        double maxZ = bb.maxZ;

        bufferBuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, minY, minZ + height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, minY, maxZ - height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, minY, minZ + height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, minY, maxZ - height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX + height, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX + height, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX - height, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX - height, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, minY + height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, minY + height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, minY + height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, minY + height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, maxY, minZ + height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, maxY, maxZ - height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, maxY, minZ + height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, maxY, maxZ - height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX + height, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX + height, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX - height, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX - height, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, maxY - height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(minX, maxY - height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, maxY - height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        vertex(maxX, maxY - height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();

        end();
        popMatrix();
    }

    public static void drawRect(float startX, float startY, float endX, float endY, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(startX, endY, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(endX, endY, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(endX, startY, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(startX, startY, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float lineSize, int color, int borderColor) {
        drawRect(x, y, x2, y2, color);
        drawRect(x, y, x + lineSize, y2, borderColor);
        drawRect(x2 - lineSize, y, x2, y2, borderColor);
        drawRect(x, y2 - lineSize, x2, y2, borderColor);
        drawRect(x, y, x2, y + lineSize, borderColor);
    }

    public static void end() {
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void start() {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    }

    private static BufferBuilder vertex(double x, double y, double z) {
        return bufferBuilder.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
    }

    private static void vertex(double x, double y, double z, int r, int g, int b, int a) {
        bufferBuilder.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ).color(r, g, b, a).endVertex();
    }

    private static void width(float width) {
        GlStateManager.glLineWidth(width);
    }
}
