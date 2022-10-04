package dev.tarico.module.command.commands;

import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.Mapper;

import java.util.ArrayList;

public class CommandScoreboard
        extends Command {
    public static ArrayList<Mapper> mappers = new ArrayList<>();

    public CommandScoreboard() {
        super("scoreboard", new String[]{"sb"}, "Replace scoreboard text");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 2) {
            mappers.add(new Mapper(args[0], args[1]));
            Helper.sendMessage("Added!");
        } else {
            return "use: .sb <old> <new>";
        }
        return null;
    }
}

