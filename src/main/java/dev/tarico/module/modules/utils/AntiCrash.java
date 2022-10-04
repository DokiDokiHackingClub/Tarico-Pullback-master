package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class AntiCrash extends Module {
    public AntiCrash() {
        super("AntiCrash", "Anti server <-> client invalid packet.", ModuleType.Utils);
    }

    @EventTarget
    public void onPacket(EventPacketReceive e) {
    }
}
