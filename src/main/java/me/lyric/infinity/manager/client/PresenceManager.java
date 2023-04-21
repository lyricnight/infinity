package me.lyric.infinity.manager.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.impl.modules.client.RPC;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;

/**
 * @author zzurio
 */

public class PresenceManager implements IGlobals {

    final private static DiscordRPC lib = DiscordRPC.INSTANCE;
    final private static DiscordRichPresence presence = new DiscordRichPresence();

    public static void start() {
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("[Infinity] RPC Started!");
        lib.Discord_Initialize("922967411739721808", handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).details.getValue();
        presence.state = "";
        presence.largeImageKey = "infinity";
        presence.largeImageText = Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).largeImageText.getValue();

        lib.Discord_UpdatePresence(presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();

                presence.details = Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).details.getValue();
                presence.state = "";
                presence.largeImageKey = "infinity";
                presence.largeImageText = Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).largeImageText.getValue();

                if (mc.world == null) {
                    presence.details = Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).details.getValue();
                    if (mc.currentScreen instanceof GuiWorldSelection) {
                        presence.state = "Selecting a world.";
                    } else if (mc.currentScreen instanceof GuiMainMenu) {
                        presence.state = "In the main menu.";
                    } else if (mc.currentScreen instanceof GuiMultiplayer || mc.currentScreen instanceof GuiScreenAddServer || mc.currentScreen instanceof GuiScreenServerList) {
                        presence.state = "Selecting a server.";
                    } else if (mc.currentScreen instanceof GuiScreenResourcePacks) {
                        presence.state = "Selecting a texture pack.";
                    } else if (mc.currentScreen instanceof GuiDisconnected) {
                        presence.state = "Disconnecting from a server.";
                    } else if (mc.currentScreen instanceof GuiConnecting) {
                        presence.state = "Connecting to a server.";
                    } else if (mc.currentScreen instanceof GuiCreateFlatWorld || mc.currentScreen instanceof GuiCreateWorld) {
                        presence.state = "Creating a new world.";
                    } else {
                        presence.state = "Playing the game.";
                    }
                } else {
                    if (mc.player != null) {
                        int health = Math.round(mc.player.getHealth() + mc.player.getAbsorptionAmount());
                        int armor = Math.round(mc.player.getTotalArmorValue());
                        presence.state = mc.player.getName() + " | Health " + health + " | " + "Armor " + armor;
                        if (mc.player.isDead) {
                            presence.state = mc.player.getName() + " | " + "Dead" + " | " + "Armor " + armor;
                        }
                        if (mc.isIntegratedServerRunning()) {
                            presence.details = "Playing singleplayer";
                        } else if (!mc.isIntegratedServerRunning()) {
                            if (Infinity.INSTANCE.moduleManager.getModuleByClass(RPC.class).showIP.getValue()) {
                                presence.details = "Playing on " + mc.getCurrentServerData().serverIP;
                            }
                        }
                    }
                }
                lib.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();
    }

    public static void shutdown() {
        lib.Discord_Shutdown();
    }
}
