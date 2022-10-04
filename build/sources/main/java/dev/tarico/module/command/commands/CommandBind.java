package dev.tarico.module.command.commands;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.command.Command;
import dev.tarico.module.modules.Module;
import dev.tarico.utils.client.Helper;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class CommandBind extends Command {
    public CommandBind() {
        super("Bind", new String[]{"b"}, "Set Key Bind of Module");
    }

    @Override
    public String execute(String[] args) {
        if (args.length >= 2) {
            Module m = ModuleManager.instance.getModule(args[0]);
            if (m != null) {
                int k = Keyboard.getKeyIndex(args[1].toUpperCase());
                m.setKey(k);
                Object[] arrobject = new Object[2];
                arrobject[0] = m.getName();
                arrobject[1] = k == 0 ? "none" : args[1].toUpperCase();
                Helper.sendMessage(String.format(EnumChatFormatting.DARK_GRAY + " Bound %s to %s", arrobject));
            } else {
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " Invalid module name, double check spelling.");
            }
        } else {
            Helper.sendMessageWithoutPrefix(EnumChatFormatting.DARK_GRAY + " Correct usage:\u00a77 .bind <module> <key>");
        }
        return null;
    }
}

