package dev.tarico.module.modules.utils;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class Panic extends Module {
    public Panic() {
        super("Panic", "Disable Modules", ModuleType.Utils);
    }

    @Override
    public void enable() {
        for (Module m : ModuleManager.instance.getModules()) {
            if (m.getState())
                m.setState(false);
        }
    }
}
