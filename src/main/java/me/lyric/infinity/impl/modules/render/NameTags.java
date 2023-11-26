package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.RenderNametagEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ColorSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.impl.modules.client.Notifications;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.mixin.mixins.accessors.IRenderManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lyric
 * meh
 */

@ModuleInformation(name = "NameTags", description = "change all the default colors for good ones", category = Category.Render)
public class NameTags extends Module {

    private final BooleanSetting health = createSetting("Health",true);
    private final BooleanSetting armor = createSetting("Armor", true);
    private final BooleanSetting reversedArmour = createSetting("ReversedArmour", true, v -> armor.getValue());
    private final BooleanSetting scaleing = createSetting("Scale", false);
    private final FloatSetting scaling = createSetting("Scaler", 0.3f, 0.1f, 20.0f, v -> scaleing.getValue());
    private final BooleanSetting ping = createSetting("Ping", true);
    private final BooleanSetting totemPops = createSetting("TotemPops", true);
    private final BooleanSetting gamemode = createSetting("Gamemode", false);
    private final BooleanSetting entityID = createSetting("ID", false);
    private final BooleanSetting rect = createSetting("Rectangle", true);
    private final BooleanSetting outline = createSetting("Outline", false);
    private final FloatSetting lineWidth = createSetting("LineWidth", 1.5f, 0.1f, 5.0f, v -> outline.getValue());
    private final BooleanSetting sneak = createSetting("SneakColor", false);
    private final BooleanSetting heldStackName = createSetting("StackName",false);
    private final FloatSetting factor = createSetting("Factor", 1.0f, 0.1f, 1.0f, v -> scaleing.getValue());
    private final BooleanSetting smartScale = createSetting("SmartScale", false, v -> scaleing.getValue());
    private final BooleanSetting ench = createSetting("Enchantments", false);
    private final ColorSetting mainColor = createSetting("MainColour", defaultColor);
    private final ColorSetting outlineColor = createSetting("OutlineColour", defaultColor);
    private final ColorSetting textColor = createSetting("TextColour", defaultColor);
    private final ColorSetting friendtextColor = createSetting("FriendColor", defaultColor);
    private final ColorSetting invisibleText = createSetting("InvisColour", defaultColor);
    private final ColorSetting shiftColor = createSetting("ShiftColour", defaultColor);
    private CopyOnWriteArrayList<EntityPlayer> players = new CopyOnWriteArrayList<>();

    @Override
    public void onUpdate() {
        players.clear();
        players.addAll(mc.world.playerEntities);
    }

    @Override
    public void onRender3D(float partialTicks) {
            for (EntityPlayer player : players) {
                double x = interpolate(player.lastTickPosX, player.posX, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
                double y = interpolate(player.lastTickPosY, player.posY, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosY();
                double z = interpolate(player.lastTickPosZ, player.posZ, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
                renderNameTag(player, x, y, z, partialTicks);
            }
        }

    public void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth.getValue());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawOutlineRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth.getValue());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        if(player == mc.player)
        {
            return;
        }
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = Managers.FONT.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) scaling.getValue() * (distance * (double) factor.getValue())) / 1000.0;
        if (distance <= 8.0 && smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!scaleing.getValue()) {
            scale = (double) scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();

        if (rect.getValue()) {
            drawRect(-width - 2, -(Managers.FONT.getStringHeight() + 1), (float) width + 2.0f, 1.5f, Managers.FRIENDS.isFriend(player.getDisplayNameString()) ? friendtextColor.getValue().getRGB() : mainColor.getValue().getRGB());
            if (outline.getValue()) {
                drawOutlineRect((float) (-width - 2), (float) (-(Managers.FONT.getStringHeight() + 1)), width + 2.0f, 1.5f, outlineColor.getValue().getRGB());
            }
        }
        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (heldStackName.getValue() && !renderMainHand.isEmpty() && renderMainHand.getItem() != Items.AIR) {
            String stackName = renderMainHand.getDisplayName();
            int stackNameWidth = Managers.FONT.getStringWidth(stackName) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75f, 0.75f, 0.0f);
            Managers.FONT.drawString(stackName, -stackNameWidth, -(getBiggestArmorTag(player) + 20.0f), -1, true);
            GL11.glScalef(1.5f, 1.5f, 1.0f);
            GL11.glPopMatrix();
        }
        if (armor.getValue()) {
           if (reversedArmour.getValue()) {
               GlStateManager.pushMatrix();
               int xOffset = -8;
               for (ItemStack stack : player.inventory.armorInventory) {
                   if (stack == null) continue;
                   xOffset -= 8;
               }
               xOffset -= 8;
               ItemStack renderOffhand = player.getHeldItemOffhand().copy();
               renderItemStack(renderOffhand, xOffset, -26);
               xOffset += 16;
               for (ItemStack stack : player.inventory.armorInventory) {
                   if (stack == null) continue;
                   ItemStack armourStack = stack.copy();
                   renderItemStack(armourStack, xOffset, -26);
                   xOffset += 16;
               }
               renderItemStack(renderMainHand, xOffset, -26);
               GlStateManager.popMatrix();
           } else {
               GlStateManager.pushMatrix();
               int xOffset = -8;
               for (int i = player.inventory.armorInventory.size() - 1; i >= 0; i--) {
                   ItemStack stack = player.inventory.armorInventory.get(i);
                   if (stack == null) continue;
                   xOffset -= 8;
               }
               xOffset -= 8;

               ItemStack renderOffhand = player.getHeldItemOffhand().copy();
               renderItemStack(renderOffhand, xOffset, -26);
               xOffset += 16;

               for (int i = player.inventory.armorInventory.size() - 1; i >= 0; i--) {
                   ItemStack stack = player.inventory.armorInventory.get(i);
                   if (stack == null) continue;
                   ItemStack armourStack = stack.copy();
                   renderItemStack(armourStack, xOffset, -26);
                   xOffset += 16;
               }

               renderItemStack(renderMainHand, xOffset, -26);
               GlStateManager.popMatrix();
           }
        }
        Managers.FONT.drawString(displayTag, -width, -(Managers.FONT.getStringHeight() - 1), getDisplayColour(player), true);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        // GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        //   GlStateManager.disableDepth();
        if(ench.getValue())
            renderEnchantmentText(stack, x, y);
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            Managers.FONT.drawString("god", x * 2, enchantmentY, -3977919, true);
            enchantmentY -= 8;
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID(id);
            if (enc == null) continue;
            String encName = enc.isCurse() ? TextFormatting.RED + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase() : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
            encName = encName + level;
            Managers.FONT.drawString(encName, x * 2, enchantmentY, -1, true);
            enchantmentY -= 8;
        }

    }

    private float getBiggestArmorTag(EntityPlayer player) {
        ItemStack renderOffHand;
        Enchantment enc;
        int index;
        float enchantmentY = 0.0f;
        boolean arm = false;
        for (ItemStack stack : player.inventory.armorInventory) {
            float encY = 0.0f;
            if (stack != null) {
                NBTTagList enchants = stack.getEnchantmentTagList();
                for (index = 0; index < enchants.tagCount(); ++index) {
                    short id = enchants.getCompoundTagAt(index).getShort("id");
                    enc = Enchantment.getEnchantmentByID(id);
                    if (enc == null) continue;
                    encY += 8.0f;
                    arm = true;
                }
            }
            if (!(encY > enchantmentY)) continue;
            enchantmentY = encY;
        }
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect()) {
            float encY = 0.0f;
            NBTTagList enchants = renderMainHand.getEnchantmentTagList();
            for (int index2 = 0; index2 < enchants.tagCount(); ++index2) {
                short id = enchants.getCompoundTagAt(index2).getShort("id");
                Enchantment enc2 = Enchantment.getEnchantmentByID(id);
                if (enc2 == null) continue;
                encY += 8.0f;
                arm = true;
            }
            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }
        if ((renderOffHand = player.getHeldItemOffhand().copy()).hasEffect()) {
            float encY = 0.0f;
            NBTTagList enchants = renderOffHand.getEnchantmentTagList();
            for (index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                enc = Enchantment.getEnchantmentByID(id);
                if (enc == null) continue;
                encY += 8.0f;
                arm = true;
            }
            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }
        return (float) (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        if (!health.getValue()) {
            return name;
        }
        float health = EntityUtil.getHealth(player);
        String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
        String pingStr = "";
        if (ping.getValue()) {
            try {
                int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr = pingStr + responseTime + "ms ";
            } catch (Exception responseTime) {
                responseTime.printStackTrace();
            }
        }
        String popStr = " ";
        if (totemPops.getValue()) {
            if (Notifications.totemPops.get(player.getDisplayNameString()) == null)
            {
                popStr = " ";
            }
            else
            {
                popStr = popStr +"-"+ Notifications.totemPops.get(player.getDisplayNameString());

            }
        }
        String idString = "";
        if (entityID.getValue()) {
            idString = idString + "ID: " + player.getEntityId() + " ";
        }
        String gameModeStr = "";
        if (gamemode.getValue()) {
            gameModeStr = player.isCreative() ? gameModeStr + "[C] " : (player.isSpectator() || player.isInvisible() ? gameModeStr + "[I] " : gameModeStr + "[S] ");
        }
        name = Math.floor(health) == (double) health ? name + color + " " + (health > 0.0f ? Integer.valueOf((int) Math.floor(health)) : "0 ") : name + color + " " + (health > 0.0f ? Integer.valueOf((int) health) : "0 ");
        return name + ChatFormatting.RESET +" "+ idString + gameModeStr + pingStr + popStr;
    }

    private int getDisplayColour(EntityPlayer player) {
        int colour = textColor.getValue().getRGB();
        if (Managers.FRIENDS.isFriend(String.valueOf(player))) {
            return friendtextColor.getValue().getRGB();
        }
        if (player.isInvisible()) {
            colour = invisibleText.getValue().getRGB();
        } else if (player.isSneaking() && sneak.getValue()) {
            colour = shiftColor.getValue().getRGB();
        }
        return colour;
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    @SuppressWarnings("unused")
    @EventListener
    public void onRenderNametag(RenderNametagEvent event) {
        if (!nullSafe()) return;
        event.cancel();
    }
}