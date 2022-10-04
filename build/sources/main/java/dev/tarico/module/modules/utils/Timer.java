package dev.tarico.module.modules.utils;

import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;

public class Timer extends Module {
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.0, 0.5, 10.0, 0.1);

    public Timer() {
        super("Timer", "Change timer speed", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onTick(EventTick e) {
        setSuffix(speed.getValue() + "F");
        Client.getTimer().timerSpeed = speed.getValue().floatValue();
    }

    @Override
    public void disable() {
        Client.getTimer().timerSpeed = 1.0f;
    }
}
