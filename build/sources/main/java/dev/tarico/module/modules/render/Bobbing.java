package dev.tarico.module.modules.render;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;

public class Bobbing extends Module {
    private final NumberValue<Double> boob = new NumberValue<>("Amount", 1.0, 0.1, 5.0, 0.5);

    public Bobbing() {
        super("Bobbing", "Render Bobbing Effect", ModuleType.Render);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventPreUpdate event) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.cameraYaw = (float) (0.09090908616781235 * this.boob.getValue());
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void ontick(EventTick e) {
        setSuffix(boob.getValue());
    }
}
