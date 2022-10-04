package dev.tarico.module.modules.render;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class Brightness extends Module {
    static float gamma = 0f;

    public Brightness() {
        super("Brightness", "Render Brightness", ModuleType.Render);
        this.setState(true);
        this.inVape = true;
        this.vapeName = "FullBright";
    }

    @Override
    public void enable() {
        gamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void disable() {
        mc.gameSettings.gammaSetting = gamma;
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onRender(EventTick e) {
        mc.gameSettings.gammaSetting = 300;
    }
}
