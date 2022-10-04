package dev.tarico.module.command.commands;

import dev.tarico.Client;
import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;

public class CommandHelp
        extends Command {
    public CommandHelp() {
        super("help", new String[]{"list"}, "Get Command List");
    }

    @Override
    public String execute(String[] args) {
        Helper.sendMessage(Client.instance.name + " " + Client.instance.version);
        for (Command c : Client.instance.commandManager.getCommands()) {
            Helper.sendMessage(c.getName() + " - " + c.getHelp());
        }
        return null;
    }
}

