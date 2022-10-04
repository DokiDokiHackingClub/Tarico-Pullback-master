package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;

public class ClientSpoof extends Module {

    public static ModeValue<Enum<MODE>> modeValue = new ModeValue<>("Mode", MODE.values(), MODE.LUNAR);

    public ClientSpoof() {
        super("ClientSpoof", "Spoof server think your are using other client", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        setSuffix(modeValue.getValue());
    }

    private enum MODE {
        NONE("Forge"), LUNAR("Lunar"), LM("LabyMod"), PVPL("PvP-L"), CHEATBREAKER("C-B"), GEYSER("Geyser");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
