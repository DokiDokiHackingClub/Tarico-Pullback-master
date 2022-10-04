package dev.tarico.module.auth.packet.server;

import dev.tarico.module.auth.packet.IPacket;
import dev.tarico.module.auth.packet.IServerPacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SMessagePacket implements IPacket<IServerPacketHandler> {
    private String message;

    public SMessagePacket() {
    }

    public SMessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void writeData(DataOutputStream buffer) throws IOException {
        buffer.writeUTF(message);
    }

    @Override
    public void readData(DataInputStream buffer) throws IOException {
        message = buffer.readUTF();
    }

    @Override
    public void processPacket(IServerPacketHandler packetHandler) {
        packetHandler.processMessage(this);
    }

    public String getMessage() {
        return message;
    }
}
