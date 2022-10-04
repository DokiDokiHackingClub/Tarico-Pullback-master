package dev.tarico.module.command.commands;

import dev.tarico.Client;
import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;

import java.io.IOException;

public class CommandReload extends Command {
    public CommandReload() {
        super("reload", new String[]{"rl"}, "Reload plugins");
    }

    @Override
    public String execute(String[] args) {
        Client.instance.relaodPlugin();
        return null;
    }
}
