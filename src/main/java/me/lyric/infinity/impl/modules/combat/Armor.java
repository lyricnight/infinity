package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.GameLoopEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.client.WindowUtil;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.function.Function;

/**
 * @author 3arth
 * skidded by lyric
 */
//TODO: fix bugs
public class Armor extends Module {

    public Setting<Integer> delay = register(new Setting<>("Delay", "Delay between actions.", 50, 1, 500));

    public Setting<Boolean> auto = register(new Setting<>("AutoMend", "Mends automatically.", false));

    public Setting<Boolean> single = register(new Setting<>("SingleMend", "One armor piece at a time?", false).withParent(auto));

    public Setting<Integer> helm = register(new Setting<>("Helmet", "Helmet mending threshold.", 80, 1, 100).withParent(auto));

    public Setting<Integer> chest = register(new Setting<>("Chest", "Chest mending threshold.", 80, 1, 100).withParent(auto));

    public Setting<Integer> leg = register(new Setting<>("Leggings", "Leggings mending threshold.", 80, 1, 100).withParent(auto));

    public Setting<Integer> boots = register(new Setting<>("Boots", "Boots mending threshold.", 80, 1, 100).withParent(auto));

    public Setting<Boolean> safety = register(new Setting<>("SafetyCheck", "Whether to check if it is safe to mend or not.", true).withParent(auto));

    public Setting<Boolean> drag = register(new Setting<>("DragXCarry", "Uses XCarry slots.", false).withParent(auto));

    public Setting<Boolean> putBack = register(new Setting<>("AntiStuck", "", true).withParent(drag));

    public Setting<Boolean> doubleClick = register(new Setting<>("DoubleClick", "Clicks twice when swapping.", false).withParent(auto));

    public Setting<Boolean> strict = register(new Setting<>("Strict", "Avoids the XCarry slots.", false).withParent(auto));

    public Setting<Boolean> pause = register(new Setting<>("Pause", "Pauses when in inventories.", true).withParent(auto));
    public Armor()
    {
        super("Armor", "Module does not work - here for testing purposes.", Category.COMBAT);
        this.damages = new Setting[]{helm, chest, leg, boots};
    }

    Timer timer = new Timer();

    protected final Queue<WindowUtil> windowClicks = new LinkedList<>();
    protected Set<Integer> queuedSlots = new HashSet<>();

    protected Setting<?>[] damages;
    protected WindowUtil putBackClick;

    protected boolean stackSet;

    protected MendingStage stage = MendingStage.MENDING;
    protected final Timer mendingTimer = new Timer();

    protected final SingleMendingSlot[] singleMendingSlots = {
            new SingleMendingSlot(EntityEquipmentSlot.HEAD),
            new SingleMendingSlot(EntityEquipmentSlot.CHEST),
            new SingleMendingSlot(EntityEquipmentSlot.LEGS),
            new SingleMendingSlot(EntityEquipmentSlot.FEET)
    };

    @Override
    public void onEnable() {
        ChatUtils.sendMessage("This module is still in development and might not work!");
        stage = MendingStage.MENDING;
        windowClicks.clear();
        queuedSlots.clear();
        putBackClick = null;
        unblockMendingSlots();
    }

    @Override
    public void onDisable() {
        stage = MendingStage.MENDING;
        windowClicks.clear();
        queuedSlots.clear();
        putBackClick = null;
        unblockMendingSlots();
    }
    @Override
    public String getDisplayInfo()
    {
        if(auto.getValue())
        {
            return ChatFormatting.GREEN + "true" + ChatFormatting.WHITE + ", " + delay.getValue();
        }
        return ChatFormatting.RED + "false" + ChatFormatting.WHITE + ", " + delay.getValue();
    }

    @EventListener
    public void onGameLoop(GameLoopEvent ignored)
    {
        runClick();
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent ignored)
    {
        if (!nullSafe()) {
            stage = MendingStage.MENDING;
            putBackClick = null;
            return;
        }

        stackSet = false;
        queuedSlots.clear();
        windowClicks.clear();

        if (pause.getValue() && mc.currentScreen instanceof GuiInventory) {
            return;
        }

        if (RenderUtils.validScreen()) {
            if (canAutoMend()) {
                queuedSlots.add(-2);
                ItemStack setStack = setStack();
                boolean setStackIsNull = setStack == null;
                boolean singleMend = single.getValue();
                if (setStack == null) {
                    if (!singleMend) {
                        return;
                    }

                    setStack = mc.player.inventory.getItemStack();
                    queuedSlots.remove(-2);
                }

                int mendBlock = 25;
                if (stage != MendingStage.MENDING) {
                    if (setStackIsNull || isFull()) {
                        stage = MendingStage.MENDING;
                        return;
                    }

                    if (stage == MendingStage.BLOCK) {
                        if (mendingTimer.passedMs(mendBlock)) {
                            stage = MendingStage.TAKEOFF;
                            mendingTimer.reset();
                        } else {
                            return;
                        }
                    }

                    if (stage == MendingStage.TAKEOFF && mendingTimer.passedMs(50)) {
                        stage = MendingStage.MENDING;
                    }
                }

                if (singleMend) {
                    doSingleMend(setStack, mendBlock);
                } else {
                    doNormalMend(setStack, mendBlock);
                }
            } else {
                stage = MendingStage.MENDING;
                unblockMendingSlots();
                Map<EntityEquipmentSlot, Integer> map = setup(!strict.getValue());
                int last = -1;
                ItemStack drag = mc.player.inventory.getItemStack();
                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet()) {
                    if (entry.getValue() == 8) {
                        int slot = fromEquipment(entry.getKey());
                        if (slot != -1 && slot != 45) {
                            ItemStack inSlot = InventoryUtil.get(slot);
                            queueClick(slot, inSlot, drag);
                            drag = inSlot;
                            last = slot;
                        }

                        map.remove(entry.getKey());
                        break;
                    }
                }

                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet()) {
                    int slot = fromEquipment(entry.getKey());
                    if (slot != -1 && slot != 45 && entry.getValue() != null) {
                        int i = entry.getValue();
                        ItemStack inSlot = InventoryUtil.get(i);
                        queueClick(i, inSlot, drag).setDoubleClick(doubleClick.getValue());

                        if (!drag.isEmpty()) {
                            queuedSlots.add(i);
                        }

                        drag = inSlot;
                        inSlot = InventoryUtil.get(slot);
                        queueClick(slot, inSlot, drag);
                        drag = inSlot;
                        last = slot;
                    }
                }
                if (putBack.getValue()) {
                    if (last != -1) {
                        ItemStack stack = InventoryUtil.get(last);
                        if (!stack.isEmpty()) {
                            queuedSlots.add(-2);
                            int air = findItem(Items.AIR, !strict.getValue(), queuedSlots);
                            if (air != -1) {
                                ItemStack inSlot = InventoryUtil.get(air);
                                putBackClick = queueClick(air, inSlot, drag);

                                putBackClick.addPost(() -> putBackClick = null);
                            }
                        }
                    } else if (putBackClick != null && putBackClick.isValid()) {
                        queueClick(putBackClick);
                    } else {
                        putBackClick = null;
                    }
                }
            }
        } else {
            stage = MendingStage.MENDING;
        }

        runClick();
    }
    private boolean checkMendingStage(int mendBlock) {
        if (mendBlock > 0 && stage == MendingStage.MENDING) {
            stage = MendingStage.BLOCK;
            mendingTimer.reset();
            return true;
        }

        return false;
    }

    private boolean isFull() {
        boolean added = false;
        if (!drag.getValue()) {
            added = queuedSlots.add(-2);
        }

        boolean result = findItem(Items.AIR, !strict.getValue(), queuedSlots) == -1;
        if (added) {
            queuedSlots.remove(-2);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void doNormalMend(ItemStack dragIn, int mendBlock) {
        List<DamageStack> stacks = new ArrayList<>(4);
        for (int i = 5; i < 9; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) != 0) {
                float percent = RenderUtils.getPercent(stack);
                if (percent > ((Setting<Integer>) damages[i - 5]).getValue()) {
                    stacks.add(new DamageStack(stack, percent, i));
                }
            }
        }

        stacks.sort(DamageStack::compareTo);
        MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
        for (DamageStack stack : stacks) {
            if (checkDamageStack(stack, mendBlock, drag)) {
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doSingleMend(ItemStack dragIn, int mendBlock) {
        boolean allBlocked = true;
        for (SingleMendingSlot singleMendingSlot : singleMendingSlots) {
            allBlocked = allBlocked && singleMendingSlot.isBlocked();
        }

        if (allBlocked) {
            unblockMendingSlots();
        }

        List<DamageStack> stacks = new ArrayList<>(4);
        for (int i = 5; i < 9; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) != 0) {
                float percent = RenderUtils.getPercent(stack);
                stacks.add(new DamageStack(stack, percent, i));
            }
        }

        stacks.sort(DamageStack::compareTo);
        if (stacks.size() <= 0) {
            int bestSlot = -1;
            MutableWrapper<Float> lowest = new MutableWrapper<>(Float.MAX_VALUE);
            MutableWrapper<ItemStack> bestStack = new MutableWrapper<>(ItemStack.EMPTY);
            for (SingleMendingSlot singleMendingSlot : singleMendingSlots) {
                if (!singleMendingSlot.isBlocked()) {
                    int slot = iterateItems(!strict.getValue(), queuedSlots, stack -> {
                        if (getSlot(stack) == singleMendingSlot.getSlot()) {
                            float percent = RenderUtils.getPercent(stack);
                            if (percent < lowest.get()) {
                                bestStack.set(stack);
                                lowest.set(percent);
                                return true;
                            }
                        }

                        return false;
                    });

                    bestSlot = slot == -1 ? bestSlot : slot;
                }
            }

            if (bestSlot != -1 && lowest.get() < 100.0f) {
                EntityEquipmentSlot equipmentSlot = getSlot(bestStack.get());
                if (equipmentSlot != null) {
                    int slot = fromEquipment(equipmentSlot);
                    if (bestSlot != -2) {
                        queueClick(bestSlot, bestStack.get(), dragIn, slot).setDoubleClick(doubleClick.getValue());
                    }

                    queueClick(slot, InventoryUtil.get(slot), bestStack.get());
                }
            }
            else if (!allBlocked)
            {
                unblockMendingSlots();
            }
        } else if (stacks.size() == 1) {
            DamageStack stack = stacks.get(0);
            SingleMendingSlot mendingSlot = Arrays.stream(singleMendingSlots)
                    .filter(s -> s.getSlot() == getSlot(stack.getStack()))
                    .findFirst()
                    .orElse(null);
            if (mendingSlot != null && stack.getDamage() > ((Setting<Integer>) damages[stack.getSlot() - 5]).getValue()) {
                MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
                checkDamageStack(stack, mendBlock, drag);
                mendingSlot.setBlocked(true);
            }
        } else {
            MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
            for (DamageStack stack : stacks) {
                if (checkDamageStack(stack, mendBlock, drag)) {
                    return;
                }
            }

            stage = MendingStage.MENDING;
        }
    }

    private boolean checkDamageStack(DamageStack stack, int mendBlock, MutableWrapper<ItemStack> drag) {
        ItemStack sStack = stack.getStack();
        int slot = findItem(Items.AIR, !strict.getValue(), queuedSlots);
        if (slot == -1) {
            if (this.drag.getValue() && (stackSet || mc.player.inventory.getItemStack().isEmpty())) {
                if (checkMendingStage(mendBlock)) {
                    return true;
                }

                queueClick(stack.getSlot(), sStack, drag.get(), -1);
            }

            return true;
        } else if (slot != -2 && mc.player.inventory.getItemStack().isEmpty()) {
            if (checkMendingStage(mendBlock)) {
                return true;
            }

            queueClick(stack.getSlot(), sStack, drag.get(), slot).setDoubleClick(doubleClick.getValue());

            drag.set(sStack);
            ItemStack inSlot = InventoryUtil.get(slot);
            queueClick(slot, inSlot, drag.get());
            queuedSlots.add(slot);
            drag.set(inSlot);
        }

        return false;
    }

    private Map<EntityEquipmentSlot, Integer> setup(boolean xCarry) {
        boolean wearingBlast = false;
        Set<EntityEquipmentSlot> cursed = new HashSet<>(6);
        List<EntityEquipmentSlot> empty = new ArrayList<>(4);
        for (int i = 5; i < 9; i++) {
            ItemStack stack = InventoryUtil.get(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemArmor) {
                    int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack);
                    if (lvl > 0) {
                        wearingBlast = true;
                    }
                } else {
                    empty.add(fromSlot(i));
                }

                if (EnchantmentHelper.hasBindingCurse(stack)) {
                    cursed.add(fromSlot(i));
                }
            } else {
                empty.add(fromSlot(i));
            }
        }

        if (wearingBlast && empty.isEmpty()) {
            return new HashMap<>(1, 1.0f); // 2 for elytra
        }

        Map<EntityEquipmentSlot, LevelStack> map = new HashMap<>(6);
        Map<EntityEquipmentSlot, LevelStack> blast = new HashMap<>(6);

        for (int i = 8; i < 45; i++) {
            if (i == 5) {
                i = 9;
            }

            ItemStack stack = getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
                float d = RenderUtils.getDamage(stack);
                ItemArmor armor = (ItemArmor) stack.getItem();
                EntityEquipmentSlot type = armor.getEquipmentSlot();
                int blastLvL = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack);

                if (blastLvL != 0) {
                    compute(stack, blast, type, i, blastLvL, d);
                }

                int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);

                if (blastLvL != 0) {
                    if (lvl >= 4) {
                        lvl += blastLvL;
                    } else {
                        continue;
                    }
                }

                compute(stack, map, type, i, lvl, d);
            }

            if (i == 8 && xCarry) {
                i = 0;
            }
        }

        Map<EntityEquipmentSlot, Integer> result = new HashMap<>(6);
        if (wearingBlast) {
            for (EntityEquipmentSlot slot : empty) {
                if (map.get(slot) == null) {
                    LevelStack e = blast.get(slot);
                    if (e != null) {
                        map.put(slot, e);
                    }
                }
            }

            map.keySet().retainAll(empty);
            map.forEach((key, value) -> result.put(key, value.getSlot()));
        } else {
            boolean foundBlast = false;
            List<EntityEquipmentSlot> both = new ArrayList<>(4);
            for (EntityEquipmentSlot slot : empty) {
                LevelStack b = blast.get(slot);
                LevelStack p = map.get(slot);

                if (b == null && p != null) {
                    result.put(slot, p.getSlot());
                } else if (b != null && p == null) {
                    foundBlast = true;
                    result.put(slot, b.getSlot());
                } else if (b != null) {
                    both.add(slot);
                }
            }

            for (EntityEquipmentSlot b : both) {
                if (foundBlast) {
                    result.put(b, map.get(b).getSlot());
                } else {
                    foundBlast = true;
                    result.put(b, blast.get(b).getSlot());
                }
            }

            if (!foundBlast && !blast.isEmpty()) {
                Optional<Map.Entry<EntityEquipmentSlot, LevelStack>> first =
                        blast.entrySet()
                                .stream()
                                .filter(e -> !cursed.contains(e.getKey()))
                                .findFirst();

                first.ifPresent(e -> result.put(e.getKey(), e.getValue().getSlot()));
            }
        }

        return result;
    }

    private ItemStack getStack(int slot) {
        if (slot == 8) {
            return mc.player.inventory.getItemStack();
        }

        return InventoryUtil.get(slot);
    }

    private void compute(ItemStack stack, Map<EntityEquipmentSlot, LevelStack> map, EntityEquipmentSlot type, int slot, int level, float damage) {
        map.compute(type, (k, v) -> {
            if (v == null || !v.isBetter(damage, 35.0f, level, true)) {
                return new LevelStack(stack, damage, slot, level);
            }

            return v;
        });
    }

    protected void unblockMendingSlots() {
        for (SingleMendingSlot mendingSlot : singleMendingSlots) {
            mendingSlot.setBlocked(false);
        }
    }
    protected WindowUtil queueClick(int slot, ItemStack inSlot, ItemStack inDrag) {
        return queueClick(slot, inSlot, inDrag, slot);
    }

    protected WindowUtil queueClick(int slot, ItemStack inSlot, ItemStack inDrag, int target) {
        WindowUtil click = new WindowUtil(slot, inSlot, inDrag, target);
        queueClick(click);
        click.setFast(strict.getValue());
        return click;
    }

    protected void queueClick(WindowUtil click) {
        windowClicks.add(click);
    }

    protected void runClick() {
        if (RenderUtils.validScreen() && mc.playerController != null) {
            if (timer.passedMs(delay.getValue())) {
                WindowUtil windowClick = windowClicks.poll();
                while (windowClick != null) {
                    if (safety.getValue() && !windowClick.isValid()) {
                        windowClicks.clear();
                        queuedSlots.clear();
                        return;
                    }

                    windowClick.runClick(mc.playerController);
                    timer.reset();

                    if (!windowClick.isDoubleClick()) {
                        return;
                    }

                    windowClick = windowClicks.poll();
                }
            }
        } else {
            windowClicks.clear();
            queuedSlots.clear();
        }
    }

    protected ItemStack setStack() {
        if (!stackSet) {
            ItemStack drag = mc.player.inventory.getItemStack();
            if (!drag.isEmpty()) {
                int slot = findItem(Items.AIR, !strict.getValue(), queuedSlots);
                if (slot != -1) {
                    ItemStack inSlot = InventoryUtil.get(slot);
                    queueClick(slot, drag, inSlot);
                    queuedSlots.add(slot);
                    stackSet = true;
                    return inSlot;
                }

                return null;
            }

            stackSet = true;
            return drag;
        }

        return null;
    }

    protected boolean canAutoMend() {
        if (!auto.getValue() || (!Mouse.isButtonDown(1) || !InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE))) {
            return false;
        }

        EntityPlayer closestPlayer = CombatUtil.getTarget(MathUtils.square((int) (4.5F * 2)));
        if (closestPlayer != null) {
            return false;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(mc.player) < MathUtils.square((int) (6.5F * 2)) && !entity.isDead && mc.player.getDistanceSq(entity) <= 144) {
                return false;
            } else if (entity instanceof EntityEnderPearl && entity.getDistanceSq(mc.player) < MathUtils.square((int) (32.5F * 2)) && !entity.isDead && mc.player.getDistanceSq(entity) <= 144) {
                return false;
            }
        }

        return true;
    }

    protected static EntityEquipmentSlot fromSlot(int slot) {
        switch (slot) {
            case 5:
                return EntityEquipmentSlot.HEAD;
            case 6:
                return EntityEquipmentSlot.CHEST;
            case 7:
                return EntityEquipmentSlot.LEGS;
            case 8:
                return EntityEquipmentSlot.FEET;
            default:
                ItemStack stack = InventoryUtil.get(slot);
                return getSlot(stack);
        }
    }

    protected static int fromEquipment(EntityEquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case OFFHAND:
                return 45;
            case FEET:
                return 8;
            case LEGS:
                return 7;
            case CHEST:
                return 6;
            case HEAD:
                return 5;
            default:
        }

        return -1;
    }

    protected static EntityEquipmentSlot getSlot(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                return armor.getEquipmentSlot();
            } else if (stack.getItem() instanceof ItemElytra) {
                return EntityEquipmentSlot.CHEST;
            }
        }

        return null;
    }

    public static int findItem(Item item, boolean xCarry, Set<Integer> blackList) {
        ItemStack drag = mc.player.inventory.getItemStack();
        if (!drag.isEmpty() && drag.getItem() == item && !blackList.contains(-2)) {
            return -2;
        }

        for (int i = 9; i < 45; i++) {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.getItem() == item && !blackList.contains(i)) {
                return i;
            }
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                ItemStack stack = InventoryUtil.get(i);
                if (stack.getItem() == item && !blackList.contains(i)) {
                    return i;
                }
            }
        }

        return -1;
    }

    protected static int iterateItems(boolean xCarry, Set<Integer> blackList, Function<ItemStack, Boolean> accept) {
        ItemStack drag = mc.player.inventory.getItemStack();
        if (!drag.isEmpty() && !blackList.contains(-2) && accept.apply(drag)) {
            return -2;
        }

        for (int i = 9; i < 45; i++) {
            ItemStack stack = InventoryUtil.get(i);
            if (!blackList.contains(i) && accept.apply(stack)) {
                return i;
            }
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                ItemStack stack = InventoryUtil.get(i);
                if (!blackList.contains(i) && accept.apply(stack)) {
                    return i;
                }
            }
        }

        return -1;
    }














    private class SingleMendingSlot {
        private final EntityEquipmentSlot slot;
        private boolean blocked;

        public SingleMendingSlot(EntityEquipmentSlot slot) {
            this.slot = slot;
        }

        public EntityEquipmentSlot getSlot() {
            return slot;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }

    }
    public class DamageStack implements Comparable<DamageStack> {
        private final ItemStack stack;
        private final float damage;
        private final int slot;

        public DamageStack(ItemStack stack, float damage, int slot) {
            this.stack = stack;
            this.damage = damage;
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public float getDamage() {
            return damage;
        }

        public ItemStack getStack() {
            return stack;
        }

        @Override
        public int compareTo(DamageStack o) {
            return Float.compare(o.damage, this.damage);
        }

    }
    public class LevelStack extends DamageStack {
        private final int level;

        public LevelStack(ItemStack stack, float damage, int slot, int level) {
            super(stack, damage, slot);
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public boolean isBetter(float damage, float min, int level, boolean prio) {
            if (level > this.level) {
                return false;
            } else if (level < this.level) {
                return true;
            }

            if (prio) {
                return !(damage > min) || !(damage < this.getDamage());
            }

            return !(damage > this.getDamage());
        }

    }
    public static class MutableWrapper<T> {
        protected T value;

        public MutableWrapper() {
            this(null);
        }

        public MutableWrapper(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }
    }
    public enum MendingStage {
        MENDING,
        BLOCK,
        TAKEOFF
    }
}
