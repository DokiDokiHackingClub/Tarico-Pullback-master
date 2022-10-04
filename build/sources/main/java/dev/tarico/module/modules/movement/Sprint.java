package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Auto Sprint in Press Move", ModuleType.Movement);
        this.inVape = true;
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventTick event) {
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.thePlayer.setSprinting(!ModuleManager.instance.getModule(Scaffold.class).getState() || Scaffold.sprint.getValue());
        }
    }
}
