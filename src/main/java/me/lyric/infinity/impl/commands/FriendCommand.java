package me.lyric.infinity.impl.commands;

import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.string.StringUtils;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.manager.client.FriendManager;

import java.util.ArrayList;

/**
 * @author lyric :((
 */

public class FriendCommand extends Command {

    //TODO: Why the fuck don't clear and list commands work

    public FriendCommand(){
        super("friend", "adds and removes friends.");

    }
    @Override
    public String theCommand() {
        return "friend <add/del> <name> / list";
    }
    @Override
    public void onCommand(String[] args)
    {
        String friend = null;
        String task = null;
        if (args.length < 1)
        {
            splash(CommandState.ERROR);
        }
        if (args.length > 2)
        {
            task = args[1];
            friend = args[2];
        }
        if (args.length > 3)
        {
            splash(CommandState.ERROR);
        }
        if (StringUtils.contains(task, "add"))
        {
            Managers.FRIENDS.addFriend(friend);
            this.splash(CommandState.PERFORMED);
        }
        if (StringUtils.contains(task, "del"))
        {
            Managers.FRIENDS.removeFriend(friend);
            this.splash(CommandState.PERFORMED);
        }
        if (StringUtils.contains(task, "list"))
        {
            ChatUtils.sendMessage("All your friends :");
            for (int i = 0; i <= Managers.FRIENDS.friendList.size(); i++)
            {
                ChatUtils.sendMessage(Managers.FRIENDS.friendList.get(i).getName());
            }
            splash(CommandState.PERFORMED);
        }
        if (StringUtils.contains(task, "description"))
        {
            ChatUtils.sendMessage(getDescription());
            splash(CommandState.PERFORMED);
        }
    }


}
