package dev.tarico.module.modules.fun;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;


public class AutoL extends Module {
    public static BooleanValue<Boolean> shout = new BooleanValue<>("Shout", false);

    public AutoL() {
        super("AutoL", "Print L when you kill", ModuleType.Fun);
    }
}
