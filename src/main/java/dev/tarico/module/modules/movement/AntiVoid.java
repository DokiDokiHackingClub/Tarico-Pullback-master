package dev.tarico.module.modules.movement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.timer.TimeHelper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;

public class AntiVoid extends Module {
    public static TimeHelper timer = new TimeHelper();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    private final NumberValue<Double> pullbackTime = new NumberValue<>("Pull Back Time", 700.0, 500.0, 1500.0, 100.0);
    public double[] lastGroundPos = new double[3];

    public AntiVoid() {
        super("AntiVoid", "Anti you fall into void", ModuleType.Movement);
        this.inVape = true;
        this.vapeName = "AntiFall";
    }

    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MoveUtils.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }

    @EventTarget
    public void onPacket(EventPacketSend e) {
        if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100)
            packets.clear();

        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());
            if (isInVoid()) {
                e.setCancelled(true);
                packets.add(packet);

                if (timer.isDelayComplete(pullbackTime.getValue())) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                }
            } else {
                lastGroundPos[0] = mc.thePlayer.posX;
                lastGroundPos[1] = mc.thePlayer.posY;
                lastGroundPos[2] = mc.thePlayer.posZ;

                if (!packets.isEmpty()) {
                    for (C03PacketPlayer p : packets)
                        PacketUtils.sendPacketNoEvent(p);
                    packets.clear();
                }
                timer.reset();
            }
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onRevPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook && packets.size() > 1) {
            packets.clear();
        }
    }
}


