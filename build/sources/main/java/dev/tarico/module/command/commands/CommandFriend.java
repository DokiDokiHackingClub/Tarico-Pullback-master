package dev.tarico.module.command.commands;

import dev.tarico.management.FileManager;
import dev.tarico.management.FriendManager;
import dev.tarico.module.command.Command;
import dev.tarico.utils.client.Helper;
import net.minecraft.util.EnumChatFormatting;

import java.util.Iterator;

public class CommandFriend extends Command {

    public CommandFriend() {
        super("friend", new String[]{"f"}, "Manager Friends");
    }

    public String execute(String[] args) {
        String friends;
        String fr;
        Iterator<String> var4;
        int var5;
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                friends = "";
                friends = friends + String.format("%s:%s%s", args[1], args[2], System.lineSeparator());
                FriendManager.maps().put(args[1], args[2]);
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format(" %s has been added as %s", args[1], args[2]));
                FileManager.save("Friends.txt", friends, true);
            } else if (args[0].equalsIgnoreCase("del")) {
                FriendManager.maps().remove(args[1]);
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format(" %s has been removed from your friends list", args[1]));
            } else if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.maps().size() > 0) {
                    var5 = 1;

                    for (var4 = FriendManager.maps().values().iterator(); var4.hasNext(); ++var5) {
                        fr = var4.next();
                        Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format("%s. %s", var5, fr));
                    }
                } else {
                    Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " get some friends fag lmao");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                friends = "";
                friends = friends + String.format("%s%s", args[1], System.lineSeparator());
                FriendManager.maps().put(args[1], args[1]);
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format(" %s has been added as %s", args[1], args[1]));
                FileManager.save("Friends.txt", friends, true);
            } else if (args[0].equalsIgnoreCase("del")) {
                FriendManager.maps().remove(args[1]);
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format(" %s has been removed from your friends list", args[1]));
            } else if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.maps().size() > 0) {
                    var5 = 1;

                    for (var4 = FriendManager.maps().values().iterator(); var4.hasNext(); ++var5) {
                        fr = var4.next();
                        Helper.sendMessage(EnumChatFormatting.DARK_GRAY + String.format("%s. %s", var5, fr));
                    }
                } else {
                    Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " you dont have any you lonely fuck");
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.maps().size() > 0) {
                    var5 = 1;

                    for (var4 = FriendManager.maps().values().iterator(); var4.hasNext(); ++var5) {
                        fr = var4.next();
                        Helper.sendMessage(String.format("%s. %s", var5, fr));
                    }
                } else {
                    Helper.sendMessage("you dont have any you lonely fuck");
                }
            } else if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("del")) {
                Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>");
            } else {
                Helper.sendMessage(EnumChatFormatting.GRAY + " Please enter a players name");
            }
        } else {
            Helper.sendMessage(EnumChatFormatting.DARK_GRAY + " Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>");
        }

        return null;
    }
}