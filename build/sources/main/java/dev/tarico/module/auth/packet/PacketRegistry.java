package dev.tarico.module.auth.packet;

import dev.tarico.module.auth.packet.client.CAuthPacket;
import dev.tarico.module.auth.packet.client.CDisconnectPacket;
import dev.tarico.module.auth.packet.client.CMessagePacket;
import dev.tarico.module.auth.packet.client.CRegisterPacket;
import dev.tarico.module.auth.packet.server.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public final class PacketRegistry {
    private static final HashMap<Integer, Class<? extends IPacket<IPacketHandler>>> PACKETS = new HashMap<>();
    private static final HashMap<Class<? extends IPacket<IPacketHandler>>, Integer> PACKET_IDS = new HashMap<>();

    private static boolean done = false;

    static {
        register();
    }

    public static void register() {
        if (done) return;

        registerPacket(CAuthPacket.class);
        registerPacket(CRegisterPacket.class);
        registerPacket(CMessagePacket.class);
        registerPacket(CDisconnectPacket.class);

        registerPacket(SAuthResultPacket.class);
        registerPacket(SRegisterResultPacket.class);
        registerPacket(SClosePacket.class);
        registerPacket(SKickPacket.class);
        registerPacket(SMessagePacket.class);

        done = true;
    }

    public static IPacket<IPacketHandler> readPacket(int id, DataInputStream buf) throws Throwable {
        final IPacket<IPacketHandler> packet = PACKETS.get(id).newInstance();

        packet.readData(buf);

        return packet;
    }

    public static void writeHead(DataOutputStream buf, Class<? extends IPacket<?>> packetClass) throws IOException {
        buf.writeInt(PACKET_IDS.get(packetClass));
    }

    @SuppressWarnings("unchecked")
    private static void registerPacket(Class<? extends IPacket<?>> packetClass) {
        final Class<? extends IPacket<IPacketHandler>> cast = (Class<? extends IPacket<IPacketHandler>>) packetClass;
        final int id = PACKETS.size();

        PACKETS.put(id, cast);
        PACKET_IDS.put(cast, id);
    }
}
