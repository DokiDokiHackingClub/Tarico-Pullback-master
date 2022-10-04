package dev.tarico.module.auth.packet;

import dev.tarico.module.auth.packet.server.*;

import java.io.IOException;

public interface IServerPacketHandler extends IPacketHandler {
    void processClose(SClosePacket packet);

    void processKick(SKickPacket packet);

    void processMessage(SMessagePacket packet);

    void processAuthResult(SAuthResultPacket packet) throws IOException;

    void processRegisterResult(SRegisterResultPacket packet);
}
