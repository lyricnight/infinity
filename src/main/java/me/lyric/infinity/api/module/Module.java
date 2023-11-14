package me.lyric.infinity.api.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.*;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.impl.modules.client.Notifications;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lyric
 */

public class Module implements IGlobals {

    private String name;
    private String description;

    public Color defaultColor;
    public List<Setting<?>> settingList;
    public float animfactor = 0.0f;
    private Category category;

    public boolean enabled;

    public KeySetting bind = new KeySetting("Keybind",0);

    public BooleanSetting drawn = new BooleanSetting("Drawn",true);
    public ModuleInformation getModuleInfo() {
        return this.getClass().getAnnotation(ModuleInformation.class);
    }

    public Module() {
        this.settingList = new ArrayList<>();
        this.defaultColor = new Color(254, 254, 254);
        this.enabled = false;
        this.name = this.getModuleInfo().getName();
        this.category = this.getModuleInfo().category();
        this.description = this.getModuleInfo().getDescription();
    }


    public String getName() {
        return name;
    }

    public boolean isDrawn() {
        return drawn.getValue();
    }

    public String getDisplayInfo() {
        return "";
    }

    public Category getCategory() {
        return category;
    }
    public void onTotemPop(EntityPlayer player)
    {

    }
    public void onDeath(EntityPlayer player){

    }

    public boolean isEnabled() {
        return this.enabled;
    }
    public final boolean isDisabled() {
        return !isEnabled();
    }

    public void onRender3D(float partialTicks) {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }
    public void onTick()
    {

    }

    protected void onEnable() {
            MinecraftForge.EVENT_BUS.register(this);
            Infinity.INSTANCE.eventBus.subscribe(this);
    }

    protected void onDisable() {
            MinecraftForge.EVENT_BUS.unregister(this);
            Infinity.INSTANCE.eventBus.unsubscribe(this);
    }
    public void enable()
    {
        this.enabled = true;
        this.onEnable();
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).modules.getValue()) {
            ChatUtils.sendMessageWithID(ChatFormatting.BOLD + this.name + " " + ChatFormatting.RESET + ChatFormatting.GREEN + "enabled!", hashCode());
        }
    }

    public void disable()
    {
        this.enabled = false;
        this.onDisable();
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).modules.getValue()) {
            ChatUtils.sendMessageWithID(ChatFormatting.BOLD + this.name + " " + ChatFormatting.RESET + ChatFormatting.RED + "disabled!", hashCode());
        }
    }

    public BooleanSetting createSetting(String name, boolean value) {
        BooleanSetting setting = new BooleanSetting(name, value);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public BooleanSetting createSetting(String name, boolean value, Predicate<Boolean> shown) {
        BooleanSetting setting = new BooleanSetting(name, value, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public ColorSetting createSetting(String name, Color value) {
        ColorSetting setting = new ColorSetting(name, value);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public ColorSetting createSetting(String name, Color value, Predicate<Color> shown) {
        ColorSetting setting = new ColorSetting(name, value, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public ModeSetting createSetting(String name, String value, List<String> modeList) {
        ModeSetting setting = new ModeSetting(name, value, modeList);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public ModeSetting createSetting(String name, String value, List<String> modeList, Predicate<String> shown) {
        ModeSetting setting = new ModeSetting(name, value, modeList, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public FloatSetting createSetting(String name, float value, float minimum, float maximum) {
        FloatSetting setting = new FloatSetting(name, value, minimum, maximum);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public FloatSetting createSetting(String name, float value, float minimum, float maximum, Predicate<Float> shown) {
        FloatSetting setting = new FloatSetting(name, value, minimum, maximum, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public IntegerSetting createSetting(String name, int value, int minimum, int maximum) {
        IntegerSetting setting = new IntegerSetting(name, value, minimum, maximum);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public IntegerSetting createSetting(String name, int value, int minimum, int maximum, Predicate<Integer> shown) {
        IntegerSetting setting = new IntegerSetting(name, value, minimum, maximum, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public KeySetting createSetting(String name, int value) {
        KeySetting setting = new KeySetting(name, value);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public KeySetting createSetting(String name, int value, Predicate<Integer> shown) {
        KeySetting setting = new KeySetting(name, value, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public StringSetting createSetting(String name, String value) {
        StringSetting setting = new StringSetting(name, value);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }

    public StringSetting createSetting(String name, String value, Predicate<String> shown) {
        StringSetting setting = new StringSetting(name, value, shown);
        setting.setModule(this);
        this.settingList.add(setting);
        return setting;
    }
    protected boolean nullSafe() {
        return mc.player != null && mc.world != null;
    }

    public float getFullWidth()
    {
        return stringWidth() + infoWidth();
    }
    public float infoWidth()
    {
        return -mc.fontRenderer.getStringWidth((!getDisplayInfo().equals("") ? ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + getDisplayInfo() + ChatFormatting.GRAY + "]" : ""));
    }
    public float stringWidth()
    {
        return -mc.fontRenderer.getStringWidth(name + (!getDisplayInfo().equals("") ? " " : ""));
    }
}
