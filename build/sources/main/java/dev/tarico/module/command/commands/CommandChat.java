package dev.tarico.module.command.commands;

import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.auth.packet.client.CMessagePacket;
import dev.tarico.module.command.Command;

public class CommandChat extends Command {
    public CommandChat() {
        super("chat", new String[]{"i", "irc"}, "Chat to irc");
    }

    @Override
    public String execute(String[] args) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                builder.append(args[i]);
            } else {
                builder.append(' ').append(args[i]);
            }
        }

        ClientImpl.sendPacket(new CMessagePacket(builder.toString()));

        return null;
    }
}
