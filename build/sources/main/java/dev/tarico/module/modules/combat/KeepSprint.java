package dev.tarico.module.modules.combat;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KeepSprint extends Module {
    public KeepSprint() {
        super("KeepSprint", "Force sprint any time", ModuleType.Combat);
        this.inVape = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.gameSettings.keyBindForward.isKeyDown())
            if (!mc.thePlayer.isSprinting()) mc.thePlayer.setSprinting(true);
    }
}