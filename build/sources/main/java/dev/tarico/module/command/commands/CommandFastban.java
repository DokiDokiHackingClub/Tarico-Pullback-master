package dev.tarico.module.command.commands;

import dev.tarico.module.command.Command;
import dev.tarico.utils.client.PacketUtils;
import net.minecraft.network.play.client.C18PacketSpectate;

public class CommandFastban extends Command {
    public CommandFastban() {
        super("fastban", new String[]{"etb"}, "Ban your account in hypixel");
    }

    @Override
    public String execute(String[] args) {
        PacketUtils.sendPacketNoEvent(new C18PacketSpectate(mc.thePlayer.getUniqueID()));
        return null;
    }
}
