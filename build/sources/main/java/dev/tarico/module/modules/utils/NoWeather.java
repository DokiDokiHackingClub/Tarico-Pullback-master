package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public final class NoWeather extends Module {

    public NoWeather() {
        super("NoWeather", "Sunny Day", ModuleType.Utils);
    }

    @EventTarget
    public void onUpdate(EventPreUpdate event) {
        mc.theWorld.setThunderStrength(0);
        mc.theWorld.setRainStrength(0);
    }
}
