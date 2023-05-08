package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.minecraft.EntityUtil;
import me.lyric.infinity.mixin.mixins.accessors.IRenderManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class NameTags extends Module {
    public Setting<ColorPicker> colour = register(new Setting<>("Colour", "Colour of border.", new ColorPicker(Color.BLACK)));
    public Setting<Boolean> armor = register(new Setting<>("Armor","Renders armor.", true));
    public Setting<Boolean> health = register(new Setting<>("Health","Renders health.", true));
    public Setting<Boolean> ping = register(new Setting<>("Ping","Renders ping.", true));
    public Setting<Boolean> gamemode = register(new Setting<>("Gamemode","Renders gamemode.", true));
    public Setting<Boolean> invisibles = register(new Setting<>("Invisibles","Renders invis entities.", true));
    public Setting<Boolean> durability = register(new Setting<>("Durability","Renders item dura,",true));
    public Setting<Boolean> itemName = register(new Setting<>("ItemName","Renders item names.", true));
    public Setting<Boolean> totemPops = register(new Setting<>("TotemPops","Renders totem pop.",true));
    public Setting<Boolean> shortEnchants = register(new Setting<>("Enchants","Renders enchantment names.", true));
    public Setting<Double> scaling = register(new Setting<>("Scaling","scale.", 3.0d, 1.0d, 5.0d));
    public Setting<Boolean> background = register(new Setting<>("Background","Waste of a setting.", true));
    public Setting<Boolean> border = register(new Setting<>("Border","if you use this you need help", true));
    public Setting<Double> borderWidth = register(new Setting<>("BorderWidth","width of border", 1.0d, 0.1d, 3.0d));

    public static NameTags INSTANCE;
    public NameTags() {
        super("NameTags","Nametags rendering.", Category.RENDER);
        INSTANCE = this;
    }

    private ICamera camera = new Frustum();

    @Override
    public void onRender3D(float partialTicks) {
        for (EntityPlayer entity : mc.world.playerEntities) {
            double x = interpolate(entity.lastTickPosX, entity.posX, partialTicks) - ((IRenderManager)(mc.getRenderManager())).getRenderPosX();
            double y = interpolate(entity.lastTickPosY, entity.posY, partialTicks) - ((IRenderManager)(mc.getRenderManager())).getRenderPosY();
            double z = interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) -((IRenderManager)(mc.getRenderManager())).getRenderPosZ();
            if(entity != mc.getRenderViewEntity()) {
                if(!invisibles.getValue() && entity.isInvisible()) {
                    return;
                }
                renderNameTag(entity, x, y, z, partialTicks);
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        AxisAlignedBB bb = player.getEntityBoundingBox();

        bb = bb.expand(0.15f, 0.1f, 0.15f);

        if (!camera.isBoundingBoxInFrustum(bb))
            return;

        double tempY = y;
        tempY += player.isSneaking() ? 0.5D : 0.7D;
        Entity camera = mc.getRenderViewEntity();
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        double distance = camera.getDistance(x + (mc.getRenderManager()).viewerPosX, y + (mc.getRenderManager()).viewerPosY, z +  (mc.getRenderManager()).viewerPosZ);
        int width = (int) (mc.fontRenderer.getStringWidth(getDisplayName(player)) / 2);
        double scale = distance > 8.0d ? 0.0018d + (scaling.getValue() * 0.001d) * distance : 0.0018d + (scaling.getValue() * 0.001d) * 8.0d;
        RenderUtils.drawBorderedRect(-width - 2.0f, -(mc.fontRenderer.FONT_HEIGHT + 2.3f), width + 4.0f, 1.5F, borderWidth.getValue().floatValue(), colour.getValue().getColor().getRGB(), colour.getValue().getColor().getRGB());
        mc.fontRenderer.drawStringWithShadow(getDisplayName(player), -width, (int) -(mc.fontRenderer.FONT_HEIGHT + 2), getDisplayColour(player));
        if (armor.getValue()) {
            int xOffset = 0;
            if((player.getHeldItemMainhand().getItem() != null && player.getHeldItemOffhand().getItem() == null) || (player.getHeldItemMainhand().getItem() == null && player.getHeldItemOffhand().getItem() != null)) {
                xOffset = -4;
            } else if(player.getHeldItemMainhand().getItem() != null && player.getHeldItemOffhand().getItem() != null) {
                xOffset = -8;
            }
            int index;
            for (index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory.get(index);
                if (stack != null && stack.getItem() != Items.AIR)
                    xOffset -= 8;
            }
            ArrayList<ItemStack> armorStacks = new ArrayList<>();
            if(player.inventory.armorInventory != null) {
                for(ItemStack itemStack : player.inventory.armorInventory) {
                    if(itemStack != null && !itemStack.getItem().equals(Items.AIR)) {
                        armorStacks.add(itemStack);
                    }
                }
            }
            ArrayList<ItemStack> stacks = new ArrayList<>();
            stacks.addAll(player.inventory.armorInventory);
            if(player.getHeldItemMainhand() != null) stacks.add(player.getHeldItemMainhand().copy());
            if(player.getHeldItemOffhand() != null) stacks.add(player.getHeldItemOffhand().copy());

            if (player.getHeldItemOffhand() != null) {
                xOffset -= 8;
                ItemStack renderStack = player.getHeldItemOffhand().copy();
                if(!renderStack.getItem().equals(Items.AIR)) {
                    renderItemStack(stacks, renderStack, xOffset, -(getEnchantSpace(stacks)+26)+26+10);
                    if(armorStacks.isEmpty()) {
                        xOffset += 22;
                    } else {
                        xOffset += 16;
                    }
                }
            }
            for (index = armorStacks.size()-1; index >= 0; index--) {
                ItemStack stack = armorStacks.get(index);
                if (stack != null) {
                    ItemStack armourStack = stack.copy();
                    if(!armourStack.getItem().equals(Items.AIR)) {
                        if (armourStack.getItem() instanceof net.minecraft.item.ItemTool || armourStack.getItem() instanceof net.minecraft.item.ItemArmor || armourStack.getItem().equals(Items.ELYTRA)) {
                            renderItemStack(stacks, armourStack, xOffset, -(getEnchantSpace(stacks)+26)+26+10);
                        }
                        if(armorStacks.get(0) == stack) {
                            xOffset += 24;
                        } else {
                            xOffset += 16;
                        }
                    }
                }
            }
            if (player.getHeldItemMainhand() != null) {
                xOffset -= 8;
                ItemStack renderStack = player.getHeldItemMainhand().copy();
                if(!renderStack.getItem().equals(Items.AIR)) {
                    renderItemStack(stacks, renderStack, xOffset, -(getEnchantSpace(stacks)+26)+26+10);
                    if (itemName.getValue() && !renderStack.isEmpty() && renderStack.getItem() != Items.AIR) {
                        String stackName = renderStack.getDisplayName();
                        int stackNameWidth = (int) (mc.fontRenderer.getStringWidth(stackName) / 2);
                        int enchantY = 2-getEnchantSpace(stacks)-24;
                        if(!(enchantY < -50)) {
                            enchantY = -50;
                        }
                        mc.fontRenderer.drawStringWithShadow(stackName, -stackNameWidth, (int) (enchantY - 20), -1);
                    }
                    xOffset += 16;
                }
            }
        }
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
    }


    public static int getTotemPops(String user) {
        if(user != null && Infinity.INSTANCE.totemPopManager.getPoppedUsers() != null && Infinity.INSTANCE.totemPopManager.getPoppedUsers().get(user) != null) {
            return (int) Infinity.INSTANCE.totemPopManager.getPoppedUsers().get(user);
        }
        return 0;
    }
    public int getEnchantSpace(ArrayList<ItemStack> items) {
        int biggestEncCount = 0;
        for(ItemStack i : items) {
            NBTTagList enchants = i.getEnchantmentTagList();
            if (enchants != null) {
                if(enchants.tagCount() > biggestEncCount) {
                    biggestEncCount = enchants.tagCount();
                }
            }
        }
        if(!shortEnchants.getValue()) {
            biggestEncCount = 1;
        }
        return biggestEncCount*8;
    }

    public int getHighestEncY(ArrayList<ItemStack> items) {
        return getEnchantSpace(items);
    }

    public String getGMText(EntityPlayer p) {
        if(p.isCreative())
            return "C";
        if(p.isSpectator())
            return "I";
        if(!p.isAllowEdit() && !p.isSpectator())
            return "A";
        if(!p.isCreative() && !p.isSpectator() && p.isAllowEdit())
            return "S";
        return "";
    }

    private void renderItemStack(ArrayList<ItemStack> stacks, ItemStack stack, int x, int y) {
        int enchantY = 2-getEnchantSpace(stacks)-24;
        int armorY = 2-(getEnchantSpace(stacks)/2)-14;

        if(!(armorY < -26)) {
            armorY = -26;
        }
        if(!(enchantY < -50)) {
            enchantY = -50;
        }
        (mc.getRenderItem()).zLevel = -150.0F;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, armorY);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, armorY);
        (mc.getRenderItem()).zLevel = 0.0F;
        renderEnchantmentText(stack, x, enchantY);
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y;

        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemElytra) {
            if (durability.getValue()) {
                float armorPercent = ((float)(stack.getMaxDamage()-stack.getItemDamage()) /  (float)stack.getMaxDamage())*100.0f;
                int armorBarPercent = (int)Math.min(armorPercent, 999.0f);
                mc.fontRenderer.drawStringWithShadow(String.valueOf(armorBarPercent)+"%", x * 2, y - 10, stack.getItem().getRGBDurabilityForDisplay(stack));
            }
        }
        if(!shortEnchants.getValue()) {
            return;
        }

        if(stack.getItem() != null && !(stack.getItem() instanceof ItemAir)) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            if (enchants != null) {
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    short id = enchants.getCompoundTagAt(index).getShort("id");
                    short level = enchants.getCompoundTagAt(index).getShort("lvl");
                    Enchantment enc = Enchantment.getEnchantmentByID(id);
                    if (enc != null) {
                        String encName = enc.isCurse() ? enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase() : enc.getTranslatedName(level).substring(0, 3).toLowerCase();
                        if(!String.valueOf(level).equalsIgnoreCase("1") && !enc.isCurse()) {
                            encName = enc.getTranslatedName(level).substring(0, 2).toLowerCase()+String.valueOf(level);
                        } else if(String.valueOf(level).equalsIgnoreCase("1") && !enc.isCurse()) {
                            encName = enc.getTranslatedName(level).substring(0, 3).toLowerCase();
                        }
                        if(enc.isCurse()) {
                            encName = "Van";
                        }
                        encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
                        mc.fontRenderer.drawStringWithShadow(encName, x * 2, enchantmentY, 0xffffffff);
                        enchantmentY += 8;
                    }
                }
            }
        }
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            mc.fontRenderer.drawStringWithShadow("God", (x * 2), enchantmentY, 0xff9e1800);
        }
    }

    private String getDisplayName(EntityPlayer player) {
        TextFormatting color;
        String name = player.getDisplayName().getFormattedText();

        if(gamemode.getValue())
            name += String.format(" [%s]", getGMText(player));

        if(ping.getValue())
            name += String.format(" %dms", EntityUtil.getPing(player));

        if (this.health.getValue())
        {
            float health = player.getHealth()+player.getAbsorptionAmount();

            if (health <= 0)
                health = 1;

            if (health > 18.0F)
                color = TextFormatting.GREEN;
            else if (health > 16.0F)
                color = TextFormatting.DARK_GREEN;
            else if (health > 12.0F)
                color = TextFormatting.YELLOW;
            else if (health > 8.0F)
                color = TextFormatting.GOLD;
            else if (health > 5.0F)
                color = TextFormatting.RED;
            else
                color = TextFormatting.DARK_RED;

            float totalHealth = player.getHealth() + player.getAbsorptionAmount();
            int pHealth = (int)Math.ceil(totalHealth);

            if(pHealth <= 0)
                pHealth = 1;

            name += String.format(" %s%d", color, pHealth);
        }

        if (totemPops.getValue() && player.getName() != null && getTotemPops(player.getName()) > 0)
            name += String.format(" %s-%d", TextFormatting.AQUA, getTotemPops(player.getName()));

        return name;
    }

    private int getDisplayColour(EntityPlayer player) {
        int colour = 0xffffffff;

        if (Infinity.INSTANCE.friendManager.isFriend(player.getName()))
            return 0xff55FFFF;

        if (player.isInvisible())
            colour = 0xff910022;
        else if (player.isSneaking())
            colour = -6481515;

        return colour;
    }


    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * delta;
    }
}
