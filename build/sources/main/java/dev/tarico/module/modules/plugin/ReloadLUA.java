package dev.tarico.module.modules.plugin;

import dev.tarico.lua.api.ScriptLoader;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

public class ReloadLUA extends Module {
    public ReloadLUA() {
        super("Reload lua","Reload all lua scripts",ModuleType.Plugin);
        noToggle = true;
    }

    @Override
    public void enable() {
        ScriptLoader.init();
        setState(false);
    }
}
