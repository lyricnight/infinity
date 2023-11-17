package me.lyric.infinity.impl.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.StringSetting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.gl.ColorUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;


/**
 * @author lyric
 */

@ModuleInformation(name = "HUD", description = "Head-Up-Display", category = Category.Client)
public class HUD extends Module {
    public BooleanSetting shadow = createSetting("Shadow", true);

    public BooleanSetting step = createSetting("Step", false);
    public ColorSetting stepColor = createSetting("Step Colour", defaultColor, v -> step.getValue());
    public IntegerSetting stepLength = createSetting("Length", 30, 10, 130, (Predicate<Integer>) v -> step.getValue());
    public IntegerSetting stepSpeed = createSetting("Speed", 30, 1, 130, (Predicate<Integer>) v -> step.getValue());

    public ColorSetting color = createSetting("Main Colour", defaultColor);
    public BooleanSetting activeModules = createSetting("Modules",true);
    public BooleanSetting watermark = createSetting("Watermark",true);
    public IntegerSetting waterX = createSetting("Watermark X", 2, 1, 1000, (Predicate<Integer>) v -> watermark.getValue());
    public IntegerSetting waterY = createSetting("Watermark Y", 2, 1, 1000, (Predicate<Integer>) v -> watermark.getValue());

    public BooleanSetting info = createSetting("Info", true);

    public BooleanSetting coordinates = createSetting("Coordinates",true);

    public BooleanSetting speed = createSetting("Speed", true);
    public BooleanSetting armor = createSetting("Armor", false);
    public BooleanSetting welcomer = createSetting("Welcomer", false);
    public StringSetting textthing = createSetting("Welcomer String", "Welcome to infinity!", v -> welcomer.getValue());
    public IntegerSetting thing = createSetting("Offset-Array", 10, 1, 200, (Predicate<Integer>) v -> activeModules.getValue());
    private int offset = 0;
    private final ArrayList<Module> modules = new ArrayList<>();
    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent event) {
        if (!nullSafe())
            return;
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.POTION_ICONS) || (event.getType().equals(RenderGameOverlayEvent.ElementType.ARMOR))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void draw(final RenderGameOverlayEvent.Text event) {
        if (!nullSafe()) {
            return;
        }
        offset = 0;
        int SCREEN_WIDTH = new ScaledResolution(mc).getScaledWidth();
        int y = new ScaledResolution(mc).getScaledHeight() - 11;
        if (activeModules.getValue()) {
            Infinity.INSTANCE.moduleManager.getModules().stream().filter(module -> module.isEnabled() && !modules.contains(module)).forEach(modules::add);
            modules.sort(Comparator.comparing(Module::getFullWidth));
            int deltaY = 0;
            for (final Module module : new ArrayList<>(modules)) {
                if (!module.isDrawn()) {
                    continue;
                }
                module.animfactor = MathUtils.linearInterpolation(module.animfactor, module.isEnabled() ? 1.0f : 0.0f, 0.005f * Infinity.INSTANCE.forgeEventManager.frameTime);
                final float x = SCREEN_WIDTH - ((module.animfactor * mc.fontRenderer.getStringWidth(module.name + (!module.getDisplayInfo().equals("") ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : ""))) + thing.getValue());
                if (!module.isEnabled() && module.animfactor < 0.05f) {
                    modules.remove(module);
                }
                String text = module.name + (!module.getDisplayInfo().equals("") ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                mc.fontRenderer.drawString(text, x, deltaY, getTextColor(deltaY).getRGB(), shadow.getValue());
                deltaY += (mc.fontRenderer.FONT_HEIGHT + 1) * module.animfactor;
            }
        }
        if (info.getValue()) {
            DecimalFormat minuteFormatter = new DecimalFormat("0");
            DecimalFormat secondsFormatter = new DecimalFormat("00");
            List<InfoComponent> potions = new ArrayList<InfoComponent>();
            List<InfoComponent> info = new ArrayList<InfoComponent>();
            for (final PotionEffect effect : mc.player.getActivePotionEffects()) {
                double timeS = (double) effect.getDuration() / 20 % 60;
                double timeM = (double) effect.getDuration() / 20 / 60;
                final String time = minuteFormatter.format(timeM) + ":" + secondsFormatter.format(timeS);
                String name;
                name = I18n.format(effect.getEffectName(), new Object[0]) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.WHITE + time;
                potions.add(new InfoComponent(name));
            }
            info.add(new InfoComponent("FPS " + ChatFormatting.WHITE + Minecraft.getDebugFPS()));
            if (mc.getConnection() != null && mc.world != null && !(mc.currentScreen instanceof GuiDownloadTerrain) && (mc.getConnection().getPlayerInfo(mc.player.getUniqueID()) != null)) {
                info.add(new InfoComponent("Ping " + ChatFormatting.WHITE + mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime()));
                info.add(new InfoComponent("TPS " + ChatFormatting.WHITE + Infinity.INSTANCE.tpsManager.getTickRateRound()));
            }
            double distanceX = mc.player.posX - mc.player.prevPosX;
            double distanceZ = mc.player.posZ - mc.player.prevPosZ;
            info.add(new InfoComponent("Speed " + ChatFormatting.WHITE + MathUtils.roundFloat((MathHelper.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2)) / 1000) / (0.05F / 3600), 1) + " km/h"));
            info.sort(Comparator.comparingInt(i -> - mc.fontRenderer.getStringWidth(i.text)));
            renderPotions(potions);
            renderInfo(info);
        }
        if (coordinates.getValue())
        {
            DecimalFormat coordFormat = new DecimalFormat("#.#");
            boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
            int k = (mc.currentScreen instanceof GuiChat) ? 14 : 0;
            mc.fontRenderer.drawString(EntityUtil.getFacing(mc.player.getHorizontalFacing().getName().toUpperCase()), 2, 530 - mc.fontRenderer.FONT_HEIGHT - k, getTextColor(530 - mc.fontRenderer.FONT_HEIGHT).getRGB(), shadow.getValue());
            if (inHell) {
                mc.fontRenderer.drawString("XYZ " + ChatFormatting.WHITE + coordFormat.format(mc.player.posX) + ", " + coordFormat.format(mc.player.posY) + ", " + coordFormat.format(mc.player.posZ) + ChatFormatting.DARK_GRAY + " (" + ChatFormatting.WHITE + coordFormat.format(mc.player.posX * 7.0) + ", " + coordFormat.format(mc.player.posZ * 7.0) + ChatFormatting.DARK_GRAY + ")", 2, 530 - k, getTextColor(530).getRGB(), shadow.getValue());
            }
            else {
                mc.fontRenderer.drawString("XYZ " + ChatFormatting.WHITE + coordFormat.format(mc.player.posX) + ", " + coordFormat.format(mc.player.posY) + ", " + coordFormat.format(mc.player.posZ) + ChatFormatting.DARK_GRAY + " (" + ChatFormatting.WHITE + coordFormat.format(mc.player.posX / 7.0) + ", " + coordFormat.format(mc.player.posZ / 7.0) + ChatFormatting.DARK_GRAY + ")", 2, 530 - k, getTextColor(530).getRGB(), shadow.getValue());
            }


        }
        if (watermark.getValue()) {
            mc.fontRenderer.drawString("Infinity" + " " + Infinity.INSTANCE.version, waterX.getValue(), waterY.getValue(), getTextColor(waterY.getValue()).getRGB(), shadow.getValue());
        }
        if (armor.getValue()) {
            RenderUtils.renderArmorNew();
        }
        if (welcomer.getValue()) {
            renderGreeter();
        }
    }
    public Color getTextColor (final int y){
        if (step.getValue()) {
            double roundY = Math.sin(Math.toRadians((double) ((long) y * (stepLength.getValue()) + System.currentTimeMillis() / stepSpeed.getValue())));
            roundY = Math.abs(roundY);
            return ColorUtils.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), color.getValue(), stepColor.getValue());
        }
        return color.getValue();
    }
    public void renderGreeter() {
        final int width = new ScaledResolution(mc).getScaledWidth();
        String welcomerString = String.format(textthing.getValue(), mc.player.getName());
        mc.fontRenderer.drawString(welcomerString, width / 2.0f - mc.fontRenderer.getStringWidth(welcomerString) / 2.0f + 2.0f, 2, getTextColor(2).getRGB(), shadow.getValue());
    }
    public void renderInfo(List<InfoComponent> info) {
        int start = new ScaledResolution(mc).getScaledHeight() - 11;
        int SCREEN_WIDTH = new ScaledResolution(mc).getScaledWidth();
        for (final InfoComponent comp : info) {
            final int x = SCREEN_WIDTH - mc.fontRenderer.getStringWidth(comp.text);
            mc.fontRenderer.drawString(comp.text, x - 2, (start + offset + 1), getTextColor(start + offset).getRGB(), shadow.getValue());
            offset -= mc.fontRenderer.FONT_HEIGHT + 1;
        }
    }
    public void renderPotions(List<InfoComponent> potions) {
        int start = new ScaledResolution(mc).getScaledHeight() - 11;
        for (final InfoComponent comp : potions) {
            int SCREEN_WIDTH = new ScaledResolution(mc).getScaledWidth();
            final int x = SCREEN_WIDTH - mc.fontRenderer.getStringWidth(comp.text);
            mc.fontRenderer.drawString(comp.text, (x - 2), (start + offset + 1), getTextColor(start + offset).getRGB(), shadow.getValue());
            offset -= mc.fontRenderer.FONT_HEIGHT + 1;
        }
    }
    static class InfoComponent
    {
        String text;
        public InfoComponent(String text) {
            this.text = text;
        }
    }
}