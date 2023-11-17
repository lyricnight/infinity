package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.ImageUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CsgoCategory {
    public ArrayList<CsgoModule> csgoModules = new ArrayList<>();
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
    public float scale = 0.0f;
    public float imageX = 0.0f;
    public float textX;
    public float alpha;
    public float scrollDownRevertValue;
    public float scrollUpRevertValue;
    public float leftHeight;
    public float rightHeight;
    public float bottomWidth;
    public float topWidth;

    public CsgoCategory(Category category, int x, int y, int width, int height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textX = (float)x - Infinity.INSTANCE.infinityFont.getStringWidth(category.toString());
        this.alpha = 0.0f;
        if (!this.csgoModules.isEmpty()) {
            this.csgoModules.clear();
        }
        this.deltaY = CsgoGui.y + 10 + this.scrollingY;
        Infinity.INSTANCE.moduleManager.getModulesInCategory(category).forEach(module -> this.csgoModules.add(new CsgoModule(module, x + 127, this.deltaY += 21, x + 242, 20)));
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (this.canRender()) {
            this.setScroll(mouseX, mouseY);
        }
        if (this.allowScale) {
            this.scale = AnimationUtils.increaseNumber(this.scale, 1.0f, (1.0f - this.scale) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
        }
        GlStateManager.scale(this.scale, 1.0f, this.scale);
        this.alpha = this.isIncreasingAlpha ? AnimationUtils.increaseNumber(this.alpha, 60.0f, (60.0f - this.alpha) / 15.0f).floatValue() : AnimationUtils.decreaseNumber(this.alpha, 0.0f, this.alpha / 15.0f).floatValue();
        if (this.alpha >= 58.0f) {
            this.isIncreasingAlpha = false;
        }
        if (this.textX > (float)(this.x + this.width)) {
            this.textX = (float)this.x - Infinity.INSTANCE.infinityFont.getStringWidth(this.category.toString());
        }
        String name = this.category.toString();
        int x = (int)((float)this.x / this.scale);
        RenderUtils.rectangle(x, this.y, x + this.width, this.y + this.height, new Color(0x3A3A3A).getRGB());
        RenderUtils.outline(x, this.y, x + this.width, this.y + this.height, new Color(0x2C2C2C), 1.0f);
        x = CsgoGui.x + 3;
        if (!this.correctInsideOutline) {
            this.leftHeight = this.y + this.height;
            this.rightHeight = this.y + this.height;
            this.bottomWidth = x;
            this.topWidth = x;
            this.correctInsideOutline = true;
        }
        if (this.isInside(mouseX, mouseY)) {
            this.leftHeight = AnimationUtils.decreaseNumber(this.leftHeight, (float)this.y, (this.leftHeight - (float)this.y) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            this.bottomWidth = AnimationUtils.increaseNumber(this.bottomWidth, (float)(x + this.width), ((float)(x + this.width) - this.bottomWidth) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            if (this.bottomWidth >= (float)(x + this.width - 1)) {
                this.topWidth = AnimationUtils.increaseNumber(this.topWidth, (float)(x + this.width), ((float)(x + this.width) - this.topWidth) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
                this.rightHeight = AnimationUtils.decreaseNumber(this.rightHeight, (float)this.y, (this.rightHeight - (float)this.y) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            }
        } else {
            this.topWidth = AnimationUtils.decreaseNumber(this.topWidth, (float)x, (this.topWidth - (float)x) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            this.rightHeight = AnimationUtils.increaseNumber(this.rightHeight, (float)(this.y + this.height), ((float)(this.y + this.height) - this.rightHeight) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            if (this.topWidth <= (float)(x + 1)) {
                this.leftHeight = AnimationUtils.increaseNumber(this.leftHeight, (float)(this.y + this.height), ((float)(this.y + this.height) - this.leftHeight) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
                this.bottomWidth = AnimationUtils.decreaseNumber(this.bottomWidth, (float)x, (this.bottomWidth - (float)x) / CsgoGui.getAnimationSpeedAccordingly(25)).floatValue();
            }
        }
        RenderUtils.rectangle(x, this.leftHeight, x + 1, this.y + this.height, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getRGB()));
        RenderUtils.rectangle(x, this.y + this.height - 1, this.bottomWidth, this.y + this.height, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getRGB()));
        RenderUtils.rectangle(x, this.y, this.topWidth, this.y + 1, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getRGB()));
        RenderUtils.rectangle(x + this.width - 1, this.rightHeight, x + this.width, this.y + this.height, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getRGB()));
        x = (int)((float)this.x / this.scale);
        RenderUtils.rectangle(x, this.y, x + this.width, this.y + this.height, new Color((float)(Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getRed()) / 255.0f, (float)Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getGreen() / 255.0f, (float)Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue().getBlue() / 255.0f, this.alpha / 255.0f).getRGB());
        if (this.isInside(mouseX, mouseY)) {
            this.imageX = AnimationUtils.decreaseNumber(this.imageX, (float)(x + 1), (this.imageX - (float)x + 1.0f) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            if (this.imageX <= (float)(x + 2)) {
                float targetX = (float)x + (float)this.width / 2.0f - Infinity.INSTANCE.infinityFont.getStringWidth(name) / 2.0f;
                this.textX = AnimationUtils.increaseNumber(this.textX, targetX, (targetX - this.textX) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            }
        } else {
            float targetX = (float)x - Infinity.INSTANCE.infinityFont.getStringWidth(name);
            this.textX = AnimationUtils.decreaseNumber(this.textX, targetX, (this.textX - targetX) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            this.imageX = AnimationUtils.increaseNumber(this.imageX, (float)x + (float)this.width / 2.0f - 10.0f, ((float)x + (float)this.width / 2.0f - 10.0f - this.imageX) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
        }
        ImageUtils.image(new ResourceLocation("infinity/textures/icons/" + name.toLowerCase() + ".png"), (int)this.imageX, this.y + 1, 18, 18);
        RenderUtils.prepareScissor((int)(this.imageX + 18.0f), this.y, this.width, this.height);
        Infinity.INSTANCE.infinityFont.drawStringWithShadow(name, this.textX, (float)this.y + (float)this.height / 2.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, -1);
        RenderUtils.releaseScissor();
        if (CsgoGui.finishedScaling && !this.setRight) {
            this.setCsgoModules();
            this.setRight = true;
        }
        RenderUtils.prepareScissor(CsgoGui.x + 124, CsgoGui.y + 30, CsgoGui.width - 130, CsgoGui.height - 36);
        this.renderModules(mouseX, mouseY);
        RenderUtils.releaseScissor();
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (!this.canRender()) return;
        this.csgoModules.forEach(csgoModule -> csgoModule.mouseReleased(mouseX, mouseY, state));
    }

    public void setScroll(int mouseX, int mouseY) {
        int firstModuleY;
        if (!this.isInsideModules(mouseX, mouseY)) return;
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.scrollingY -= Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
        } else if (dWheel > 0) {
            this.scrollingY += Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
        }
        int lastModuleY = this.getLastModuleY();
        if (lastModuleY != -69420) {
            this.needsScrollRevertDown = lastModuleY + 20 <= CsgoGui.y + 50;
            this.scrollDownRevertValue = 0.0f;
        }
        if ((firstModuleY = this.getFirstModuleY()) != -69420) {
            this.needsScrollRevertUp = firstModuleY >= CsgoGui.y + CsgoGui.height - 26;
            this.scrollUpRevertValue = 0.0f;
        }
        if (this.needsScrollRevertDown) {
            this.scrollDownRevertValue = AnimationUtils.increaseNumber(this.scrollDownRevertValue, 3.0f, (3.0f - this.scrollDownRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            this.scrollingY = (int)((float)this.scrollingY + this.scrollDownRevertValue);
        }
        if (this.needsScrollRevertUp) {
            this.scrollUpRevertValue = AnimationUtils.increaseNumber(this.scrollDownRevertValue, 3.0f, (3.0f - this.scrollUpRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            this.scrollingY = (int)((float)this.scrollingY - this.scrollUpRevertValue);
        }
        this.deltaY = CsgoGui.y + 10 + this.scrollingY;
        for (CsgoModule csgoModule : this.csgoModules) {
            csgoModule.y = this.deltaY += 21;
        }
    }

    public int getLastModuleY() {
        TreeMap<Integer, CsgoModule> moduleTreeMap = this.csgoModules.stream().collect(Collectors.toMap(csgoModule -> csgoModule.y, csgoModule -> csgoModule, (a, b) -> b, TreeMap::new));
        if (moduleTreeMap.isEmpty()) return -69420;
        return (moduleTreeMap.lastEntry().getValue()).y;
    }

    public int getFirstModuleY() {
        TreeMap<Integer, CsgoModule> moduleTreeMap = this.csgoModules.stream().collect(Collectors.toMap(csgoModule -> csgoModule.y, csgoModule -> csgoModule, (a, b) -> b, TreeMap::new));
        if (moduleTreeMap.isEmpty()) return -69420;
        return (moduleTreeMap.firstEntry().getValue()).y;
    }

    public boolean isInsideModules(int mouseX, int mouseY) {
        return mouseX > CsgoGui.x + 124 && mouseX < CsgoGui.x + 244 && mouseY > CsgoGui.y + 30 && mouseY < CsgoGui.y + CsgoGui.height;
    }

    public void renderModules(int mouseX, int mouseY) {
        if (!this.canRender()) return;
        this.csgoModules.forEach(csgoModule -> csgoModule.drawScreen(mouseX, mouseY));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isInside(mouseX, mouseY) && mouseButton == 0) {
            if (!CsgoGui.category.equals(this.category)) {
                this.setCsgoModules();
                CsgoGui.category = this.category;
            }
            this.isIncreasingAlpha = true;
        }
        if (!this.canRender()) return;
        this.csgoModules.forEach(csgoModule -> csgoModule.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void setCsgoModules() {
        if (!this.csgoModules.isEmpty()) {
            this.csgoModules.clear();
        }
        this.deltaY = CsgoGui.y + 10 + this.scrollingY;
        Infinity.INSTANCE.moduleManager.getModulesInCategory(this.category).forEach(module -> this.csgoModules.add(new CsgoModule(module, CsgoGui.x + 125, this.deltaY += 21, 116, 20)));
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!this.canRender()) return;
        this.csgoModules.forEach(csgoModule -> csgoModule.keyTyped(typedChar, keyCode));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    public boolean canRender() {
        return CsgoGui.category.equals(this.category);
    }
}