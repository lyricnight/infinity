package me.lyric.infinity.impl.modules.client;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author lyric - thanks to CPacketCustomPayload
 * for Catuquei - hud pvp elements are rendered by functions in RenderUtil, except for ArrayList and watermark.
 * The ArrayList's animations are handled by AnimationManager - look when a module is enabled in {Module.class}
 * This all can be rewritten except the render stuff in RenderUtil
 */

public class HUD extends Module {

    public Setting<ColorPicker> color = register(new Setting<>("Color", "The color for the HUD components.", new ColorPicker(Color.WHITE)));

    public Setting<Boolean> activeModules = register(new Setting<>("Modules", "Draws an ArrayList for enabled & drawn modules.", true));

    public Setting<Boolean> watermark = register(new Setting<>("Watermark", "Draws a watermark.", true));
        public Setting<Integer> waterX = register(new Setting<>("Watermark X", "Position X for Watermark.", 2, 1, 1000).withParent(watermark));
        public Setting<Integer> waterY = register(new Setting<>("Watermark Y", "Position Y for Watermark.", 2, 1, 1000).withParent(watermark));

    public Setting<Boolean> coordinates = register(new Setting<>("Coordinates", "Draws your coordinates.", true));
        public Setting<Integer> coordX = register(new Setting<>("Coordinates X", "Position X for Coordinates.", 2, 1, 1000).withParent(coordinates));
        public Setting<Integer> coordY = register(new Setting<>("Coordinates Y", "Position Y for Coordinates.", 10, 1, 1000).withParent(coordinates));

    public Setting<Boolean> speed = register(new Setting<>("Speed", "Draws your speed.", true));

    public Setting<Boolean> ping = register(new Setting<>("Ping", "Draws your server connection speed.", true));
    public Setting<Boolean> armor = register(new Setting<>("Armor", "Draws Armor HUD.", false));
    public Setting<Boolean> tot = register(new Setting<>("Totem Display", "For impcat because he's retarded and can't press e", false));
    public Setting<Boolean> welcomer = register(new Setting<>("Welcomer", "does what it says on the tin", false));
    public Setting<String> textthing = register(new Setting<>("Welcomer String", "string for welcomer", "Welcome to infinity!"));
    public Setting<Boolean> fps = register(new Setting<>("FPS", "Draws your current FPS.", true));
    public Setting<Boolean> tps = register(new Setting<>("TPS", "Draws TPS.", true));
    public Setting<Boolean> pps = register(new Setting<>("PPS", "Draws Packets per Second sent to server.", true));
    public Setting<Boolean> reset = register(new Setting<>("Reset", "Sets HUD components to default positions.", false));
    float offset;
    public Timer packetTimer = new Timer();
    int packets;
    public HUD() {
        super("HUD", "Renders various components on your screen.", Category.CLIENT);
    }


    @EventListener(priority = ListenerPriority.LOWEST)
    public void onPacketSend(PacketEvent.Send event)
    {
        packets++;
    }
    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (packetTimer.passedMs(1000))
        {
            packets = 0;
            packetTimer.reset();
        }

        if (!nullSafe()) return;
        int SCREEN_WIDTH = new ScaledResolution(mc).getScaledWidth();
        int SCREEN_HEIGHT = new ScaledResolution(mc).getScaledHeight();
        if (activeModules.getValue()) {
            offset = 0;
            Infinity.INSTANCE.moduleManager.getModules().stream().filter(Module::isDrawn).filter(Module::isEnabled).filter(module -> module.getAnimation().getAnimationFactor() > 0.05).sorted(Comparator.comparing(module -> mc.fontRenderer.getStringWidth(module.getName() + (!module.getDisplayInfo().equals("") ? " " + module.getDisplayInfo() : "")) * -1)).forEach(module -> {
                mc.fontRenderer.drawStringWithShadow(module.getName() + TextFormatting.WHITE + (!module.getDisplayInfo().equals("") ? " " + module.getDisplayInfo() : ""), (float) (new ScaledResolution(mc).getScaledWidth() - ((mc.fontRenderer.getStringWidth(module.getName() + (!module.getDisplayInfo().equals("") ? " " + module.getDisplayInfo() : "")) + 2) * MathHelper.clamp(module.getAnimation().getAnimationFactor(), 0, 1))), 2 + offset, color.getValue().getColor().getRGB());
                offset += (mc.fontRenderer.FONT_HEIGHT + 1) * MathHelper.clamp(module.getAnimation().getAnimationFactor(), 0, 1);
            });
        }


        if (watermark.getValue()) {
            mc.fontRenderer.drawStringWithShadow("Infinity" + TextFormatting.WHITE + " " + Infinity.INSTANCE.version, waterX.getValue(), waterY.getValue(), color.getValue().getColor().getRGB()); // X & Y can be made custom.
        }

        int y = SCREEN_HEIGHT - 11;

        if (speed.getValue()) {
            double distanceX = mc.player.posX - mc.player.prevPosX;
            double distanceZ = mc.player.posZ - mc.player.prevPosZ;
            String speedDisplay = "Speed: " + TextFormatting.WHITE + roundFloat((MathHelper.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2)) / 1000) / (0.05F / 3600), 1) + " kmh";
            mc.fontRenderer.drawStringWithShadow(speedDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(speedDisplay) - 2, y, color.getValue().getColor().getRGB());
            y -= 10;
        }

        if (ping.getValue()) {
            try {
                String pingDisplay = "Ping: " + TextFormatting.WHITE + (!mc.isSingleplayer() ? Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.player.getUniqueID()).getResponseTime() : 0) + "ms";
                mc.fontRenderer.drawStringWithShadow(pingDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(pingDisplay) - 2, y, color.getValue().getColor().getRGB());
                y -= 10;
            } catch (NullPointerException e) {
                // Hmm...
            }
        }
        if (armor.getValue())
        {
            RenderUtils.renderArmorNew();
        }
        if (tot.getValue())
        {
           RenderUtils.renderTotem();
        }
        if (welcomer.getValue())
        {
            RenderUtils.renderGreeter();
        }
        if (fps.getValue()) {
            String fpsDisplay = "FPS: " + TextFormatting.WHITE + Minecraft.getDebugFPS();
            mc.fontRenderer.drawStringWithShadow(fpsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(fpsDisplay) - 2, y, color.getValue().getColor().getRGB());
            y -= 10;
        }

        if (tps.getValue()) {
            String tpsDisplay = "TPS: " + TextFormatting.WHITE + Infinity.INSTANCE.tpsManager.getTickRateRound();
            mc.fontRenderer.drawStringWithShadow(tpsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(tpsDisplay) - 2, y, color.getValue().getColor().getRGB());
            y -= 10;
        }

        if (pps.getValue()) {
            String ppsDisplay = "Packets: " + TextFormatting.WHITE + packets;
            mc.fontRenderer.drawStringWithShadow(ppsDisplay, SCREEN_WIDTH - mc.fontRenderer.getStringWidth(ppsDisplay) - 2, y, color.getValue().getColor().getRGB());
        }

        if (coordinates.getValue()) {
            String overWorldCoords = mc.player.dimension != -1 ? "" + TextFormatting.WHITE + roundFloat(mc.player.posX, 1) + " " + roundFloat(mc.player.posY, 1) + " " + roundFloat(mc.player.posZ, 1) : "" + TextFormatting.WHITE + roundFloat(mc.player.posX * 8, 1) + " " + roundFloat(mc.player.posY * 8, 1) + " " + roundFloat(mc.player.posZ * 8, 1);
            String netherCoords = mc.player.dimension == -1 ? "" + TextFormatting.WHITE + roundFloat(mc.player.posX, 1) + " " + roundFloat(mc.player.posY, 1) + " " + roundFloat(mc.player.posZ, 1) : "" + TextFormatting.WHITE + roundFloat(mc.player.posX / 8, 1) + " " + roundFloat(mc.player.posY / 8, 1) + " " + roundFloat(mc.player.posZ / 8, 1);

            mc.fontRenderer.drawStringWithShadow(TextFormatting.GRAY + "XYZ" + ":" + " " + TextFormatting.GRAY + "[" + overWorldCoords + TextFormatting.GRAY + "]" + TextFormatting.GRAY + "{" + netherCoords + TextFormatting.GRAY + "}", coordX.getValue(), SCREEN_HEIGHT - coordY.getValue(), color.getValue().getColor().getRGB());
        }

        if (reset.getValue()) {
            coordX.setValue(2);
            coordY.setValue(10);
            waterX.setValue(2);
            waterY.setValue(2);
            reset.setValue(false);
        }
    }

    public static float roundFloat(double number, int scale) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(scale, RoundingMode.FLOOR);
        return bd.floatValue();
    }
}

/**
 * To add HUD information:
 *
 * @Override public String getDisplayInfo() {
 * return "The Information";
 * }
 * <p>
 * If you would like to add brackets, and colors then use ChatFormatting etc and create new strings.
 */
