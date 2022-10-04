package dev.tarico.module.command.commands;

import me.cubk.plugin.PickupFilterManager;
import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;

public class CommandPickupFilter extends Command {
    public CommandPickupFilter() {
        super("pf", new String[]{"pickupfilter", "filter"}, "Set Pickup Filter");
    }

    @Override
    public String execute(String[] args) {
        try {
            if (args[0].equalsIgnoreCase("add")) {
                PickupFilterManager.addItem(Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("remove")) {
                PickupFilterManager.removeItem(Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("clear")) {
                PickupFilterManager.clear();
            }
        } catch (Exception e) {
            Helper.sendMessage("Usage: .pf add <id> | remove <id> | clear");
        }
        return null;
    }
}
