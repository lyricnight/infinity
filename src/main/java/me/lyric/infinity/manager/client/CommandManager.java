package me.lyric.infinity.manager.client;

import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.impl.commands.ConfigCommand;
import me.lyric.infinity.impl.commands.FriendCommand;
import me.lyric.infinity.impl.commands.PrefixCommand;
import me.lyric.infinity.impl.commands.ToggleCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lyric - forced to use clientMessage here
 */

public class CommandManager {
    private String clientMessage = "[Infinity]";

    private static final Set<Command> commands = new HashSet<>();

    private String prefix = "-";

    public static Command get(final String commandStr) {
        Command command = null;

        for (Command commands : getCommands()) {
            if (commands.getCommand().equalsIgnoreCase(commandStr)) {
                command = commands;

                break;
            }
        }

        return command;
    }

    public void init() {
        commands.add(new PrefixCommand());
        commands.add(new ConfigCommand());
        commands.add(new FriendCommand());
        commands.add(new ToggleCommand());
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static Set<Command> getCommands() {
        return commands;
    }

    public String getClientMessage() {
        return this.clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }
}
