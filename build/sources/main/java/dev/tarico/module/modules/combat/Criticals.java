package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.movement.Faker;
import dev.tarico.module.modules.movement.LegitSpeed;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
    public static ModeValue<Enum<floats>> floating = new ModeValue<>("Float", floats.values(), floats.New);
    public static NumberValue<Double> delay = new NumberValue<>("Delay", 500.0, 0.0, 5000.0, 100.0);

    public static TimerUtil timer = new TimerUtil();
    private int attacks = 0;

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onEvent(EventPacketSend event) {
        if (ModuleManager.instance.getModule(Faker.class).getState())
            return;

        if (!(event.getPacket() instanceof C02PacketUseEntity))
            return;

        if (isEating() || ModuleManager.instance.getModule(LegitSpeed.class).getState())
            return;

        C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

        if (timer.hasTimeElapsed(delay.getValue().intValue())) {
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK && mc.thePlayer.isCollidedVertically && this.hurtTimeCheck(packet.getEntityFromWorld(mc.theWorld))) {
                if (floating.getValue() == floats.New) {
                    attack(0.0, 0.04132332, 0.0, true);
                    attack(0.0, 0.04132332, 0.0, false);
                    attack(0.0, 0.01, 0.0, false);
                    attack(0.0, 0.0011, 0.0, false);
                } else if (floating.getValue() == floats.Noteless) {
                    attack(0.0, 0.0625, 0.0, true);
                    attack(0.0, 0.0, 0.0, false);
                    attack(0.0, 1.1E-5, 0.0, false);
                    attack(0.0, 0.0, 0.0, false);
                } else if (floating.getValue() == floats.Edit) {
                    attack(0.0, 0.05250033001304, 0.0, false);
                    attack(0.0, 0.00150020001304, 0.0, false);
                } else if (floating.getValue() == floats.AAC) {
                    attack(0.0, 0.05250000001304, 0.0, false);
                    attack(0.0, 0.00150000001304, 0.0, false);
                    attack(0.0, 0.01400000001304, 0.0, false);
                    attack(0.0, 0.00150000001304, 0.0, false);
                } else if (floating.getValue() == floats.Matrix) {
                    attacks++;
                    if (attacks > 3) {
                        attack(0.0, 0.0825080378093, 0.0, false);
                        attack(0.0, 0.023243243674, 0.0, false);
                        attack(0.0, 0.0215634532004, 0.0, false);
                        attack(0.0, 0.00150000001304, 0.0, false);
                        attacks = 0;
                    }
                }


            }
            timer.reset();
        }
    }

    public Criticals() {
        super("Criticals", "Critical Attack", ModuleType.Combat);
        this.inVape = true;
    }

    public static boolean isEating() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getItemInUse().getItem()
                .getItemUseAction(mc.thePlayer.getItemInUse()) == EnumAction.EAT;
    }

    @Native
    public void attack(Double xOffset, Double yOffset, Double zOffset, boolean ground) {
        double x = mc.thePlayer.posX + xOffset;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + zOffset;
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
    }

    enum floats {
        New,
        Noteless,
        Edit,
        AAC,
        Matrix
        // 河南支持二次元
    }


    private boolean hurtTimeCheck(Entity entity) {
        return entity != null && entity.hurtResistantTime <= 15;
    }
}

