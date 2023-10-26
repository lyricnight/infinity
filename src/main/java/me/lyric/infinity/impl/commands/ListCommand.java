package me.lyric.infinity.impl.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.client.CommandManager;

public class ListCommand extends Command {

    public ListCommand()
    {
        super("commands", "shows a list of all available commands.");
    }

    @Override
    public String theCommand()
    {
        return "commands";
    }

    @Override
    public void onCommand(String[] args)
    {
        if(args.length > 1)
        {
            this.splash(CommandState.ERROR);
            return;
        }
        ChatUtils.sendMessage(ChatFormatting.GREEN + CommandManager.getCommandsAsString().toString());
        this.splash(CommandState.PERFORMED);
    }


}
