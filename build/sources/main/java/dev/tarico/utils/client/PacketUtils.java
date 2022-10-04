package dev.tarico.utils.client;

import net.minecraft.network.Packet;

import java.util.ArrayList;

public class PacketUtils extends Helper {
    public static ArrayList<Packet<?>> packets = new ArrayList<>();
    public static void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet);
        }
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        packets.add(packet);
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
