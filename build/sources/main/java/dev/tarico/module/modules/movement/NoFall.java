package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.client.ReflectionUtil;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Anti Falling Damages", ModuleType.Movement);
        this.inVape = true;
        this.setVSuffix("Normal");
    }

    @Native
    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (ModuleManager.instance.getModule(Faker.class).getState())
            return;
        if (mc.thePlayer.posY > 0 && mc.thePlayer.fallDistance >= 2 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0) {
            if (!MoveUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MoveUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                return;
            }

            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    event.setCancelled(true);
                }
            }

            if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                if (packet.isMoving() && (boolean) ReflectionUtil.getFieldValue(packet, "rotating", "field_149481_i")) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), packet.isOnGround()));
                    event.setCancelled(true);
                }
            }
        }
    }

    @Native
    @EventTarget
    public void onMotion(EventPreUpdate event) {
        if (ModuleManager.instance.getModule(Faker.class).getState())
            return;
        if (mc.thePlayer.posY > 0 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.fallDistance >= 2.5) {
            if (!MoveUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MoveUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                return;
            }
            if (mc.thePlayer.fallDistance > 10 || mc.thePlayer.ticksExisted % 2 == 0) {
                PacketUtils.sendPacketNoEvent(new C00PacketKeepAlive()); // 发个C00降低NoFall检查
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer(true));
                Client.getTimer().timerSpeed = 1.0F;
            }
        }
    }
}
