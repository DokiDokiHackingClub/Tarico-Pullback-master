package dev.tarico.module.command.commands;

import dev.tarico.management.FileManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.command.Command;
import dev.tarico.module.config.ConfigLoader;
import dev.tarico.module.config.ConfigManager;
import dev.tarico.module.modules.Module;
import dev.tarico.utils.client.Helper;

import java.awt.*;
import java.io.IOException;

public class CommandConfig extends Command {

    public CommandConfig() {
        super("Config", new String[]{"cfg", "profile"}, "Manage configs");
    }


    @Override
    public String execute(String[] args) {
        ConfigManager.reload();
        if (args.length < 1) {
            Helper.sendMessage(".config load <Config name> - Load a config without binds");
            Helper.sendMessage(".config loadbind <Config name> - Load a config with binds");
            Helper.sendMessage(".config save <Config name> - Save as a new Config");
            Helper.sendMessage(".config delete <Config name> - delete a config");
            Helper.sendMessage(".config list - print config list");
            Helper.sendMessage(".config opendir - open configs dir");
            return null;
        }
        if (args[0].equals("list")) {
            Helper.sendMessage("Configs:");
            for (String s : ConfigManager.configs)
                Helper.sendMessage("- " + s);
            return null;
        }
        if (args[0].equals("opendir")) {
            try {
                Desktop.getDesktop().open(FileManager.dir);
            } catch (IOException e) {
                Helper.sendMessage("Failed to open dir");
            }
            return null;
        }
        if (args.length >= 2) {
            if (args[0].equals("load")) {
                for (Module m : ModuleManager.instance.getModules()) {
                    if (m.getState())
                        m.setState(false);
                }
                ConfigLoader cl = new ConfigLoader(args[1]);
                if (cl.isExist()) {
                    cl.readConfigNoBind();
                    Helper.sendMessage(cl.getName() + " loaded.");
                } else {
                    Helper.sendMessage("Config not exist.");
                }
            }
            if (args[0].equals("loadbind")) {
                for (Module m : ModuleManager.instance.getModules()) {
                    if (m.getState())
                        m.setState(false);
                }
                ConfigLoader cl = new ConfigLoader(args[1]);
                if (cl.isExist()) {
                    cl.readConfig();
                    Helper.sendMessage(cl.getName() + " loaded.");
                } else {
                    Helper.sendMessage("Config not exist.");
                }
            }
            if (args[0].equals("save")) {
                ConfigLoader cl = new ConfigLoader(args[1]);
                cl.saveSetting();
                Helper.sendMessage(cl.getName() + " saved.");
            }
            if (args[0].equals("delete")) {
                ConfigLoader cl = new ConfigLoader(args[1]);
                cl.delete();
                Helper.sendMessage(cl.getName() + " deleted.");
            }
        } else {
            Helper.sendMessage(".config load <Config name> - Load a config");
            Helper.sendMessage(".config save <Config name> - Save as a new Config");
            Helper.sendMessage(".config delete <Config name> - delete a config");
            Helper.sendMessage(".config list - print config list");
            Helper.sendMessage(".config opendir - open configs dir");
        }
        return null;
    }
}
