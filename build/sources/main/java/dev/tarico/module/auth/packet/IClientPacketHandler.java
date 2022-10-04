package dev.tarico.module.auth.packet;

import dev.tarico.module.auth.packet.client.CAuthPacket;
import dev.tarico.module.auth.packet.client.CDisconnectPacket;
import dev.tarico.module.auth.packet.client.CMessagePacket;
import dev.tarico.module.auth.packet.client.CRegisterPacket;

public interface IClientPacketHandler extends IPacketHandler {
    void processAuth(CAuthPacket packet);

    void processRegister(CRegisterPacket packet);

    void processMessage(CMessagePacket packet);

    void processDisconnect(CDisconnectPacket packet);
}
