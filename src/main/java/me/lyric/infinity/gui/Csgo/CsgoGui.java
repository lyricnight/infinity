package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CsgoGui extends GuiScreen
{
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
    public ArrayList<CsgoCategory> csgoCategories;
    public boolean corrected;
    
    public CsgoGui() {
        this.csgoCategories = new ArrayList<>();
        CsgoGui.scaleH = 0.0f;
        CsgoGui.scaleY = 0.1f;
        CsgoGui.deltaY = 0;
        Infinity.INSTANCE.moduleManager.getCategories().forEach(category -> this.csgoCategories.add(new CsgoCategory((category), x + 1, deltaY += 22, 120, 20)));
        CsgoGui.category = Category.Combat;
        CsgoGui.module = null;
    }
    
    public static int getXByModule(final Module module) {
        final float x = CsgoGui.x / CsgoGui.scaleH;
        int i = (int)(x + 264.0f + CsgoGui.currentModuleDif);
        for (Module module2 : Infinity.INSTANCE.moduleManager.getModulesInCategory(CsgoGui.category)) {
            i += 300;
            if (module.equals(module2)) {
                return i - 290;
            }
        }
        return -69420;
    }
    
    public static float getAnimationSpeedAccordingly(final int original) {
        return original / (25.0f / (26.0f - Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).animationSpeed.getValue()));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        if (!this.corrected) {
            CsgoGui.x = scaledResolution.getScaledWidth() / 2 - CsgoGui.width / 2;
            CsgoGui.y = scaledResolution.getScaledHeight() / 2 - CsgoGui.height / 2;
            this.corrected = true;
        }
        CsgoGui.scaleH = AnimationUtils.increaseNumber(CsgoGui.scaleH, 1.0f, (1.0f - CsgoGui.scaleH) / getAnimationSpeedAccordingly(10));
        if (CsgoGui.scaleH >= 0.9f) {
            CsgoGui.scaleY = AnimationUtils.increaseNumber(CsgoGui.scaleY, 1.0f, (1.0f - CsgoGui.scaleY) / getAnimationSpeedAccordingly(50));
            if (CsgoGui.scaleY >= 0.9f) {
                this.csgoCategories.forEach(csgoCategory -> csgoCategory.allowScale = true);
                CsgoGui.finishedScaling = true;
            }
        }
        GlStateManager.scale(CsgoGui.scaleH, CsgoGui.scaleY, CsgoGui.scaleH);
        float x = CsgoGui.x / CsgoGui.scaleH;
        float y = CsgoGui.y / CsgoGui.scaleY;
        CsgoGui.deltaY = (int)(y + 5.0f);
        this.csgoCategories.forEach(csgoCategory -> {
            csgoCategory.x = (int)(x + 3.0f);
            csgoCategory.y = deltaY += 22;
        });
        RenderUtils.rectangle(x, y, x + CsgoGui.width, y + CsgoGui.height, new Color(3223857).getRGB());
        RenderUtils.outline(x, y, x + CsgoGui.width, y + CsgoGui.height, new Color(2894892), 2.0f);
        RenderUtils.outline(x + 2.0f, y + 5.0f, x + CsgoGui.width - 2.0f, y + CsgoGui.height - 2.0f, new Color(2894892), 2.0f);
        RenderUtils.rectangle(x + 1.0f, y + 1.0f, x + CsgoGui.width - 1.0f, y + 2.0f, (Infinity.INSTANCE.moduleManager.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.image(new ResourceLocation("textures/clientrewrite/icons/logo.png"), (int)(x + 14.0f), (int)(y + 4.0f), 100, 19);
        RenderUtils.rectangle(x + 7.0f, y + 23.0f, x + 118.0f, y + 24.0f, new Color(2894892).getRGB());
        RenderUtils.prepareScissor((int)x + 2, (int)y, CsgoGui.width, CsgoGui.height);
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.drawScreen(mouseX, mouseY));
        RenderUtils.prepareScissor((int)x + 122, (int)y, CsgoGui.width, CsgoGui.height);
        RenderUtils.outline(x + 124.0f, y + 7.0f, x + CsgoGui.width - 4.0f, y + CsgoGui.height - 4.0f, new Color(2894892), 2.0f);
        this.drawCurrentCategory();
        RenderUtils.outline(x + 126.0f, y + 29.0f, x + 244.0f, y + CsgoGui.height - 6.0f, new Color(2894892), 1.0f);
        RenderUtils.releaseScissor();
        RenderUtils.prepareScissor((int)x + 245, (int)y + 8, 221, CsgoGui.height - 13);
        RenderUtils.outline(x + 246.0f, y + 29.0f, x + CsgoGui.width - 6.0f, y + CsgoGui.height - 6.0f, new Color(2894892), 1.0f);
        RenderUtils.outline(x + 246.0f, y + 8.0f, x + CsgoGui.width - 6.0f, y + 28.0f, new Color(2894892), 1.0f);
        this.drawSelectedModuleName();
        RenderUtils.releaseScissor();
        RenderUtils.releaseScissor();
    }

    public void drawSelectedModuleName() {
        float x = (float)CsgoGui.x / scaleH;
        float y = (float)CsgoGui.y / scaleY;
        if (module != null) {
            if (CsgoGui.module.getCategory() != category) {
                module = null;
                return;
            }
            RenderUtils.prepareScissor((int)x + 246, (int)y + 8, 226, 20);
            int i = (int)(x + 264.0f + currentModuleDif);
            int diff = this.getDiffModule(category, module);
            currentModuleDif = currentModuleDif < (float)diff ? AnimationUtils.increaseNumber(currentModuleDif, (float)diff, ((float)diff - currentModuleDif) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue() : AnimationUtils.decreaseNumber(currentModuleDif, (float)diff, (currentModuleDif - (float)diff) / CsgoGui.getAnimationSpeedAccordingly(100)).floatValue();
            for (Module module1 : Infinity.INSTANCE.moduleManager.getModulesInCategory(category)) {
                String name = module1.getName();
                Infinity.INSTANCE.infinityFont.drawStringWithShadow(name, (float)((i += 300) - 175) - Infinity.INSTANCE.infinityFont.getStringWidth(name) / 2.0f, y + 18.0f - Infinity.INSTANCE.infinityFont.getHeight(name) / 2.0f, -1);
            }
            RenderUtils.releaseScissor();
        }
    }

    public int getDiffModule(Category category, Module module) {
        int i = -30;
        for (Module module1 : Infinity.INSTANCE.moduleManager.getModulesInCategory(category)) {
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
        for (Category category1 : Infinity.INSTANCE.moduleManager.getCategories()) {
            RenderUtils.image(new ResourceLocation("textures/clientrewrite/icons/" + category1.toString().toLowerCase() + ".png"), i += 80, (int)y + 9, 18, 18);
        }
        RenderUtils.releaseScissor();
    }
    
    public int getDiff(final Category category) {
        switch (category) {
            case Combat: {
                return 0;
            }
            case Exploit: {
                return -80;
            }
            case Misc: {
                return -160;
            }
            case Movement: {
                return -240;
            }
            case Player: {
                return -320;
            }
            case Visual: {
                return -400;
            }
            case Client: {
                return -480;
            }
            default: {
                return 0;
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.mouseReleased(mouseX, mouseY, state));
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.csgoCategories.forEach(csgoCategory -> csgoCategory.keyTyped(typedChar, keyCode));
    }
    
    public void initGui() {
        if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            try {
                this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }
    
    public void onGuiClosed() {
        try {
            this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        catch (Exception ex) {}
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
}
