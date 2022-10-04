package dev.tarico.module.modules.combat;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AntiFireball extends Module {
    public static NumberValue<Double> delay = new NumberValue<>("Delay", 1000D, 0D, 2000D, 100D);
    TimerUtil time = new TimerUtil();

    public AntiFireball() {
        super("AntiFireball", "Auto attack fireballs", ModuleType.Combat);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventPreUpdate event) {
        for (Entity entity : getEntityList()) {
            if (entity instanceof EntityFireball && mc.thePlayer.getDistanceToEntity(entity) < 4.5) {
                if (time.hasTimeElapsed(delay.getValue().intValue())) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.swingItem();
                }
            }
        }
    }

    public Entity[] getEntityList() {
        return mc.theWorld != null ? mc.theWorld.getLoadedEntityList().toArray(new Entity[0]) : null;
    }
}
