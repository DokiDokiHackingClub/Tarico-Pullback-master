package dev.tarico.management;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventBus;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.misc.EventChat;
import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.auth.packet.client.CMessagePacket;
import dev.tarico.module.command.Command;
import dev.tarico.module.command.commands.*;
import dev.tarico.utils.client.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class CommandManager {
    private List<Command> commands = new ArrayList<>();
    public ArrayList<Command> pluginCommand = new ArrayList<>();

    @Native
    public void init() {
        commands.add(new CommandToggle());
        commands.add(new CommandBind());
        commands.add(new CommandFriend());
        commands.add(new CommandHelp());
        commands.add(new ComamndQQ());
        commands.add(new CommandScoreboard());
        commands.add(new CommandReconnect());
        commands.add(new CommandPickupFilter());
        commands.add(new CommandChat());
        commands.add(new CommandWatermark());
        commands.add(new CommandFastban());
        commands.add(new CommandConfig());
        //commands.add(new CommandReload());
        EventBus.getInstance().register(this);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public Optional<Command> getCommandByName(String name) {
        return this.commands.stream().filter(c2 -> {
            boolean isAlias = false;
            String[] arrstring = c2.getAlias();
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String str = arrstring[n2];
                if (str.equalsIgnoreCase(name)) {
                    isAlias = true;
                    break;
                }
                ++n2;
            }
            return c2.getName().equalsIgnoreCase(name) || isAlias;
        }).findFirst();
    }

    public void add(Command command) {
        this.commands.add(command);
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onChat(EventChat e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith(".")) {
            e.setCancelled(true);
            String[] args = e.getMessage().trim().substring(1).split(" ");
            Optional<Command> possibleCmd = this.getCommandByName(args[0]);
            if (possibleCmd.isPresent()) {
                String result = possibleCmd.get().execute(Arrays.copyOfRange(args, 1, args.length));
                if (result != null && !result.isEmpty()) {
                    Helper.sendMessage(result);
                }
            } else {
                Helper.sendMessage(String.format("Command not found Try '%shelp'", "."));
            }
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onIRCChat(EventChat e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith("#")) {
            e.setCancelled(true);
            ClientImpl.sendPacket(new CMessagePacket(e.getMessage().replace("#", "")));
        }
    }


}

