package dev.tarico.module.command.commands;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.command.Command;
import dev.tarico.module.modules.Module;
import dev.tarico.utils.client.Helper;
import net.minecraft.util.EnumChatFormatting;

public class CommandToggle
        extends Command {
    public CommandToggle() {
        super("t", new String[]{"toggle", "togl", "turnon", "enable"}, "Toggles a specified Module");
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            Helper.sendMessageWithoutPrefix(EnumChatFormatting.DARK_GRAY + " Correct usage:\u00a77 .t <module>");
        }
        boolean found = false;
        Module m = ModuleManager.instance.getModule(args[0]);
        if (m != null) {
            m.toggle();
            found = true;
            if (m.getState()) {
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " " + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.GREEN + " Enabled");
            } else {
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " " + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.RED + " Disabled");
            }
        }
        if (!found) {
            Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " Module name " + EnumChatFormatting.RED + args[0] + EnumChatFormatting.GRAY + " is invalid");
        }
        return null;
    }
}

