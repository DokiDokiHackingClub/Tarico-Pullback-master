package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.Mapping;
import dev.tarico.utils.client.MoveUtils;
import net.minecraft.client.settings.KeyBinding;

public class LegitSpeed extends Module {
    private final NumberValue<Double> speed = new NumberValue<>("Timer", 1.1, 0.5, 10.0, 0.1);
    private final BooleanValue<Boolean> airStrafe = new BooleanValue<>("Air Strafe", false);
    private BooleanValue<Boolean> slow = new BooleanValue<>("Slow in Strafe", true);

    public LegitSpeed() {
        super("LegitSpeed", "Make you move faster", ModuleType.Movement);
        this.inVape = true;
        this.vapeName = "Speed";
        this.setVSuffix("AntiCheat A");
    }

    @EventTarget
    private void onStrafe(EventPreUpdate event) {
        if (!mc.thePlayer.onGround) {
            if (!airStrafe.getValue())
                return;
            if (Mapping.getPressed(mc.gameSettings.keyBindJump)) {
                if (Mapping.getPressed(mc.gameSettings.keyBindBack) || Mapping.getPressed(mc.gameSettings.keyBindRight) || Mapping.getPressed(mc.gameSettings.keyBindLeft)) {
                    MoveUtils.strafe(slow.getValue() ? MoveUtils.getSpeed() * 0.85 : MoveUtils.getSpeed());
                } else {
                    MoveUtils.strafe();
                }
            }
        }
    }

    @Native
    @EventTarget
    public void onTick(EventPostUpdate e) {
        Client.getTimer().timerSpeed = speed.getValue().floatValue();
        if (!mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0) {
            mc.thePlayer.setSprinting(true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        }
    }

    @Override
    public void disable() {
        Client.getTimer().timerSpeed = 1.0f;
        mc.thePlayer.setSprinting(false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }
}
