package dev.tarico.module.modules.hudimplement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

@Native
public class HotBar extends Module {
    public HotBar() {
        super("HotBar", "HotBar Render", ModuleType.HUD);
    }
}
