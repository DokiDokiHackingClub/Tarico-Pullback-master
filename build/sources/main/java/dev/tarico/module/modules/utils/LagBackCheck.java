package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class LagBackCheck extends Module {

    public LagBackCheck() {
        super("LagCheck", "Check LagBack", ModuleType.Utils);
    }

    @EventTarget
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook)
            NotificationsManager.addNotification(new Notification("The server is pulling you back!", Notification.Type.Error));
    }
}

