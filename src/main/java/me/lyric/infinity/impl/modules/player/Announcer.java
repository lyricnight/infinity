package me.lyric.infinity.impl.modules.player;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInformation(getName = "Announcer", getDescription = "we ANNOUNCING out here", category = Category.Player)
public class Announcer extends Module {
    public Setting<String> client = register(new Setting<>("Name", "Name to use in announcer.", "Infinity"));
    public static String[] breakMessages;
    public static String[] placeMessages;
    public static String[] eatMessages;
    public static String[] walkMessages;
    public Setting<Integer> delay = register(new Setting<>("Delay", "dumb", 5, 1, 15));
    public static int blockBrokeDelay;
    static int blockPlacedDelay;
    static int jumpDelay;
    static int attackDelay;
    static int eattingDelay;
    static long lastPositionUpdate;
    static double lastPositionX;
    static double lastPositionY;
    static double lastPositionZ;
    private static double speed;
    String heldItem;
    int blocksPlaced;
    int blocksBroken;
    int eaten;

    public Announcer()
    {
        super("Announcer", "Announces things to be annoying.", Category.PLAYER);
    }

    @EventListener
    public void onPacketSend(PacketEvent event) {
        if (!nullSafe()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            blocksPlaced++;
            final int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
            if (blockPlacedDelay >= 150 * ((Number)this.delay.getValue()).intValue() && this.blocksPlaced > randomNum) {
                Random random = new Random();
                String msg = placeMessages[random.nextInt(placeMessages.length)].replace("{amount}", "" + this.blocksPlaced).replace("{name}", "" + mc.player.getHeldItemMainhand().getDisplayName());
                sendMessage(msg);
                blocksPlaced = 0;
                blockPlacedDelay = 0;
            }
        }
    }

    public void onBreakBlock(BlockPos pos) {
        blocksBroken++;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
        if (blockBrokeDelay >= 300 * delay.getValue() && blocksBroken > randomNum) {
            Random random = new Random();
            String msg = breakMessages[random.nextInt(breakMessages.length)].replace("{amount}", "" + blocksBroken).replace("{name}", "" + mc.world.getBlockState(pos).getBlock().getLocalizedName());
            sendMessage(msg);
            blocksBroken = 0;
            blockBrokeDelay = 0;
        }
    }
    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent ignored)
    {
        eaten = 0;
        blocksPlaced = 0;
        blocksBroken = 0;
        lastPositionX = 0;
        lastPositionY = 0;
        lastPositionZ = 0;
        speed = 0;
        blockBrokeDelay = 0;
        blockPlacedDelay = 0;
        jumpDelay = 0;
        attackDelay = 0;
        eattingDelay = 0;
    }

    @Override
    public void onUpdate() {
        if (!nullSafe()) {
            return;
        }
        breakMessages = new String[] { "I just mined {amount} {name} thanks to " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0434\u043e\u0431\u044b\u043b {amount} {name} \u0431\u043b\u043e\u043a\u0430 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!" };;
        placeMessages = new String[] { "I just placed {amount} {name} thanks to " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u043f\u043e\u0441\u0442\u0440\u043e\u0438\u043b \u0437\u0430\u043c\u043e\u043a \u0438\u0437 {amount} {name} \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!" };
        eatMessages = new String[] { "I just ate {amount} {name} thanks to " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0441\u044a\u0435\u043b {amount} {name} \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() + "!" };
        walkMessages = new String[] { "I just teleported {blocks} blocks thanks to " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() +  "!", "\u042f \u043f\u0440\u043e\u0441\u0442\u043e \u0432\u043e\u043b\u0448\u0435\u0431\u043d\u044b\u043c \u043e\u0431\u0440\u0430\u0437\u043e\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043b {blocks} \u0431\u043b\u043e\u043a\u043e\u0432 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f " + Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).client.getValue() +"!" };
        ++blockBrokeDelay;
        ++blockPlacedDelay;
        ++jumpDelay;
        ++attackDelay;
        ++eattingDelay;
        this.heldItem = mc.player.getHeldItemMainhand().getDisplayName();
        ++blockBrokeDelay;
        ++blockPlacedDelay;
        ++jumpDelay;
        ++attackDelay;
        ++eattingDelay;
        this.heldItem = mc.player.getHeldItemMainhand().getDisplayName();
        if (lastPositionUpdate + 5000L * delay.getValue() < System.currentTimeMillis()) {
            final double d0 = lastPositionX - mc.player.lastTickPosX;
            final double d2 = lastPositionY - mc.player.lastTickPosY;
            final double d3 = lastPositionZ - mc.player.lastTickPosZ;
            speed = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
            if (speed > 1.0 && speed <= 5000.0) {
                String walkAmount = new DecimalFormat("0.00").format(speed);
                Random random = new Random();
                sendMessage(walkMessages[random.nextInt(walkMessages.length)].replace("{blocks}", "" + walkAmount));
                lastPositionUpdate = System.currentTimeMillis();
                lastPositionX = mc.player.lastTickPosX;
                lastPositionY = mc.player.lastTickPosY;
                lastPositionZ = mc.player.lastTickPosZ;
            }
        }
    }

    @SubscribeEvent
    public void onEntityEat(LivingEntityUseItemEvent.Finish event) {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
        if (event.getEntity() == mc.player && (event.getItem().getItem() instanceof ItemFood || event.getItem().getItem() instanceof ItemAppleGold)) {
            boolean off = false;
            if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold) && (mc.player.getHeldItemOffhand().getItem() instanceof ItemAppleGold))
            {
                off = true;
            }
            ++eaten;
            if (eattingDelay >= 300 * delay.getValue() && this.eaten > randomNum) {
                Random random = new Random();
                if (off)
                {
                    sendMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", "" + this.eaten).replace("{name}", "" + mc.player.getHeldItemOffhand().getDisplayName()));
                }
                else
                {
                    sendMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", "" + this.eaten).replace("{name}", "" + mc.player.getHeldItemMainhand().getDisplayName()));
                }
                eaten = 0;
                eattingDelay = 0;
            }
        }
    }
    public static void sendMessage(final String message) {
        mc.player.connection.sendPacket(new CPacketChatMessage(message));
    }

}
