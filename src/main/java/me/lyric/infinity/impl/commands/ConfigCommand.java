package me.lyric.infinity.impl.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.config.Config;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.string.StringUtils;
import me.lyric.infinity.api.util.time.DateTimeUtils;
import me.lyric.infinity.manager.client.ConfigManager;

/**
 * @author lyric
 */

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Create, and manage your configs.");
    }

    @Override
    public String theCommand() {
        return "config <save/load> <name> | <add/new/create/remove/rem/del/delete> <name> | <refresh/reload/list>";
    }

    @Override
    public void onCommand(String[] args) {
        String task = null;
        String name = null;

        if (args.length > 1) {
            task = args[1];
        }

        if (args.length > 2) {
            name = args[2];
        }

        if (args.length > 3 || task == null) {
            splash(CommandState.ERROR);

            return;
        }

        if (StringUtils.contains(task, "list")) {
            ConfigManager.info();

            this.splash(CommandState.PERFORMED);

            return;
        }

        if (StringUtils.contains(task, "reload", "refresh")) {
            if (name != null) {
                splash(CommandState.ERROR);

                return;
            }

            ConfigManager.refresh();

            ChatUtils.sendMessage("Reloaded configurations folder.");

            this.splash(CommandState.PERFORMED);

            return;
        }

        if (task.equalsIgnoreCase("save")) {
            if (name == null) {
                ConfigManager.reload();
                ConfigManager.process(ConfigManager.SAVE);

                ChatUtils.sendMessage("Saved current configuration: " + ConfigManager.current().getName());

                return;
            }

            final Config current = ConfigManager.current();
            final Config config = ConfigManager.get(name);

            if (config != null) {
                ConfigManager.set(config);
                ConfigManager.reload();
                ConfigManager.process(ConfigManager.SAVE);

                if (current != null) {
                    ConfigManager.set(current);
                    ConfigManager.reload();
                }

                ChatUtils.sendMessage("Saved " + config.getName() + ".");

                return;
            }

            ChatUtils.sendMessage(ChatFormatting.RED + name + " config does not exist.");

            return;
        }

        if (task.equalsIgnoreCase("load")) {
            if (name == null) {
                splash(CommandState.ERROR);

                return;
            }

            final Config config = ConfigManager.get(name);

            if (config != null) {
                ConfigManager.set(config);
                ConfigManager.reload();
                ConfigManager.process(ConfigManager.LOAD);

                ChatUtils.sendMessage(config.getName() + " was loaded.");

                return;
            }

            ChatUtils.sendMessage(ChatFormatting.RED + name + " configuration doesn't exist!");

            return;
        }

        if (StringUtils.contains(task, "add", "new", "create")) {
            if (name == null) {
                splash(CommandState.ERROR);

                return;
            }

            boolean contains = ConfigManager.contains(name);

            if (!contains) {
                final Config config = new Config(name, name.toLowerCase(), DateTimeUtils.time(DateTimeUtils.TIME_AND_DATE));

                ConfigManager.implement(config);
                ConfigManager.sync(config);

                ChatUtils.sendMessage("Created configuration: " + config.getName() + " in: " + config.getData());

                return;
            }

            ChatUtils.sendMessage("Duplicate configuration!");

            return;
        }

        if (StringUtils.contains(task, "remove", "delete", "del", "rem")) {
            if (name == null) {
                splash(CommandState.ERROR);

                return;
            }

            final Config config = ConfigManager.get(name);

            if (config != null) {
                ConfigManager.exclude(config);
                ConfigManager.reload();

                ChatUtils.sendMessage("Removed " + config.getName() + " from configuration list.");

                return;
            }

            ChatUtils.sendMessage(ChatFormatting.RED + name + " configuration doesn't exist!");

            return;
        }

        splash(CommandState.ERROR);
    }
}
