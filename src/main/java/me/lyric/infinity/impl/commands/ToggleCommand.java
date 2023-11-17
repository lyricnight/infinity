package me.lyric.infinity.impl.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.command.Command;
import me.lyric.infinity.api.command.CommandState;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;

/**
 * @author lyric
 */

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "Enable or disable a module.");
    }

    @Override
    public String theCommand() {
        return "toggle <module>";
    }

    @Override
    public void onCommand(String[] args) {
        String module = null;

        if (args.length > 1) {
            module = args[1];
        }

        if (module == null || args.length > 2) {
            this.splash(CommandState.ERROR);
            return;
        }

        boolean isModule = false;

        for (Module modules : Infinity.INSTANCE.moduleManager.getModules()) {
            if (modules.name.equalsIgnoreCase(module)) {
                if (Infinity.INSTANCE.moduleManager.getModuleByString(module).isEnabled())
                {
                    Infinity.INSTANCE.moduleManager.getModuleByString(module).disable();
                }
                else
                {
                    Infinity.INSTANCE.moduleManager.getModuleByString(module).enable();
                }
                isModule = true;
                break;
            }
        }
        if (!isModule) {
            ChatUtils.sendMessage(ChatFormatting.RED + "Unknown module.");
        } else {
            ChatUtils.sendMessage(ChatFormatting.BOLD + module + ChatFormatting.RESET + " " + "has been toggled.");
        }

        this.splash(CommandState.PERFORMED);
    }
}