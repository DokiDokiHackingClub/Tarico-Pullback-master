package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.timer.TimerUtils;

public final class FastDrop extends Module {
    private final ModeValue<Enum<FastDropMode>> mode = new ModeValue<>("Mode", FastDropMode.values(), FastDropMode.Delay);
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 1.0, 0.0, 1000.0, 1.0);
    private final NumberValue<Double> clicks = new NumberValue<>("FastClicks", 64.0, 1.0, 64.0, 1.0);

    private final TimerUtils timerUtils = new TimerUtils(true);

    public FastDrop() {
        super("FastDrop", "Drop Items Faster for lag server or other client", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        if (mc.gameSettings.keyBindDrop.isKeyDown()) {
            if (mc.thePlayer.getHeldItem() != null) {
                if (mode.getValue() == FastDropMode.Fast) {
                    for (int i = 0; i < clicks.getValue(); i++) {
                        mc.thePlayer.dropOneItem(false);
                    }
                } else if (mode.getValue() == FastDropMode.Delay) {
                    if (timerUtils.hasReached(delay.getValue())) {
                        mc.thePlayer.dropOneItem(false);
                    }
                }
            }
        }
    }

    enum FastDropMode {
        Fast,
        Delay
    }
}
