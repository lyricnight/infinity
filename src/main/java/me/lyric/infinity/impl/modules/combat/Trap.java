package me.lyric.infinity.impl.modules.combat;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.player.MotionUpdateEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.FloatSetting;
import me.lyric.infinity.api.setting.settings.IntegerSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.*;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.manager.Managers;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lyric
 */

@ModuleInformation(name = "Trap", description = "TRAPSTAR LONDON LOL", category = Category.Combat)
public class Trap extends Module {

    private static final EnumFacing[] TOP_FACINGS = new EnumFacing[] {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST};

    public static final Vec3i[] OFFSETS = new Vec3i[]
            {
                    new Vec3i(1, 0, 0),
                    new Vec3i(0, 0, 1),
                    new Vec3i(-1, 0, 0),
                    new Vec3i(0, 0, -1)
            };

    public static final Vec3i[] NO_STEP = new Vec3i[]
            {
                    new Vec3i(1, 1, 0),
                    new Vec3i(0, 1, 1),
                    new Vec3i(-1, 1, 0),
                    new Vec3i(0, 1, -1)
            };

    public static final Vec3i[] TOP = new Vec3i[]
            {
                    new Vec3i(0, 1, 0)
            };

    public BooleanSetting rots = createSetting("Rotations", false);
    public IntegerSetting delay = createSetting("Delay", 1, 0, 10);

    public IntegerSetting bpt = createSetting("BPT", 3, 1, 10);

    public BooleanSetting strict = createSetting("StrictDirection", false);

    public FloatSetting strictRange = createSetting("Strict-Range", 3.0f, 1.0f, 5.0f);

    public ModeSetting switchType = createSetting("Swap", "Silent", Arrays.asList("Silent", "SilentPacket", "Slot"));

    public BooleanSetting altback = createSetting("Slot-Cooldown", false);

    public FloatSetting targetRange = createSetting("Target-Range", 5.0f, 1.0f, 10.0f);

    public FloatSetting placeRange = createSetting("Place-Range", 5.0f, 2.0f, 8.0f);

    public IntegerSetting extend = createSetting("Extend-Amount", 1, 1, 3);

    public BooleanSetting logout = createSetting("Logouts", false);

    public BooleanSetting body = createSetting("Body", true);
    public BooleanSetting face = createSetting("Face", true);

    public BooleanSetting extra = createSetting("Extra", false);

    public BooleanSetting jump = createSetting("JumpDisable", true);

    private double enablePosY;

    private final Map<EntityPlayer, List<BlockPos>> tickCache = new HashMap<>();
    private final Map<EntityPlayer, Double> speeds = new HashMap<>();
    private List<BlockPos> placeList;
    private EntityPlayer target;
    private final List<Packet<?>> packets = new ArrayList<>();
    private final Timer timer = new Timer();
    private float[] rotations;
    private int blocksPlaced = 0;
    private int slot = -1;


    @Override
    public void onDisable()
    {
        tickCache.clear();
        speeds.clear();
        placeList.clear();
        target = null;
    }

    @Override
    public void onEnable()
    {
        enablePosY = mc.player.posY;
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event)
    {
        if(!nullSafe())
        {
            disable();
        }
        if (mc.player.posY != enablePosY)
        {
            disable();
        }
        if (placeList != null)
        {
            placeList.clear();
        }
        target = null;
        getTargets();

        onPreEvent(placeList, event);
    }
    public void onPreEvent(List<BlockPos> blocks, MotionUpdateEvent event) {
        if (event.getStage() == 0) {
            blocksPlaced = 0;
            placeBlocks(blocks);
            if (rotations != null) {
                Managers.ROTATIONS.setRotations(rotations[0], rotations[1]);
                rotations = null;
            }
            execute();
        }
    }

    public void placeBlocks(List<BlockPos> blockList) {
        if (blockList == null || blockList.isEmpty()) {
            return;
        }

        for (BlockPos pos : blockList) {
            placeBlock(pos);
        }
    }

    public void placeBlock(BlockPos pos) {
        getSlot();
        if (slot == -1) {
            return;
        }

        if (!timer.passedMs(delay.getValue() * 50)) {
            return;
        }

        EnumFacing facing = BlockUtil.getFacing(pos);



        if (facing == null) {
            BlockPos helpingPos;
            for (EnumFacing side : EnumFacing.values()) {
                helpingPos = pos.offset(side);
                EnumFacing helpingFacing = BlockUtil.getFacing(helpingPos);
                if (helpingFacing != null) {
                    facing = side;
                    placeBlock(helpingPos.offset(helpingFacing), helpingFacing.getOpposite());
                    break;
                }
            }
        }


        if (facing == null) {
            return;
        }

        if (blocksPlaced >= bpt.getValue()) {
            return;
        }

        if (mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
            return;
        }

        if (crystalCheck(pos)) {
            timer.reset();
            return;
        }

        if (canPlaceBlock(pos, strict.getValue())) {
            placeBlock(pos.offset(facing), facing.getOpposite());
        }
    }

    private void placeBlock(BlockPos pos, EnumFacing facing) {
        float[] rots = RotationUtil.getRotations(pos, facing);
        RayTraceResult result = RaytraceUtil.getRayTraceResult(rots[0], rots[1]);
        placeBlock(pos, facing, rots, result.hitVec);
    }

    private void placeBlock(BlockPos on, EnumFacing facing, float[] helpingRotations, Vec3d hitVec) {
        if (rots.getValue()) {
            if (rotations == null) {
                rotations = helpingRotations;
            }
            packets.add(new CPacketPlayer.Rotation(helpingRotations[0], helpingRotations[1], mc.player.onGround));
        }

        float[] hitRots = RaytraceUtil.hitVecToPlaceVec(on, hitVec);
        packets.add(new CPacketPlayerTryUseItemOnBlock(on, facing, EnumHand.MAIN_HAND, hitRots[0], hitRots[1], hitRots[2]));
        packets.add(new CPacketAnimation(EnumHand.MAIN_HAND));
        blocksPlaced++;
    }

    private void execute() {
        if (!packets.isEmpty()) {
            int lastSlot = mc.player.inventory.currentItem;

            ItemStack oldItem = mc.player.getHeldItemMainhand();

            if (switchType.getValue() == "Slot") {
                Switch.doSwitch(slot, "Slot");
            } else {
                Switch.doSwitch(slot, switchType.getValue());
            }

            ItemStack newItem = mc.player.getHeldItemMainhand();

            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

            mc.player.swingArm(EnumHand.MAIN_HAND);

            packets.forEach(this::send);

            packets.clear();
            timer.reset();

            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

            if (altback.getValue() && switchType.getValue() == "Slot") {
                //is this right??
                Switch.switchBackAlt(slot);

            } else {
                Switch.doSwitch(lastSlot, switchType.getValue());
            }
        }
    }

    private boolean crystalCheck(BlockPos pos) {
        CPacketUseEntity attackPacket = null;
        float currentDmg = Float.MAX_VALUE;
        float[] angles = null;
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || EntityUtil.isLiving(entity)) {
                continue;
            }

            if (entity instanceof EntityEnderCrystal) {
                float damage = CombatUtil.calculate(entity, mc.player);
                if (damage < currentDmg) {
                    currentDmg = damage;
                    angles = RotationUtil.getRotations(entity.posX, entity.posY, entity.posZ);
                    attackPacket = new CPacketUseEntity(entity);
                }
            }
        }

        if (attackPacket == null) {
            return false;
        }

        int weaknessSlot = -1;
        int oldSlot = mc.player.inventory.currentItem;
        if (!CombatUtil.canBreakWeakness(true)) {
            if ((weaknessSlot = CombatUtil.findAntiWeakness()) == -1) {
                return true;
            }
        }

        if (weaknessSlot != -1) {
            if (rots.getValue()) {
                if (rotations == null) {
                    rotations = angles;
                }

                send(new CPacketPlayer.Rotation(angles[0], angles[1], mc.player.onGround));
            }

            Switch.doSwitch(weaknessSlot, switchType.getValue());

            send(attackPacket);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            Switch.doSwitch(oldSlot, switchType.getValue());

            return false;
        }

        if (rots.getValue()) {
            if (rotations == null) {
                rotations = angles;
            }

            packets.add(new CPacketPlayer.Rotation(angles[0], angles[1], mc.player.onGround));
        }

        packets.add(attackPacket);
        packets.add(new CPacketAnimation(EnumHand.MAIN_HAND));
        return false;
    }

    private boolean canPlaceBlock(BlockPos pos, boolean strict) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return false;
        }
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity instanceof EntityEnderCrystal)
                continue;
            return false;
        }

        for (EnumFacing side : getPlacableFacings(pos, strict)) {
            if (!canClick(pos.offset(side))) continue;
            return true;
        }

        return false;
    }

    private boolean canClick(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    private List<EnumFacing> getPlacableFacings(BlockPos pos, boolean strict) {
        ArrayList<EnumFacing> validFacings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            if (strict && mc.player.getDistanceSq(pos) > MathUtils.square(strictRange.getValue())) {
                Vec3d testVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5)); //TODO: this can be more accurate
                RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1F), testVec);
                if (result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
                    continue;
                }
            }
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if ((!blockState.getBlock().canCollideCheck(blockState, false) || blockState.getMaterial().isReplaceable())) {
                continue;
            }

            validFacings.add(side);
        }
        return validFacings;
    }

    private void getSlot() {
        int obi = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echest = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        slot = obi != -1 ? obi : echest;
    }


    protected void getTargets() {
        tickCache.clear();
        updateSpeed();
        EntityPlayer newTarget = calcTarget();

        target = newTarget;
        if (newTarget == null) {
            return;
        }

        List<BlockPos> newTrapping = tickCache.get(newTarget);
        if (newTrapping == null) {
            newTrapping = getPositions(newTarget);
        }

        placeList = newTrapping;
    }

    private List<BlockPos> getPositions(EntityPlayer player) {
        List<BlockPos> blocked = new ArrayList<>();
        BlockPos playerPos = new BlockPos(player);
        if (HoleUtil.isHole(playerPos) || extend.getValue() == 1) {
            blocked.add(playerPos.up());
        } else {
            List<BlockPos> unfiltered = new ArrayList<>(CombatUtil.getBlockedPositions(player)).stream().sorted(Comparator.comparingDouble(BlockUtil::getDistanceSq)).collect(Collectors.toList());
            List<BlockPos> filtered = new ArrayList<>(unfiltered).stream().filter(pos -> mc.world.getBlockState(pos).getMaterial().isReplaceable() && mc.world.getBlockState(pos.up()).getMaterial().isReplaceable()).collect(Collectors.toList());

            if (extend.getValue() == 3 && filtered.size() == 2 && unfiltered.size() == 4) {
                if (unfiltered.get(0).equals(filtered.get(0)) && unfiltered.get(3).equals(filtered.get(1))) {
                    filtered.clear();
                }
            }

            if (extend.getValue() == 2 && filtered.size() > 2 || extend.getValue() == 3 && filtered.size() == 3) {
                while (filtered.size() > 2) {
                    filtered.remove(filtered.size() - 1);
                }
            }

            for (BlockPos pos : filtered) {
                blocked.add(pos.up());
            }
        }

        if (blocked.isEmpty()) {
            blocked.add(playerPos.up());
        }

        List<BlockPos> positions = positionsFromBlocked(blocked);
        positions.sort(Comparator.comparingDouble(pos -> -BlockUtil.getDistanceSq(pos)));
        positions.sort(Comparator.comparingInt(Vec3i::getY));

        return positions.stream().filter(pos -> BlockUtil.getDistanceSq(pos) <= MathUtils.square(placeRange.getValue())).collect(Collectors.toList());
    }

    private List<BlockPos> positionsFromBlocked(List<BlockPos> blockedIn) {
        List<BlockPos> positions = new ArrayList<>();
        if (!extra.getValue() && !blockedIn.isEmpty()) {
            BlockPos[] helping = findTopHelping(blockedIn, true);
            for (int i = 0; i < helping.length; i++) {
                BlockPos pos = helping[i];
                if (pos != null) {
                    if (i == 1 && !body.getValue() && (!blockedIn.contains(EntityUtil.getPlayerPos().up()) || !face.getValue()) && helping[5] != null) {
                        positions.add(helping[5]);
                    }

                    positions.add(helping[i]);
                    break;
                }
            }
        }

        blockedIn.forEach(pos -> positions.addAll(applyOffsets(pos, Trap.TOP, positions)));

        if (body.getValue() || face.getValue() && blockedIn.contains(EntityUtil.getPlayerPos().up())) {
            blockedIn.forEach(pos -> positions.addAll(applyOffsets(pos, Trap.OFFSETS, positions)));
        }

        if (blockedIn.size() == 1) {
            if (extra.getValue()) {
                blockedIn.forEach(pos -> positions.addAll(applyOffsets(pos, Trap.NO_STEP, positions)));
            }
        }

        return positions;
    }

    private List<BlockPos> applyOffsets(BlockPos pos, Vec3i[] offsets, List<BlockPos> alreadyAdded) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        for (Vec3i vec3i : offsets) {
            BlockPos offset = pos.add(vec3i);
            if (alreadyAdded.contains(offset)) continue;
            positions.add(offset);
        }
        return positions;
    }

    private BlockPos[] findTopHelping(List<BlockPos> positions, boolean first) {
        BlockPos[] bestPos = new BlockPos[] {null, null, null, null, positions.get(0).up().north(), null};
        for (BlockPos pos : positions) {
            BlockPos up = pos.up();
            for (EnumFacing facing : TOP_FACINGS) {
                BlockPos helping = up.offset(facing);
                if (!mc.world.getBlockState(helping).getMaterial().isReplaceable()) {
                    bestPos[0] = helping;
                    return bestPos;
                }

                EnumFacing helpingFace = BlockUtil.getFacing(helping);
                byte blockingFactor = helpingEntityCheck(helping);
                if (helpingFace == null) {
                    switch (blockingFactor) {
                        case 0:
                            if (first && bestPos[5] == null) {
                                List<BlockPos> hPositions = new ArrayList<>();
                                for (BlockPos hPos : positions) {
                                    hPositions.add(hPos.down());
                                }

                                bestPos[5] = findTopHelping(hPositions, false)[0];
                            } else {
                                break;
                            }

                            bestPos[1] = helping;
                            break;
                        case 1:
                            bestPos[3] = helping;
                            break;
                        case 2:
                            break;
                    }
                } else {
                    switch (blockingFactor) {
                        case 0:
                            bestPos[0] = helping;
                            break;
                        case 1:
                            bestPos[2] = helping;
                            break;
                        case 2:
                            break;
                    }
                }
            }
        }

        return bestPos;
    }

    private byte helpingEntityCheck(BlockPos pos) {
        byte blocking = 0;
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || (EntityUtil.getHealth(entity) <= 0.0f) || !entity.preventEntitySpawning || (entity instanceof EntityPlayer && !entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos)))) {
                continue;
            }

            return 2;
        }

        return blocking;
    }

    protected EntityPlayer calcTarget() {
        EntityPlayer closest = null;
        double distance = Double.MAX_VALUE;
        for (EntityPlayer player : mc.world.playerEntities) {
            double playerDist = mc.player.getDistanceSq(player);
            if (playerDist < distance && isValid(player)) {
                closest = player;
            }
        }

        return closest;
    }

    private boolean isValid(EntityPlayer player) {
        if (player != null && EntityUtil.isLiving(player) && !player.equals(mc.player) && !Managers.FRIENDS.isFriend(player.getDisplayNameString()) && player.getDistanceSq(mc.player) <= MathUtils.square(targetRange.getValue())) {
            if (getSpeed(player) <= 22.0F) {
                List<BlockPos> positions = getPositions(player);
                tickCache.put(player, positions);
                return positions.stream().anyMatch(pos -> mc.world.getBlockState(pos).getMaterial().isReplaceable());
            }
            return true;
        }
        return false;
    }

    protected void updateSpeed() {
        for (EntityPlayer player : mc.world.playerEntities) {
            double xDist = player.posX - player.prevPosX;
            double yDist = player.posY - player.prevPosY;
            double zDist = player.posZ - player.prevPosZ;
            double speed = xDist * xDist + yDist * yDist + zDist * zDist;

            speeds.put(player, speed);
        }
    }

    private double getSpeed(EntityPlayer player) {
        Double playerSpeed = speeds.get(player);
        if (playerSpeed != null) {
            return Math.sqrt(playerSpeed) * 20 * 3.6;
        }

        return 0.0;
    }

    public boolean trapLogs() {
        return isEnabled() && logout.getValue();
    }

    public void send(Packet<?> packet)
    {
        mc.player.connection.sendPacket(packet);
    }

}
