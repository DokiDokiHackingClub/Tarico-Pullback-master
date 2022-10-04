package dev.tarico.module.auth.packet.server;

import dev.tarico.module.auth.packet.IPacket;
import dev.tarico.module.auth.packet.IServerPacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SClosePacket implements IPacket<IServerPacketHandler> {
    @Override
    public void writeData(DataOutputStream buffer) {

    }

    @Override
    public void readData(DataInputStream buffer) {

    }

    @Override
    public void processPacket(IServerPacketHandler packetHandler) {
        packetHandler.processClose(this);
    }
}
