package dev.tarico.module.modules.movement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.client.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {
    public static BooleanValue<Boolean> fakeUpdate = new BooleanValue<>("FakeUpdate", false);

    public NoRotate() {
        super("NoRotate", "Anti Server Rotate", ModuleType.Movement);
    }


    @EventTarget
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            e.setCancelled(true);
            if (fakeUpdate.getValue()) {
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        packet.getYaw(),
                        packet.getPitch(),
                        mc.thePlayer.onGround));
            }
        }
    }
}
