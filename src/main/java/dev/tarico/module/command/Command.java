package dev.tarico.module.command;

import net.minecraft.client.Minecraft;

public abstract class Command {
    public static Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final String[] alias;
    private final String help;

    public Command(String name, String[] alias, String help) {
        this.name = name.toLowerCase();
        this.help = help;
        this.alias = alias;
    }

    public abstract String execute(String[] var1);

    public String getName() {
        return this.name;
    }

    public String[] getAlias() {
        return this.alias;
    }

    public String getHelp() {
        return this.help;
    }
}

