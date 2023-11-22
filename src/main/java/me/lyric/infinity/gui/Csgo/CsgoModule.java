package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.*;
import me.lyric.infinity.api.util.gl.AnimationUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.gui.Csgo.setting.*;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import me.lyric.infinity.manager.Managers;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CsgoModule implements IGlobals
{
    public ArrayList<CsgoSetting> csgoSettings;
    public Module module;
    public int x;
    public int y;
    public int width;
    public int height;
    public int scroll;
    public boolean correctInsideOutline;
    public boolean needsScrollRevertDown;
    public boolean needsScrollRevertUp;
    public float animX;
    public float leftHeight;
    public float rightHeight;
    public float bottomWidth;
    public float topWidth;
    public float animWidth;
    public float animHeight;
    public float scrollDownRevertValue;
    public float scrollUpRevertValue;

    public CsgoModule(final Module module, final int x, final int y, final int width, final int height) {
        this.csgoSettings = new ArrayList<>();
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animX = (float)(-y);
    }

    public void drawScreen(final int mouseX, final int mouseY) {
        if (this.canRender()) {
            this.setScroll(mouseX, mouseY);
        }
        if (CsgoGui.finishedScaling) {
            this.animX = AnimationUtils.increaseNumber(this.animX, (float)(this.x + 2), (this.x + 2 - this.animX) / CsgoGui.getAnimationSpeedAccordingly(50));
        }
        float x = this.animX;
        RenderUtils.rectangle(x, (float)this.y, x + this.width, (float)(this.y + this.height), new Color(3815994).getRGB());
        if (this.module.isEnabled()) {
            this.animHeight = AnimationUtils.increaseNumber(this.animHeight, (float)this.height, (this.height - this.animHeight) / CsgoGui.getAnimationSpeedAccordingly(25));
            if (this.animHeight >= this.height - 0.5f) {
                this.animWidth = AnimationUtils.increaseNumber(this.animWidth, (float)this.width, (this.width - this.animWidth) / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        else {
            this.animWidth = AnimationUtils.decreaseNumber(this.animWidth, 0.0f, this.animWidth / CsgoGui.getAnimationSpeedAccordingly(25));
            if (this.animWidth <= 0.5f) {
                this.animHeight = AnimationUtils.decreaseNumber(this.animHeight, 0.0f, this.animHeight / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        RenderUtils.rectangle(x, (float)this.y, x + 2.0f + this.animWidth / 2.0f, this.y + this.animHeight / 2.0f, (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x, this.y + this.height - this.animHeight / 2.0f, x + 2.0f + this.animWidth / 2.0f, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x + this.width - this.animWidth / 2.0f - 2.0f, (float)this.y, x + this.width, this.y + this.animHeight / 2.0f, (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x + this.width - this.animWidth / 2.0f - 2.0f, this.y + this.height - this.animHeight / 2.0f, x + this.width, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.outline(x, (float)this.y, x + this.width, (float)(this.y + this.height), new Color(2894892), 1.0f);
        String name = this.module.name;
        Infinity.infinityFont.drawStringWithShadow(name, x + 2.0f, this.y + this.height / 2.0f - Infinity.infinityFont.getHeight(name) / 2.0f, -1);
        if (this.canRender() && !this.csgoSettings.isEmpty()) {
            int deltaY = CsgoGui.y + 17 + this.scroll;
            for (CsgoSetting csgoSetting2 : this.csgoSettings) {
                if (!csgoSetting2.setting.isVisible()) continue;
                csgoSetting2.x = CsgoGui.getXByModule(this.module) + 3;
                csgoSetting2.y = deltaY += 14;
                csgoSetting2.width = 221;
                csgoSetting2.height = 13;
                if (csgoSetting2 instanceof CsgoMode) {
                    deltaY = (int)((float)deltaY + ((CsgoMode)csgoSetting2).extendedAmount);
                }
                if (!(csgoSetting2 instanceof CsgoColor) || !csgoSetting2.setting.isOpen) continue;
                deltaY += 109;
            }
        }
        RenderUtils.prepareScissor(CsgoGui.x + 245, CsgoGui.y + 30, 220, CsgoGui.height - 36);
        if (this.canRender() && !this.csgoSettings.isEmpty()) {
            this.csgoSettings.stream().filter(csgoSetting -> csgoSetting.setting.isVisible()).forEach(csgoSetting -> csgoSetting.drawScreen(mouseX, mouseY));
        }
        RenderUtils.releaseScissor();
        x = this.animX;
        if (!this.correctInsideOutline) {
            this.leftHeight = (float)(this.y + this.height);
            this.rightHeight = (float)(this.y + this.height);
            this.bottomWidth = x;
            this.topWidth = x;
            this.correctInsideOutline = true;
        }
        if (this.isInside(mouseX, mouseY)) {
            this.leftHeight = AnimationUtils.decreaseNumber(this.leftHeight, (float)this.y, (this.leftHeight - this.y) / CsgoGui.getAnimationSpeedAccordingly(25));
            this.bottomWidth = AnimationUtils.increaseNumber(this.bottomWidth, x + this.width, (x + this.width - this.bottomWidth) / CsgoGui.getAnimationSpeedAccordingly(25));
            if (this.bottomWidth >= x + this.width - 1.0f) {
                this.topWidth = AnimationUtils.increaseNumber(this.topWidth, x + this.width, (x + this.width - this.topWidth) / CsgoGui.getAnimationSpeedAccordingly(25));
                this.rightHeight = AnimationUtils.decreaseNumber(this.rightHeight, (float)this.y, (this.rightHeight - this.y) / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        else {
            this.topWidth = AnimationUtils.decreaseNumber(this.topWidth, x, (this.topWidth - x) / CsgoGui.getAnimationSpeedAccordingly(25));
            this.rightHeight = AnimationUtils.increaseNumber(this.rightHeight, (float)(this.y + this.height), (this.y + this.height - this.rightHeight) / CsgoGui.getAnimationSpeedAccordingly(25));
            if (this.topWidth <= x + 1.0f) {
                this.leftHeight = AnimationUtils.increaseNumber(this.leftHeight, (float)(this.y + this.height), (this.y + this.height - this.leftHeight) / CsgoGui.getAnimationSpeedAccordingly(25));
                this.bottomWidth = AnimationUtils.decreaseNumber(this.bottomWidth, x, (this.bottomWidth - x) / CsgoGui.getAnimationSpeedAccordingly(25));
            }
        }
        RenderUtils.rectangle(x, this.leftHeight, x + 1.0f, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x, (float)(this.y + this.height - 1), this.bottomWidth, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x, (float)this.y, this.topWidth, (float)(this.y + 1), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
        RenderUtils.rectangle(x + this.width - 1.0f, this.rightHeight, x + this.width, (float)(this.y + this.height), (Managers.MODULES.getModuleByClass(ClickGUI.class).color.getValue()).getRGB());
    }

    public boolean canRender() {
        final int j = CsgoGui.getXByModule(this.module);
        return CsgoGui.category.equals(this.module.category) && CsgoGui.module != null && CsgoGui.module.equals(this.module) && j >= CsgoGui.x + 96 && j <= CsgoGui.x + CsgoGui.width - 6;
    }

    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.isInside(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0: {
                    if (this.module.isEnabled()) {
                        this.module.disable();
                        break;
                    }
                    this.module.enable();
                    break;
                }
                case 1: {
                    if (CsgoGui.module == null || CsgoGui.module != this.module) {
                        CsgoGui.module = this.module;
                        if (!this.csgoSettings.isEmpty()) {
                            this.csgoSettings.clear();
                        }
                        for (Setting<?> setting : this.module.settingList) {
                            if (setting instanceof BooleanSetting) {
                                this.csgoSettings.add(new CsgoBoolean((BooleanSetting)setting));
                            }
                            if (setting instanceof KeySetting) {
                                this.csgoSettings.add(new CsgoKey((KeySetting)setting));
                            }
                            if (setting instanceof ModeSetting) {
                                this.csgoSettings.add(new CsgoMode((ModeSetting)setting));
                            }
                            if (setting instanceof IntegerSetting) {
                                this.csgoSettings.add(new CsgoInteger((IntegerSetting)setting));
                            }
                            if (setting instanceof FloatSetting) {
                                this.csgoSettings.add(new CsgoFloat((FloatSetting)setting));
                            }
                            if (setting instanceof StringSetting) {
                                this.csgoSettings.add(new CsgoString((StringSetting)setting));
                            }
                            if (setting instanceof ColorSetting) {
                                this.csgoSettings.add(new CsgoColor((ColorSetting)setting));
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        if (this.canRender() && !this.csgoSettings.isEmpty()) {
            this.csgoSettings.stream().filter(csgoSetting -> csgoSetting.setting.isVisible()).forEach(newSetting -> newSetting.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (this.canRender() && !this.csgoSettings.isEmpty()) {
            this.csgoSettings.stream().filter(csgoSetting -> csgoSetting.setting.isVisible()).forEach(newSetting -> newSetting.mouseReleased(mouseX, mouseY, state));
        }
    }

    public void keyTyped(final char typedChar, final int keyCode) {
        if (this.canRender() && !this.csgoSettings.isEmpty()) {
            this.csgoSettings.stream().filter(csgoSetting -> csgoSetting.setting.isVisible()).forEach(newSetting -> newSetting.keyTyped(typedChar, keyCode));
        }
    }

    public boolean isInside(final int mouseX, final int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    public boolean isInsideSettings(final int mouseX, final int mouseY) {
        return mouseX > CsgoGui.x + 246 && mouseX < CsgoGui.x + CsgoGui.width - 6 && mouseY > CsgoGui.y + 29 && mouseY < CsgoGui.y + CsgoGui.height - 6;
    }

    public void setScroll(final int mouseX, final int mouseY) {
        if (this.isInsideSettings(mouseX, mouseY)) {
            final int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                this.scroll -= Managers.MODULES.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
            }
            else if (dWheel > 0) {
                this.scroll += Managers.MODULES.getModuleByClass(ClickGUI.class).scrollSpeed.getValue();
            }
            final int lastSetting = this.getLastSettingY() - 14;
            if (lastSetting != -69420) {
                this.needsScrollRevertDown = (lastSetting + 20 <= CsgoGui.y + 32);
                this.scrollDownRevertValue = 0.0f;
            }
            final int firstSetting = this.getFirstSettingY();
            if (firstSetting != -69420) {
                this.needsScrollRevertUp = (firstSetting >= CsgoGui.y + CsgoGui.height - 26);
                this.scrollUpRevertValue = 0.0f;
            }
            if (this.needsScrollRevertDown) {
                this.scrollDownRevertValue = AnimationUtils.increaseNumber(this.scrollDownRevertValue, 3.0f, (3.0f - this.scrollDownRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100));
                this.scroll += (int)this.scrollDownRevertValue;
            }
            if (this.needsScrollRevertUp) {
                this.scrollUpRevertValue = AnimationUtils.increaseNumber(this.scrollDownRevertValue, 3.0f, (3.0f - this.scrollUpRevertValue) / CsgoGui.getAnimationSpeedAccordingly(100));
                this.scroll -= (int)this.scrollUpRevertValue;
            }
        }
    }

    public int getLastSettingY() {
        TreeMap<Integer, CsgoSetting> csgoSettingTreeMap = this.csgoSettings.stream().collect(Collectors.toMap(csgoSetting -> csgoSetting.y, csgoSetting -> csgoSetting, (a, b) -> b, TreeMap::new));
        if (!csgoSettingTreeMap.isEmpty()) {
            return (csgoSettingTreeMap.lastEntry().getValue()).y;
        }
        return -69420;
    }

    public int getFirstSettingY() {
        TreeMap<Integer, CsgoSetting> csgoSettingTreeMap = this.csgoSettings.stream().collect(Collectors.toMap(csgoSetting -> csgoSetting.y, csgoSetting -> csgoSetting, (a, b) -> b, TreeMap::new));
        if (!csgoSettingTreeMap.isEmpty()) {
            return (csgoSettingTreeMap.firstEntry().getValue()).y;
        }
        return -69420;
    }

    public int getY()
    {
        return y;
    }
}