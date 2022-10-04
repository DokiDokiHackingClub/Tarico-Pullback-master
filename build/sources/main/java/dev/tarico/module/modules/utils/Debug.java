package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.Helper;

public class Debug extends Module {
    public Debug() {
        super("Debug", "debug tool", ModuleType.Utils);
    }

    @EventTarget
    public void onPacket(EventPacketSend e) {
        Helper.sendMessage(e.getPacket().getClass().getName());
    }

}
