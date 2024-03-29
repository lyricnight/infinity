package me.lyric.infinity.api.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.Managers;

/**
 * @author lyric -_-
 */

public class Command {

    private final String command;
    private final String description;

    public Command(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String theCommand() {
        return "";
    }

    public void splash(CommandState state) {
        switch (state) {
            case ERROR: {
                ChatUtils.sendMessage(ChatFormatting.RED + "Invalid syntax. Try: '" + Managers.COMMANDS.getPrefix() + theCommand() + "'");

                break;
            }

            case PERFORMED: {
                break;
            }
        }
    }

    /**
     * override
     * @param args the arguments in chat
     */

    public void onCommand(String[] args) {
    }
}
