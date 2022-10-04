package dev.tarico.module.modules.hudimplement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.render.CompassUtil;
import net.minecraft.client.gui.ScaledResolution;

public class Compass extends Module {
    public Compass() {
        super("Compass","Just a simple compass",ModuleType.HUD);
    }

    @EventTarget
    public void onR2d(EventRender2D e){
        CompassUtil cpass = new CompassUtil(325, 325, 1, 2, true);
        ScaledResolution sc = new ScaledResolution(mc);
        cpass.draw(sc);
    }
}
