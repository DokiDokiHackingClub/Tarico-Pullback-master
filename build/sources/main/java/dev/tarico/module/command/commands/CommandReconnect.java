package dev.tarico.module.command.commands;

import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;

import java.io.IOException;

public class CommandReconnect extends Command {
    public CommandReconnect() {
        super("reconnect", new String[]{"rc"}, "Reconnect IRC Server");
    }

    @Override
    public String execute(String[] args) {
        try {
            ClientImpl.connect();
        } catch (IOException e) {
            Helper.sendMessage("Failed to connect IRC.");
        }
        return null;
    }
}
