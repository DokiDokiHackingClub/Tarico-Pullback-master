package dev.tarico.module.modules.movement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {
    private final List<Packet<?>> packetList = new ArrayList<>();
    private EntityOtherPlayerMP blinkEntity;

    public Blink() {
        super("Blink", "Anti Packet Send", ModuleType.Movement);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(TickEvent e) {
        setSuffix("Packets:" + packetList.size());
    }

    @Override
    public void enable() {
        if (mc.thePlayer == null) {
        }
/*        this.blinkEntity = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "Blink"));
        this.blinkEntity.inventory = mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
        this.blinkEntity.setCustomNameTag("[NPC] Blink");
        mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);*/
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.packetList.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    public void disable() {
        try {
            if (packetList.isEmpty())
                return;

            for (Packet<?> packet : this.packetList) {
                mc.getNetHandler().addToSendQueue(packet);
            }
            this.packetList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

