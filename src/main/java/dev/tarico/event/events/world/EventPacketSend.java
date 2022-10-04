package dev.tarico.event.events.world;

import dev.tarico.event.Event;
import net.minecraft.network.Packet;

public class EventPacketSend extends Event {
    public final Packet<?> packet;

    public EventPacketSend(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

}

