package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.utils.client.RotationUtil;

public class AntiAim extends Module {

    public static ModeValue<Enum<YawMode>> Yaw = new ModeValue<>("YawMode", YawMode.values(), YawMode.Spin);
    public static ModeValue<Enum<PitchMode>> Pitch = new ModeValue<>("PitchMode", PitchMode.values(), PitchMode.Down);
    public BooleanValue<Boolean> Silent = new BooleanValue<>("SilentRotate", true);

    private float yaw = 0f;
    private float pitch = 0f;

    public AntiAim() {
        super("AntiAim", "like CS:GO cheat", ModuleType.Utils);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (Yaw.getValue() == YawMode.Spin) {
            yaw += 20F;
            if (yaw > 180.0f) {
                yaw = -180.0f;
            } else if (yaw < -180.0f) {
                yaw = 180.0f;
            }
        }
        if (Pitch.getValue() == PitchMode.Down) {
            pitch = 90.0F;
        }
        if (Silent.getValue()) {
            RotationUtil.setTargetRotation(new RotationUtil.Rotation(yaw, pitch));
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    public enum YawMode {
        Spin
    }

    public enum PitchMode {
        Down
    }
}
