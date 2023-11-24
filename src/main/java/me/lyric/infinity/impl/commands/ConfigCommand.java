package me.lyric.infinity.impl.commands;

import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.string.StringUtils;
import me.lyric.infinity.manager.client.ConfigManager;

/**
 * @author lyric
 */

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Create and manage your configs. Do -config load <name> or -config save <name>");
    }

    @Override
    public String theCommand() {
        return "config <save/load> <name> / description>";
    }

    @Override
    public void onCommand(String[] args) {
        String name = null;
        String task = null;

        if (args.length < 2)
        {
            splash(CommandState.ERROR);
        }
        if (args.length > 2)
        {
            task = args[1];
            name = args[2];
        }
        if (args.length > 3)
        {
            splash(CommandState.ERROR);
        }
        if (StringUtils.contains(task, "save"))
        {
            ConfigManager.save(name);
            ChatUtils.sendMessage("The config file " + name + " was saved successfully.");
            splash(CommandState.PERFORMED);
        }
        if (StringUtils.contains(task, "load"))
        {
            if (ConfigManager.load(name))
            {
                ChatUtils.sendMessage("The config file " + name + " was identified and loaded.");
                splash(CommandState.PERFORMED);
            }
            else
            {
                ChatUtils.sendMessage("The config file " + name + " does not exist!");
                splash(CommandState.ERROR);
            }
        }
        if (StringUtils.contains(task, "description"))
        {
            ChatUtils.sendMessage(getDescription());
            splash(CommandState.PERFORMED);
        }
    }
}
