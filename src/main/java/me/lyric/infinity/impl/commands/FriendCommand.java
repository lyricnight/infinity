package me.lyric.infinity.impl.commands;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.util.string.StringUtils;

//author lyric

public class FriendCommand extends Command {
    public FriendCommand(){
        super("friend", "adds and removes friends.");

    }
    @Override
    public String theCommand() {
        return "friend <add/del> <name>";
    }
    @Override
    public void onCommand(String[] args)
    {
        String friend = null;
        String task = null;
        if (args.length < 2)
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
            Infinity.INSTANCE.friendManager.addFriend(friend);
            this.splash(CommandState.PERFORMED);
        }
        if (StringUtils.contains(task, "del"))
        {
            Infinity.INSTANCE.friendManager.removeFriend(friend);
            this.splash(CommandState.PERFORMED);
        }
    }


}
