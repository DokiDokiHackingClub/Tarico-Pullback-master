package dev.tarico.module.modules.movement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.Mapping;
import dev.tarico.utils.client.MoveUtils;

public class Strafe extends Module {
    private BooleanValue<Boolean> slow = new BooleanValue<>("SlowDown", true);

    public Strafe() {
        super("Strafe", "Air Strafe", ModuleType.Movement);
        this.inVape = true;
    }

    @EventTarget
    private void onPre(EventPreUpdate event) {
        if (!mc.thePlayer.onGround) {
            if (Mapping.getPressed(mc.gameSettings.keyBindJump)) {
                if (Mapping.getPressed(mc.gameSettings.keyBindBack) || Mapping.getPressed(mc.gameSettings.keyBindRight) || Mapping.getPressed(mc.gameSettings.keyBindLeft)) {
                    MoveUtils.strafe(slow.getValue() ? MoveUtils.getSpeed() * 0.85 : MoveUtils.getSpeed());
                } else {
                    MoveUtils.strafe();
                }
            }
        }
    }
}
