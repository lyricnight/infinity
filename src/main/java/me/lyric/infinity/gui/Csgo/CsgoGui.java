package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.ImageUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import me.lyric.infinity.manager.Managers;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CsgoGui extends GuiScreen {
    public static int x = 0;
    public static int y = 0;
    public static int width = 472;
    public static int height = 183;
    public static int deltaY;
    public static float scaleH;
    public static float scaleY;
    public static float currentCategoryImageXDiff;
    public static float currentModuleDif;
    public static boolean finishedScaling;
    public static Category category;
    public static Module module;
    public ArrayList<CsgoCategory> csgoCategories = new ArrayList<>();
    public boolean corrected;

    public CsgoGui() {
        scaleH = 0.0f;
        scaleY = 0.1f;
        deltaY = 0;
        Managers.MODULES.getCategories().forEach(category -> this.csgoCategories.add(new CsgoCategory((category), x + 1, deltaY += 22, 120, 20)));
        CsgoGui.category = Category.Combat;
        module = null;
    }

    public static int getXByModule(Module module) {
        Module module1;
        float x = (float)CsgoGui.x / scaleH;
        int i = (int)(x + 264.0f + currentModuleDif);
        Iterator<Module> iterator = Managers.MODULES.getModulesInCategory(category).iterator();
        do {
            if (!iterator.hasNext()) return -69420;
            module1 = iterator.next();
            i += 300;
        } while (!module.equals(module1));
        return i - 290;
    }

    public static float getAnimationSpeedAccordingly(int original) {
        return (float)original / (25.0f / (26.0f - Managers.MODULES.getModuleByClass(ClickGUI.class).scrollSpeed.getValue().floatValue()));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if (!this.corrected) {
            x = scaledResolution.getScaledWidth() / 2 - width / 2;
            y = scaledResolution.getScaledHeight() / 2 - height / 2;
            this.corrected = true;
        }
        if ((scaleH = AnimationUtils.increaseNumber(scaleH, 1.0f, (1.0f - scaleH) / CsgoGui.getAnimationSpeedAccordingly(10)).floatValue()) >= 0.9f && (scaleY = AnimationUtils.increaseNumber(scaleY, 1.0f, (1.0f - scaleY) / CsgoGui.getAnimationSpeedAccordingly(50)).floatValue()) >= 0.9f) {
            this.csgoCategories.forEach(csgoCategory -> {
                csgoCategory.allowScale = true;
            });
            finishedScaling = true;
        }
        GlStateManager.scale(scaleH, scaleY, scaleH);
        float x = (float)CsgoGui.x / scaleH;
        float y = (float)CsgoGui.y / scaleY;
        deltaY = (int)(y + 5.0f);
        this.csgoCategories.forEach(csgoCategory -> {
            csgoCategory.x = (int)(x + 3.0f);
            csgoCategory.y = deltaY += 22;
        });
        RenderUtils.rectangle(x, y, x + (float)width, y + (float)height, new Color(0x313131).getRGB());
        RenderUtils.outline(x, y, x + (float)width, y + (float)height, new Color(0x2C2C2C), 2.0f);
        RenderUtils.outline(x + 2.0f, y + 5.0f, x + (float)width - 2.0f, y + (float)height - 2.0f, new Color(0x2C2C2C), 2.0f);
        RenderUtils.rectangle(x + 1.0f, y + 1.0f, x + (float)width - 1.0f, y + 2.0f, (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue().getRGB()));
        ImageUtils.image(new ResourceLocation("infinity/textures/icons/logo.png"), (int)(x + 14.0f), (int)(y + 4.0f), 100, 19);
        RenderUtils.rectangle(x + 7.0f, y + 23.0f, x + 118.0f, y + 24.0f, new Color(0x2C2C2C).getRGB());
        RenderUtils.prepareScissor((int)x + 2, (int)y, width, height);
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.drawScreen(mouseX, mouseY));
        RenderUtils.prepareScissor((int)x + 122, (int)y, width, height);
        RenderUtils.outline(x + 124.0f, y + 7.0f, x + (float)width - 4.0f, y + (float)height - 4.0f, new Color(0x2C2C2C), 2.0f);
        this.drawCurrentCategory();
        RenderUtils.outline(x + 126.0f, y + 29.0f, x + 244.0f, y + (float)height - 6.0f, new Color(0x2C2C2C), 1.0f);
        RenderUtils.releaseScissor();
        RenderUtils.prepareScissor((int)x + 245, (int)y + 8, 221, height - 13);
        RenderUtils.outline(x + 246.0f, y + 29.0f, x + (float)width - 6.0f, y + (float)height - 6.0f, new Color(0x2C2C2C), 1.0f);
        RenderUtils.outline(x + 246.0f, y + 8.0f, x + (float)width - 6.0f, y + 28.0f, new Color(0x2C2C2C), 1.0f);
        this.drawSelectedModuleName();
        RenderUtils.releaseScissor();
        RenderUtils.releaseScissor();
    }

    public void drawSelectedModuleName() {
        float x = (float)CsgoGui.x / scaleH;
        float y = (float)CsgoGui.y / scaleY;
        if (module == null) return;
        if (CsgoGui.module.category != category) {
            module = null;
            return;
        }
        RenderUtils.prepareScissor((int)x + 246, (int)y + 8, 226, 20);
        int i = (int)(x + 264.0f + currentModuleDif);
        int diff = this.getDiffModule(category, module);
        currentModuleDif = currentModuleDif < (float)diff ? AnimationUtils.increaseNumber(currentModuleDif, (float)diff, ((float)diff - currentModuleDif) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue() : AnimationUtils.decreaseNumber(currentModuleDif, (float)diff, (currentModuleDif - (float)diff) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
        Iterator<Module> iterator = Managers.MODULES.getModulesInCategory(category).iterator();
        while (true) {
            if (!iterator.hasNext()) {
                RenderUtils.releaseScissor();
                return;
            }
            Module module1 = iterator.next();
            String name = module1.name;
            Infinity.infinityFont.drawStringWithShadow(name, (float)((i += 300) - 175) - Infinity.infinityFont.getStringWidth(name) / 2.0f, y + 18.0f - Infinity.infinityFont.getHeight(name) / 2.0f, -1);
        }
    }

    public int getDiffModule(Category category, Module module) {
        int i = -30;
        for (Module module1 : Managers.MODULES.getModulesInCategory(category)) {
            if (module.equals(module1)) {
                return i;
            }
            i -= 300;
        }
        return i;
    }

    public void drawCurrentCategory() {
        float x = (float)CsgoGui.x / scaleH;
        float y = (float)CsgoGui.y / scaleY;
        RenderUtils.outline(x + 126.0f, y + 8.0f, x + 244.0f, y + 28.0f, new Color(0x2C2C2C), 1.0f);
        int i = (int)(x + 95.0f + currentCategoryImageXDiff);
        int diff = this.getDiff(category);
        currentCategoryImageXDiff = currentCategoryImageXDiff < (float)diff ? AnimationUtils.increaseNumber(currentCategoryImageXDiff, (float)diff, ((float)diff - currentCategoryImageXDiff) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue() : AnimationUtils.decreaseNumber(currentCategoryImageXDiff, (float)diff, (currentCategoryImageXDiff - (float)diff) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
        RenderUtils.prepareScissor((int)x + 126, (int)y + 8, 118, 20);
        Iterator<Category> iterator = Managers.MODULES.getCategories().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                RenderUtils.releaseScissor();
                return;
            }
            Category category1 = iterator.next();
            ImageUtils.image(new ResourceLocation("infinity/textures/icons/" + category1.toString().toLowerCase() + ".png"), i += 80, (int)y + 9, 18, 18);
        }
    }

    public int getDiff(Category category) {
        switch (category) {
            case Combat: {
                return 0;
            }
            case Misc: {
                return -80;
            }
            case Movement: {
                return -160;
            }
            case Player: {
                return -240;
            }
            case Render: {
                return -320;
            }
            case Client: {
                return -400;
            }
        }
        return 0;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.mouseReleased(mouseX, mouseY, state));
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.keyTyped(typedChar, keyCode));
    }

    public void initGui() {
        if (!OpenGlHelper.shadersSupported) return;
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) return;
        try {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }

    public void onGuiClosed() {
        try {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}