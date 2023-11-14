package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CsgoCategory
{
    public ArrayList<CsgoModule> csgoModules;
    public Category category;
    public int x;
    public int y;
    public int width;
    public int height;
    public int deltaY;
    public int scrollingY;
    public boolean allowScale;
    public boolean isIncreasingAlpha;
    public boolean setRight;
    public boolean correctInsideOutline;
    public boolean needsScrollRevertDown;
    public boolean needsScrollRevertUp;
    public float scale;
    public float imageX;
    public float textX;
    public float alpha;
    public float scrollDownRevertValue;
    public float scrollUpRevertValue;
    public float leftHeight;
    public float rightHeight;
    public float bottomWidth;
    public float topWidth;
    
    public CsgoCategory(Category category, int x, int y, int width, int height) {
        this.csgoModules = new ArrayList<>();
        this.scale = 0.0f;
        this.imageX = 0.0f;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textX = x - Infinity.INSTANCE.infinityFont.getStringWidth(category.toString());
        this.alpha = 0.0f;
        if (!this.csgoModules.isEmpty()) {
            this.csgoModules.clear();
        }
        this.deltaY = CsgoGui.y + 10 + this.scrollingY;
        Infinity.INSTANCE.moduleManager.getModulesInCategory(category).forEach(module ->
            csgoModules.add(new CsgoModule(module, x + 127, this.deltaY += 21, x + 242, 20))
        );
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        if (canRender()) {
            setScroll(mouseX, mouseY);
        }
        if (allowScale) {
            scale = AnimationUtils.increaseNumber(scale, 1.0f, (1.0f - scale) / CsgoGui.getAnimationSpeedAccordingly(100));
        }
        GlStateManager.scale(scale, 1.0f, scale);
        if (isIncreasingAlpha) {
            alpha = AnimationUtils.increaseNumber(alpha, 60.0f, (60.0f - alpha) / 15.0f);
        }
        else {
            alpha = AnimationUtils.decreaseNumber(alpha, 0.0f, alpha / 15.0f);
        }
        if (alpha >= 58.0f) {
            isIncreasingAlpha = false;
        }
        if (textX > x + width) {
            textX = x - Infinity.INSTANCE.infinityFont.getStringWidth(category.toString());
        }
        final String name = category.toString();
        int x = (int) (this.x / scale);
        RenderUtils.rectangle((float)x, (float)y, (float)(x + width), (float)(y + height), new Color(3815994).getRGB());
        RenderUtils.outline((float)x, (float)y, (float)(x + width), (float)(y + height), new Color(2894892), 1.0f);
        x = CsgoGui.x + 3;
        if (!correctInsideOutline) {
            leftHeight = (float)(y + height);
            rightHeight = (float)(y + height);
            bottomWidth = (float)x;
            topWidth = (float)x;
            correctInsideOutline = true;
        }
        if (isInside(mouseX, mouseY)) {
            leftHeight = AnimationUtils.decreaseNumber(leftHeight, (float)y, (leftHeight - y) / CsgoGui.getAnimationSpeedAccordingly(25));
            bottomWidth = AnimationUtils.increaseNumber(bottomWidth, (float)(x + width), (x + width - bottomWidth) / CsgoGui.getAnimationSpeedAccordingly(25));
            if (bottomWidth >= x + width - 1) {
                topWidth = AnimationUtils.increaseNumber(topWidth, (float)(x + width), (x + width - topWidth) / CsgoGui.getAnimationSpeedAccordingly(25));
                rightHeight = AnimationUtils.decreaseNumber(rightHeight, (float)y, (rightHeight - y) / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        else {
            topWidth = AnimationUtils.decreaseNumber(topWidth, (float)x, (topWidth - x) / CsgoGui.getAnimationSpeedAccordingly(25));
            rightHeight = AnimationUtils.increaseNumber(rightHeight, (float)(y + height), (y + height - rightHeight) / CsgoGui.getAnimationSpeedAccordingly(25));
            if (topWidth <= x + 1) {
                leftHeight = AnimationUtils.increaseNumber(leftHeight, (float)(y + height), (y + height - leftHeight) / CsgoGui.getAnimationSpeedAccordingly(25));
                bottomWidth = AnimationUtils.decreaseNumber(bottomWidth, (float)x, (bottomWidth - x) / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        RenderUtils.rectangle((float)x, leftHeight, (float)(x + 1), (float)(y + height), (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle((float)x, (float)(y + height - 1), bottomWidth, (float)(y + height), (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle((float)x, (float)y, topWidth, (float)(y + 1), (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle((float)(x + width - 1), rightHeight, (float)(x + width), (float)(y + height), (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        x = (int)(x / scale);
        RenderUtils.rectangle((float)x, (float)y, (float)(x + width), (float)(y + height), new Color(((Color)Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRed() / 255.0f, ((Color)Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getGreen() / 255.0f, ((Color)Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getBlue() / 255.0f, alpha / 255.0f).getRGB());
        if (isInside(mouseX, mouseY)) {
            imageX = AnimationUtils.decreaseNumber(imageX, (float)(x + 1), (imageX - x + 1.0f) / CsgoGui.getAnimationSpeedAccordingly(100));
            if (imageX <= x + 2) {
                final float targetX = x + width / 2.0f - Infinity.INSTANCE.infinityFont.getStringWidth(name) / 2.0f;
                textX = AnimationUtils.increaseNumber(textX, targetX, (targetX - textX) / CsgoGui.getAnimationSpeedAccordingly(100));
            }
        }
        else {
            final float targetX = x - Infinity.INSTANCE.infinityFont.getStringWidth(name);
            textX = AnimationUtils.decreaseNumber(textX, targetX, (textX - targetX) / CsgoGui.getAnimationSpeedAccordingly(100));
            imageX = AnimationUtils.increaseNumber(imageX, x + width / 2.0f - 10.0f, (x + width / 2.0f - 10.0f - imageX) / CsgoGui.getAnimationSpeedAccordingly(100));
        }
        RenderUtils.image(new ResourceLocation("textures/clientrewrite/icons/" + name.toLowerCase() + ".png"), (int)imageX, y + 1, 18, 18);
        RenderUtils.prepareScissor((int)(imageX + 18.0f), y, width, height);
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(name, textX, y + height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, -1);
        RenderUtils.releaseScissor();
        if (CsgoGui.finishedScaling && !setRight) {
            setCsgoModules();
            setRight = true;
        }
        RenderUtils.prepareScissor(CsgoGui.x + 124, CsgoGui.y + 30, CsgoGui.width - 130, CsgoGui.height - 36);
        renderModules(mouseX, mouseY);
        RenderUtils.releaseScissor();
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (canRender()) {
            csgoModules.forEach(csgoModule -> csgoModule.mouseReleased(mouseX, mouseY, state));
        }
    }
    
    public void setScroll(final int mouseX, final int mouseY) {
        if (isInsideModules(mouseX, mouseY)) {
            final int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                scrollingY -= Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
            }
            else if (dWheel > 0) {
                scrollingY += Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
            }
            final int lastModuleY = getLastModuleY();
            if (lastModuleY != -69420) {
                needsScrollRevertDown = (lastModuleY + 20 <= CsgoGui.y + 50);
                scrollDownRevertValue = 0.0f;
            }
            final int firstModuleY = getFirstModuleY();
            if (firstModuleY != -69420) {
                needsScrollRevertUp = (firstModuleY >= CsgoGui.y + CsgoGui.height - 26);
                scrollUpRevertValue = 0.0f;
            }
            if (needsScrollRevertDown) {
                scrollDownRevertValue = AnimationUtils.increaseNumber(scrollDownRevertValue, 3.0f, (3.0f - scrollDownRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100));
                scrollingY += (int)scrollDownRevertValue;
            }
            if (needsScrollRevertUp) {
                scrollUpRevertValue = AnimationUtils.increaseNumber(scrollDownRevertValue, 3.0f, (3.0f - scrollUpRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100));
                scrollingY -= (int)scrollUpRevertValue;
            }
            deltaY = CsgoGui.y + 10 + scrollingY;
            for (final CsgoModule csgoModule2 : csgoModules) {
                final CsgoModule csgoModule = csgoModule2;
                final int n = deltaY + 21;
                deltaY = n;
                csgoModule2.y = n;
            }
        }
    }
    
    public int getLastModuleY() {
        TreeMap<Integer, CsgoModule> moduleTreeMap = this.csgoModules.stream().collect(Collectors.toMap(csgoModule -> csgoModule.y, csgoModule -> csgoModule, (a, b) -> b, TreeMap::new));
        if (!moduleTreeMap.isEmpty()) {
            return (moduleTreeMap.lastEntry().getValue()).y;
        }
        return -69420;
    }
    
    public int getFirstModuleY() {
        TreeMap<Integer, CsgoModule> moduleTreeMap = this.csgoModules.stream().collect(Collectors.toMap(csgoModule -> csgoModule.y, csgoModule -> csgoModule, (a, b) -> b, TreeMap::new));
        if (!moduleTreeMap.isEmpty()) {
            return (moduleTreeMap.firstEntry().getValue()).y;
        }
        return -69420;
    }
    
    public boolean isInsideModules(final int mouseX, final int mouseY) {
        return mouseX > CsgoGui.x + 124 && mouseX < CsgoGui.x + 244 && mouseY > CsgoGui.y + 30 && mouseY < CsgoGui.y + CsgoGui.height;
    }
    
    public void renderModules(final int mouseX, final int mouseY) {
        if (canRender()) {
            csgoModules.forEach(csgoModule -> csgoModule.drawScreen(mouseX, mouseY));
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            if (!CsgoGui.category.equals(category)) {
                setCsgoModules();
                CsgoGui.category = category;
            }
            isIncreasingAlpha = true;
        }
        if (canRender()) {
            csgoModules.forEach(csgoModule -> csgoModule.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }
    
    public void setCsgoModules() {
        if (!csgoModules.isEmpty()) {
            csgoModules.clear();
        }
        deltaY = CsgoGui.y + 10 + scrollingY;
        Infinity.INSTANCE.moduleManager.getModulesInCategory(category).forEach(module ->
            csgoModules.add(new CsgoModule(module, CsgoGui.x + 125, deltaY + 21, 116, 20))
        );
    }
    
    public void keyTyped(final char typedChar, final int keyCode) {
        if (canRender()) {
            csgoModules.forEach(csgoModule -> csgoModule.keyTyped(typedChar, keyCode));
        }
    }
    
    public boolean isInside(final int mouseX, final int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
    
    public boolean canRender() {
        return CsgoGui.category.equals(category);
    }
}
