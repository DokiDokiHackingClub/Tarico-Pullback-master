package dev.tarico.module.modules.utils;


import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.Helper;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

public class LightningCheck extends Module {
    public LightningCheck() {
        super("LightningCheck", "Check Lightning for UHC and sw", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
            S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity) e.getPacket();
            if (packetIn.func_149053_g() == 1) {
                int x = (int) ((double) packetIn.func_149051_d() / 32.0D);
                int y = (int) ((double) packetIn.func_149050_e() / 32.0D);
                int z = (int) ((double) packetIn.func_149049_f() / 32.0D);
                Helper.sendMessage("Lightning checked on X:" + x + " Y:" + y + " Z:" + z);
            }
        }
    }
}
