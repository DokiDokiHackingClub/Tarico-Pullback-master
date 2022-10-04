package dev.tarico.module.modules.combat;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {
    public Regen() {
        super("Regen", "Fast Health Regen", ModuleType.Combat);
    }

    boolean sneak = false;
    boolean inAir = false;
    int packets = 120;

    @EventTarget
    @SuppressWarnings("unused")
    private void onUpdate(EventPreUpdate event) {
        if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() && !mc.thePlayer.isUsingItem() && (mc.thePlayer.isSneaking() || !sneak) && (mc.thePlayer.onGround || inAir) && !mc.thePlayer.getFoodStats().needFood()) {
            for (int i = 0; i < packets; i++)
                PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + 1E-9, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
    }
}

