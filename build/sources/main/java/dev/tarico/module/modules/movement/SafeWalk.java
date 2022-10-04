package dev.tarico.module.modules.movement;


import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "Walk safety", ModuleType.Movement);
        this.inVape = true;
    }
}
