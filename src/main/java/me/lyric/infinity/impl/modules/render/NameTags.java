package me.lyric.infinity.impl.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.events.render.RenderNametagEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.minecraft.EntityUtil;
import me.lyric.infinity.impl.modules.client.Notifications;
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

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


public class NameTags extends Module {

    private final Setting<Boolean> health = this.register(new Setting<>("Health","Renders health.", true));
    private final Setting<Boolean> armor = this.register(new Setting<>("Armor","Renders armor.", true));
     private final Setting<Boolean> reversedArmour = this.register(new Setting<>("ReversedArmour","Reverses the render for armour.", true).withParent(armor));

    private final Setting<Boolean> scaleing = this.register(new Setting<>("Scale","Whether to modify scale or not.", false));
    private final Setting<Float> scaling = this.register(new Setting<>("Scaler","Scale for the nametags.", 0.3f, 0.1f, 20.0f).withParent(scaleing));
    private final Setting<Boolean> invisibles = this.register(new Setting<>("Invisibles","Whether to render invis entities or not.", false));
    private final Setting<Boolean> ping = this.register(new Setting<>("Ping","Whether to render ping or not.", true));
    private final Setting<Boolean> totemPops = this.register(new Setting<>("TotemPops","Displays totem pops.", true));
    private final Setting<Boolean> gamemode = this.register(new Setting<>("Gamemode","Displays gamemode.", false));
    private final Setting<Boolean> entityID = this.register(new Setting<>("ID","Displays Entity ID - for oldfag", false));
    private final Setting<Boolean> rect = this.register(new Setting<>("Rectangle","horrible", true));
    private final Setting<Boolean> outline = this.register(new Setting<Object>("Outline","outline",false));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth","Width of line.", 1.5f, 0.1f, 5.0f).withParent(outline));
    private final Setting<Boolean> sneak = this.register(new Setting<>("SneakColor","Displays a different color for sneaking entities.", false));
    private final Setting<Boolean> heldStackName = this.register(new Setting<>("StackName","Name of item stack.", false));
    private final Setting<Float> factor = this.register(new Setting<Object>("Factor","Scale Factor.", 1.0f, 0.1f, 1.0f).withParent(scaleing));
    private final Setting<Boolean> smartScale = this.register(new Setting<Object>("SmartScale","Whether scaling should be calculated automatically.", false).withParent(scaleing));
    private final Setting<Boolean> ench = this.register(new Setting<>("Enchantments","Whether to render enchantment names.", false));
    private final Setting<ColorPicker> mainColor = this.register(new Setting<>("MainColour", "Colour of the main rect.", new ColorPicker(Color.BLACK)));
    private final Setting<ColorPicker> outlineColor = this.register(new Setting<>("OutlineColour", "Colour of the main rect's outline.", new ColorPicker(Color.BLACK)));
    private final Setting<ColorPicker> textColor = this.register(new Setting<>("TextColour", "Colour of the main rect's text.", new ColorPicker(Color.WHITE)));
    private final Setting<ColorPicker> friendtextColor = this.register(new Setting<>("FriendColor", "Colour of the nametags for friends.", new ColorPicker(Color.CYAN)));
    private final Setting<ColorPicker> invisibleText = this.register(new Setting<>("InvisColour", "Colour of the main rect's text for invis entities.", new ColorPicker(Color.ORANGE)));
    private final Setting<ColorPicker> shiftColor = this.register(new Setting<>("ShiftColour", "Colour of the main rect's text when an entity is shifted.", new ColorPicker(Color.magenta)));
    public NameTags() {
        super("NameTags", "Testing", Category.RENDER);
    }

    private CopyOnWriteArrayList<EntityPlayer> players = new CopyOnWriteArrayList<>();

    @Override
    public void onUpdate() {
        players.clear();
        players.addAll(mc.world.playerEntities);
    }

    @Override
    public void onRender3D(float partialTicks) {
            for (EntityPlayer player : players) {
                double x = this.interpolate(player.lastTickPosX, player.posX, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
                double y = this.interpolate(player.lastTickPosY, player.posY, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosY();
                double z = this.interpolate(player.lastTickPosZ, player.posZ, partialTicks) - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
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
        GlStateManager.glLineWidth(this.lineWidth.getValue());
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
        GlStateManager.glLineWidth(this.lineWidth.getValue());
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
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = this.getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) this.scaling.getValue() * (distance * (double) this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue()) {
            scale = (double) this.scaling.getValue() / 100.0;
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

        if (this.rect.getValue()) {
            drawRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), (float) width + 2.0f, 1.5f, Infinity.INSTANCE.friendManager.isFriend(player) ? friendtextColor.getValue().getColor().getRGB() : mainColor.getValue().getColor().getRGB());
            if (this.outline.getValue()) {
                this.drawOutlineRect((float) (-width - 2), (float) (-(mc.fontRenderer.FONT_HEIGHT + 1)), width + 2.0f, 1.5f, outlineColor.getValue().getColor().getRGB());
            }
        }
        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (this.heldStackName.getValue() && !renderMainHand.isEmpty() && renderMainHand.getItem() != Items.AIR) {
            String stackName = renderMainHand.getDisplayName();
            int stackNameWidth = mc.fontRenderer.getStringWidth(stackName) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75f, 0.75f, 0.0f);
            mc.fontRenderer.drawStringWithShadow(stackName, -stackNameWidth, -(this.getBiggestArmorTag(player) + 20.0f), -1);
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
               this.renderItemStack(renderOffhand, xOffset, -26);
               xOffset += 16;
               for (ItemStack stack : player.inventory.armorInventory) {
                   if (stack == null) continue;
                   ItemStack armourStack = stack.copy();
                   this.renderItemStack(armourStack, xOffset, -26);
                   xOffset += 16;
               }
               this.renderItemStack(renderMainHand, xOffset, -26);
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
               this.renderItemStack(renderOffhand, xOffset, -26);
               xOffset += 16;

               for (int i = player.inventory.armorInventory.size() - 1; i >= 0; i--) {
                   ItemStack stack = player.inventory.armorInventory.get(i);
                   if (stack == null) continue;
                   ItemStack armourStack = stack.copy();
                   this.renderItemStack(armourStack, xOffset, -26);
                   xOffset += 16;
               }

               this.renderItemStack(renderMainHand, xOffset, -26);
               GlStateManager.popMatrix();
           }
        }
        mc.fontRenderer.drawStringWithShadow(displayTag, -width, -(mc.fontRenderer.FONT_HEIGHT - 1), this.getDisplayColour(player));
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
            this.renderEnchantmentText(stack, x, y);
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            mc.fontRenderer.drawStringWithShadow("god", x * 2, enchantmentY, -3977919);
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
            mc.fontRenderer.drawStringWithShadow(encName, x * 2, enchantmentY, -1);
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
        if (!this.health.getValue()) {
            return name;
        }
        float health = EntityUtil.getHealth(player);
        String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
        String pingStr = "";
        if (this.ping.getValue()) {
            try {
                int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr = pingStr + responseTime + "ms ";
            } catch (Exception responseTime) {
                // empty catch block
            }
        }
        String popStr = " ";
        if (this.totemPops.getValue()) {
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
        if (this.entityID.getValue()) {
            idString = idString + "ID: " + player.getEntityId() + " ";
        }
        String gameModeStr = "";
        if (this.gamemode.getValue()) {
            gameModeStr = player.isCreative() ? gameModeStr + "[C] " : (player.isSpectator() || player.isInvisible() ? gameModeStr + "[I] " : gameModeStr + "[S] ");
        }
        name = Math.floor(health) == (double) health ? name + color + " " + (health > 0.0f ? Integer.valueOf((int) Math.floor(health)) : "0 ") : name + color + " " + (health > 0.0f ? Integer.valueOf((int) health) : "0 ");
        return name + ChatFormatting.RESET +" "+ idString + gameModeStr + pingStr + popStr;
    }

    private int getDisplayColour(EntityPlayer player) {
        int colour = textColor.getValue().getColor().getRGB();
        if (Infinity.INSTANCE.friendManager.isFriend(player)) {
            return friendtextColor.getValue().getColor().getRGB();
        }
        if (player.isInvisible()) {
            colour = invisibleText.getValue().getColor().getRGB();
        } else if (player.isSneaking() && this.sneak.getValue()) {
            colour = shiftColor.getValue().getColor().getRGB();
        }
        return colour;
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
    @EventListener
    public void onRenderNametag(RenderNametagEvent event) {
        if (!nullSafe()) return;
        event.cancel();
    }
}