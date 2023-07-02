package me.lyric.infinity.impl.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.ColorUtils;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.metadata.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lyric
 * the long awaited rewrite
 */

public class HUD extends Module {
    public Setting<Boolean> shadow = register(new Setting<>("Shadow", "For string to be drawn with a shadow.", false));
    public Setting<Boolean> step = register(new Setting<>("Step", "Colour step.", false));
    public Setting<ColorPicker> stepColor = register(new Setting<>("Step Colour", "The step colour for the HUD components.", new ColorPicker(Color.WHITE)).withParent(step));
    public Setting<Integer> stepLength = register(new Setting<>("Length", "Length for step.", 30, 10, 130).withParent(step));
    public Setting<Integer> stepSpeed = register(new Setting<>("Speed", "Speed for step.", 30, 1, 130).withParent(step));

    public Setting<ColorPicker> color = register(new Setting<>("Main Colour", "The main colour for the HUD components.", new ColorPicker(Color.WHITE)));
    public Setting<Boolean> activeModules = register(new Setting<>("Modules", "Draws an ArrayList for enabled & drawn modules.", true));
    public Setting<Integer> xPos = register(new Setting<>("ListX", "X for list.", 100, 0, 1000).withParent(activeModules));
    public Setting<Integer> yPos = register(new Setting<>("ListY", "Y for list.", 100, 0, 1000).withParent(activeModules));

    public Setting<Boolean> watermark = register(new Setting<>("Watermark", "Draws a watermark.", true));
    public Setting<Integer> waterX = register(new Setting<>("Watermark X", "Position X for Watermark.", 2, 1, 1000).withParent(watermark));
    public Setting<Integer> waterY = register(new Setting<>("Watermark Y", "Position Y for Watermark.", 2, 1, 1000).withParent(watermark));

    public Setting<Boolean> coordinates = register(new Setting<>("Coordinates", "Draws your coordinates.", true));
    public Setting<Integer> coordX = register(new Setting<>("Coordinates X", "Position X for Coordinates.", 2, 1, 1000).withParent(coordinates));
    public Setting<Integer> coordY = register(new Setting<>("Coordinates Y", "Position Y for Coordinates.", 10, 1, 1000).withParent(coordinates));

    public Setting<Boolean> speed = register(new Setting<>("Speed", "Draws your speed.", true));

    public Setting<Boolean> ping = register(new Setting<>("Ping", "Draws your server connection speed.", true));
    public Setting<Boolean> armor = register(new Setting<>("Armor", "Draws Armor HUD.", false));
    public Setting<Boolean> welcomer = register(new Setting<>("Welcomer", "does what it says on the tin", false));
    public Setting<String> textthing = register(new Setting<>("Welcomer String", "string for welcomer", "Welcome to infinity!").withParent(welcomer));
    public Setting<Boolean> fps = register(new Setting<>("FPS", "Draws your current FPS.", true));
    public Setting<Boolean> tps = register(new Setting<>("TPS", "Draws TPS.", true));
    public Setting<Boolean> pps = register(new Setting<>("PPS", "Draws Packets per Second sent to server.", true));

    protected int width = 30;
    private int packets = 0;

    public HUD()
    {
        super("HUDnew", "test", Category.CLIENT);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send ignored)
    {
        packets++;
    }
    @Override
    public void onEnable()
    {
        if (!nullSafe()) return;
        ScaledResolution sr = new ScaledResolution(mc);
        xPos.setMaximum(sr.getScaledWidth());
        yPos.setMaximum(sr.getScaledHeight());
    }

    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent event) {
        if (!nullSafe())
            return;
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.POTION_ICONS) || (event.getType().equals(RenderGameOverlayEvent.ElementType.ARMOR)))
        {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void draw(final RenderGameOverlayEvent.Text event) {
        if (!nullSafe()) {
            return;
        }
        int SCREEN_WIDTH = new ScaledResolution(mc).getScaledWidth();
        int y = new ScaledResolution(mc).getScaledHeight() - 11;
        if(activeModules.getValue())
        {
            final ArrayList<Module> sorted = new ArrayList<>();
            int offsetx = -2;
            int offsety = 2;
            for (final Module module : Infinity.INSTANCE.moduleManager.getModules()) {
                if ((module.isDrawn() && module.isEnabled())) {
                    sorted.add(module);
                }
            }
            width = 30;
            sorted.sort(Comparator.comparingInt(mod -> {
                int o = mc.fontRenderer.getStringWidth(mod.getName() + (!mod.getDisplayInfo().equals("") ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + mod.getDisplayInfo() + ChatFormatting.GRAY + "]" : ""));
                return -o;
            }));
            int offset = 0;
            for (final Module module : sorted) {
                String text = module.getName() + (module.getDisplayInfo().equals("") ? "" : (ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]"));
                mc.fontRenderer.drawString(text, (float) (SCREEN_WIDTH - ((mc.fontRenderer.getStringWidth(text) + offsetx))), ((offset) + offsety), getTextColor(offset).getRGB(), shadow.getValue());
                offset += mc.fontRenderer.FONT_HEIGHT + 1;
            }
        }
        if (watermark.getValue())
        {
            mc.fontRenderer.drawString("Infinity" + " " + Infinity.INSTANCE.version, waterX.getValue(), waterY.getValue(), getTextColor(waterY.getValue()).getRGB(), shadow.getValue());
        }
        if (armor.getValue())
        {
            RenderUtils.renderArmorNew();
        }
        if (welcomer.getValue())
        {
            renderGreeter();
        }
        if (speed.getValue()) {
            double distanceX = mc.player.posX - mc.player.prevPosX;
            double distanceZ = mc.player.posZ - mc.player.prevPosZ;
            String speedDisplay = "Speed: " + TextFormatting.WHITE + MathUtils.roundFloat((MathHelper.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2)) / 1000) / (0.05F / 3600), 1) + " km/h";
            mc.fontRenderer.drawString(speedDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(speedDisplay) - 2, y, getTextColor(y).getRGB(), shadow.getValue());
            y -= 10;
        }

        if (ping.getValue()) {
            try {
                String pingDisplay = "Ping: " + TextFormatting.WHITE + (!mc.isSingleplayer() ? Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.player.getUniqueID()).getResponseTime() : 0) + "ms";
                mc.fontRenderer.drawString(pingDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(pingDisplay) - 2, y, getTextColor(y).getRGB(), shadow.getValue());
                y -= 10;
            } catch (NullPointerException e) {
                // Hmm...
            }
        }
        if (fps.getValue()) {
            String fpsDisplay = "FPS: " + TextFormatting.WHITE + Minecraft.getDebugFPS();
            mc.fontRenderer.drawString(fpsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(fpsDisplay) - 2, y, getTextColor(y).getRGB(), shadow.getValue());
            y -= 10;
        }

        if (tps.getValue()) {
            String tpsDisplay = "TPS: " + TextFormatting.WHITE + Infinity.INSTANCE.tpsManager.getTickRateRound();
            mc.fontRenderer.drawString(tpsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(tpsDisplay) - 2, y, getTextColor(y).getRGB(), shadow.getValue());
            y -= 10;
        }

        if (pps.getValue()) {
            String ppsDisplay = "Packets: " + TextFormatting.WHITE + packets;
            mc.fontRenderer.drawString(ppsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(ppsDisplay) - 2, y, getTextColor(y).getRGB(), shadow.getValue());
        }
    }
    public Color getTextColor(final int y) {
        if (step.getValue()) {
            double roundY = Math.sin(Math.toRadians((double)((long) y * (stepLength.getValue()) + System.currentTimeMillis() / stepSpeed.getValue())));
            roundY = Math.abs(roundY);
            return ColorUtils.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), color.getValue().getColor(), stepColor.getValue().getColor());
        }
        return color.getValue().getColor();
    }
    public void renderGreeter() {
        final int width = new ScaledResolution(mc).getScaledWidth();
        String welcomerString = String.format(textthing.getValue(), mc.player.getName());
        mc.fontRenderer.drawString(welcomerString, width / 2.0f - mc.fontRenderer.getStringWidth(welcomerString) / 2.0f + 2.0f, 2,getTextColor(2).getRGB(), shadow.getValue() );
    }





}

