package me.lyric.infinity.impl.modules.combat;

import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.misc.RightClickItemEvent;
import me.lyric.infinity.api.event.player.MotionUpdateEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.Bind;
import me.lyric.infinity.api.util.client.EntityUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;

/**
 * @author lyric
 * will this work? who knows
 */

public class Arrow extends Module {

    public Setting<Bind> cyclebind = register(new Setting<>("Cycle-Bind", "Bind to instantly cycle arrows.", new Bind()));
    public Setting<Integer> rt = register(new Setting<>("Release", "How many ticks before bow is released.", 5, 0, 20));

    public Setting<Integer> timeout = register(new Setting<>("Timeout", "How many ticks before process is aborted.", 10, 0, 20));

    public Setting<Integer> pause = register(new Setting<>("Pause-Time", "Time taken between pulling bow if cycling more than 1 arrow", 50, 0, 1000));

    public Setting<Integer> cycle = register(new Setting<>("Cycle-Time", "Time taken to cycle an arrow type.", 200, 0, 500));

    public Setting<Integer> shoot = register(new Setting<>("Shoot-Delay", "Delay after shooting an arrow (extra delay for less desync)", 500, 0 , 500));

    public Arrow()
    {
        super("Arrow", "Basically just quiver.", Category.COMBAT);
    }
    static PotionType SPECTRAL = new PotionType();

    static Set<PotionType> BAD_TYPES = Sets.newHashSet(
            PotionTypes.EMPTY,
            PotionTypes.WATER,
            PotionTypes.MUNDANE,
            PotionTypes.THICK,
            PotionTypes.AWKWARD,
            PotionTypes.HEALING,
            PotionTypes.STRONG_HEALING,
            PotionTypes.STRONG_HARMING,
            PotionTypes.HARMING
    );

    private Set<String> arStrings = new HashSet<>();

    private ArrayList<PotionType> cycled = new ArrayList<>();

    private Timer cycleTimer = new Timer();

    private Timer timer = new Timer();

    private boolean fast;

    private PotionType lastType;

    private long lastDown;

    private String constant;

    @Override
    public void onEnable()
    {
        fast = false;
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event)
    {
        ItemStack arrow;
        EnumHand hand = InventoryUtil.getHand(Items.BOW);
        if (mc.player.isCreative() || mc.currentScreen != null || hand == null || (arrow = findArrow()).isEmpty() || blocked()) {
            return;
        }
        //this boolean acts as a stop point when cycle-delay hasn't finished
        boolean cycle = true;
        //this if statement is true if the stack has a potion that's not bad and we want to have.
        if (badStack(arrow) || fast) {
            if (!cycle) {
                return;
            }

            //cycles to the arrow
            cycle(false, true);
            fast = false;

            arrow = findArrow();
            if (badStack(arrow)) {
                return;
            }
        }

        if (event.getStage() == 1) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                lastDown = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - lastDown > 100) {
                return;
            }

            EntityPlayer player = mc.player;
            if (player.motionX != 0 || player.motionZ != 0) {
                Vec3d vec3d = player.getPositionVector().add(player.motionX, player.motionY + player.getEyeHeight(), player.motionZ);
                float[] rotations = RotationUtil.getRotations(vec3d);
                mc.player.rotationYaw = rotations[0];
                mc.player.rotationPitch = rotations[1];
            } else {
                mc.player.rotationPitch = (-90.0f);
            }
        } else if (!mc.player.getActiveItemStack().isEmpty()) {
            PotionType type = PotionUtils.getPotionFromItem(arrow);
            if (arrow.getItem() instanceof ItemSpectralArrow) {
                type = SPECTRAL;
            }

            if (lastType == type && !timer.passedMs(shoot.getValue())) {
                return;
            }

            lastType = type;
            float ticks = mc.player.getHeldItem(hand).getMaxItemUseDuration() - mc.player.getItemInUseCount() - 0.0f;
            if (ticks >= rt.getValue() && ticks <= timeout.getValue()) {
                mc.playerController.onStoppedUsingItem(mc.player);
                fast = cycle;
                timer.reset();
            }
        }
    }

    @EventListener
    public void onRightClick(RightClickItemEvent event)
    {
        if (mc.player.getHeldItem(event.getHand()).getItem() instanceof ItemBow && pause.getValue() != 0 && !timer.passedMs(pause.getValue()) && fast) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onInput(InputEvent.KeyInputEvent event)
    {
        if(Keyboard.getEventKeyState())
        {
            if(cyclebind.getValue().getKey() == Keyboard.getEventKey())
            {
                cycle(false, false);
            }
        }
    }



    protected boolean badStack(ItemStack stack) {
        return badStack(stack, true, new ArrayList<>());
    }

    protected boolean badStack(ItemStack stack, boolean checkType, ArrayList<PotionType> cycled) {
        PotionType type = PotionUtils.getPotionFromItem(stack);
        if (stack.getItem() instanceof ItemSpectralArrow) {
            type = SPECTRAL;
        }

        if (cycled.contains(type)) {
            return true;
        }

        if (checkType) {
            if (BAD_TYPES.contains(type)) {
                return true;
            }
        } else if (type.getEffects().isEmpty() && isValid("none")) {
            return false;
        }

        if (stack.getItem() instanceof ItemSpectralArrow) {
            return !isValid("Spectral") || mc.player.isGlowing();
        }

        boolean inValid = true;
        for (PotionEffect e : type.getEffects()) {
            if (!isValid(I18n.format(e.getPotion().getName()))) {
                return true;
            }

            PotionEffect eff = mc.player.getActivePotionEffect(e.getPotion());
            if (eff == null || eff.getDuration() < 200) {
                inValid = false;
            }
        }

        if (!checkType) {
            return false;
        }

        return inValid;
    }

    protected void cycle(boolean recursive, boolean key) {
        if (!InventoryUtil.validScreen() || key && !cycleTimer.passedMs(cycle.getValue())) {
            return;
        }

        int firstSlot = -1;
        int secondSlot = -1;
        ItemStack arrow = null;
        if (isArrow(mc.player.getHeldItem(EnumHand.OFF_HAND))) {
            firstSlot = 45;
        }

        if (isArrow(mc.player.getHeldItem(EnumHand.MAIN_HAND))) {
            if (firstSlot == -1) {
                firstSlot = Switch.convert(mc.player.inventory.currentItem);
            } else if (!badStack(mc.player.getHeldItem(EnumHand.MAIN_HAND), key, cycled)) {
                secondSlot = Switch.convert(mc.player.inventory.currentItem);
                arrow = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            }
        }

        if (!badStack(mc.player.inventory.getItemStack(), key, cycled)) {
            secondSlot = -2;
            arrow = mc.player.inventory.getItemStack();
        }

        if (firstSlot == -1 || secondSlot == -1) {
            for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (!isArrow(stack)) {
                    continue;
                }

                if (firstSlot == -1) {
                    firstSlot = Switch.convert(i);
                } else if (!badStack(stack, key, cycled)) {
                    secondSlot = Switch.convert(i);
                    arrow = stack;
                    break;
                }
            }
        }

        if (firstSlot == -1) {
            return;
        }

        if (secondSlot == -1) {
            if (!recursive && !cycled.isEmpty()) {
                cycled.clear();
                cycle(true, key);
            }

            return;
        }

        PotionType type = PotionUtils.getPotionFromItem(arrow);
        if (arrow.getItem() instanceof ItemSpectralArrow) {
            type = SPECTRAL;

        }
        constant = arrow.getItem().getItemStackDisplayName(arrow).replace("Arrow of ", "").replace(" Arrow", "");

        ChatUtils.sendMessageWithID(ChatFormatting.BOLD + "Arrow cycled to " + arrow.getItem().getItemStackDisplayName(arrow).replace("Arrow of ", "").replace(" Arrow", "") + "!", 8909384);
        cycled.add(type);
        int finalFirstSlot = firstSlot;
        int finalSecondSlot = secondSlot;
        Item inFirst = InventoryUtil.get(finalFirstSlot).getItem();
        Item inSecond = InventoryUtil.get(finalSecondSlot).getItem();
        if (InventoryUtil.get(finalFirstSlot).getItem() == inFirst && InventoryUtil.get(finalSecondSlot).getItem() == inSecond) {
            if (finalSecondSlot == -2) {
                InventoryUtil.click(finalFirstSlot);
            } else {
                InventoryUtil.click(finalSecondSlot);
                InventoryUtil.click(finalFirstSlot);
                InventoryUtil.click(finalSecondSlot);
            }
        }

        cycleTimer.reset();
    }

    //this tells us which itemstack has arrows in it
    protected ItemStack findArrow() {
        if (isArrow(mc.player.getHeldItem(EnumHand.OFF_HAND))) {
            return mc.player.getHeldItem(EnumHand.OFF_HAND);
        } else if (isArrow(mc.player.getHeldItem(EnumHand.MAIN_HAND))) {
            return mc.player.getHeldItem(EnumHand.MAIN_HAND);
        }

        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (isArrow(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean isValid(String string) {
        if (string == null) {
            return false;
        }

        return !arStrings.contains(string.toLowerCase());
    }

    public Collection<String> getList() {
        return arStrings;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }
    private boolean blocked() {
        BlockPos pos = EntityUtil.getPlayerPos();
        return mc.world.getBlockState(pos.up()).getMaterial().blocksMovement() || mc.world.getBlockState(pos.up(2)).getMaterial().blocksMovement();
    }

    @Override
    public String getDisplayInfo()
    {
        if (constant == null) return "";
        return constant;
    }
}



