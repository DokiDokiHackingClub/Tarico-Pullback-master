package dev.tarico.module.modules.combat;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;
import dev.tarico.utils.client.PacketUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class WTap extends Module {

    private final ModeValue<Enum<WTapMode>> mode = new ModeValue<>("Mode", WTapMode.values(), WTapMode.Legit);
    public static int ticks;

    public enum WTapMode {
        Legit,
        Packet
    }

    public WTap() {
        super("WTap", "Makes people take more knockback.", ModuleType.Combat);
        this.inVape = true;
    }

    @EventTarget
    public void onTick(EventTick e) {
        setSuffix(mode.getValue());
    }

    public void onAttack(final EventPreUpdate event) {
        ticks = 0;
    }

    @EventTarget
    public void onMotion(final EventPreUpdate e) {
        ++ticks;

        if (mc.thePlayer.isSprinting()) {
            if (mode.getValue() == WTapMode.Legit) {
                if (ticks == 2) mc.thePlayer.setSprinting(false);
                if (ticks == 3) mc.thePlayer.setSprinting(true);
            } else {
                if (ticks < 10) {
                    PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                }
            }
        }
    }
}
