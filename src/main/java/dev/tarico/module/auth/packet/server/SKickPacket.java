package dev.tarico.module.auth.packet.server;

import dev.tarico.module.auth.packet.IPacket;
import dev.tarico.module.auth.packet.IServerPacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SKickPacket implements IPacket<IServerPacketHandler> {
    private String reason;

    public SKickPacket() {
    }

    public SKickPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream buffer) throws IOException {
        buffer.writeUTF(reason);
    }

    @Override
    public void readData(DataInputStream buffer) throws IOException {
        reason = buffer.readUTF();
    }

    @Override
    public void processPacket(IServerPacketHandler packetHandler) {
        packetHandler.processKick(this);
    }

    public String getReason() {
        return reason;
    }
}
