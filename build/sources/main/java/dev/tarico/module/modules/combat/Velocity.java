package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.ReflectionUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {
    private final NumberValue<Double> horizontal = new NumberValue<>("Horizontal", 0.0, 0.0, 100.0, 1.0);
    private final NumberValue<Double> vertical = new NumberValue<>("Vertical", 0.0, 0.0, 100.0, 1.0);
    private final NumberValue<Double> chance = new NumberValue<>("Chance", 100.0, 0.0, 100.0, 1.0);
    private final BooleanValue<Boolean> Targeting = new BooleanValue<>("On Targeting", false);
    private final BooleanValue<Boolean> NoSword = new BooleanValue<>("No Sword", false);

    public Velocity() {
        super("Velocity", "Reduces your knockback", ModuleType.Combat);
        this.inVape = true;
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (horizontal.getValue() == 0.0 && vertical.getValue() == 0.0 && chance.getValue() == 100.0)
                e.setCancelled(true);


            if (this.Targeting.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }

            if (this.NoSword.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }

            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();

            if (packet.getEntityID() != mc.thePlayer.getEntityId())
                return;

            double motionX = packet.getMotionX(),
                    motionY = packet.getMotionY(),
                    motionZ = packet.getMotionZ();

            if (this.chance.getValue() != 100.0D) {
                double ch = Math.random();
                if (ch >= this.chance.getValue() / 100.0D) {
                    return;
                }
            }


            if (this.horizontal.getValue() != 100.0D) {
                motionX *= this.horizontal.getValue() / 100.0D;
                motionZ *= this.horizontal.getValue() / 100.0D;
            }

            if (this.vertical.getValue() != 100.0D) {
                motionY *= this.vertical.getValue() / 100.0D;
            }
            ReflectionUtil.setFieldValue(packet, (int) motionX, "motionX", "field_149415_b");
            ReflectionUtil.setFieldValue(packet, (int) motionY, "motionY", "field_149416_c");
            ReflectionUtil.setFieldValue(packet, (int) motionZ, "motionZ", "field_149414_d");
        }

        if (e.getPacket() instanceof S27PacketExplosion) {
            if (this.Targeting.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }

            if (this.NoSword.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }

            S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();

            float motionX = packet.func_149149_c(),
                    motionY = packet.func_149144_d(),
                    motionZ = packet.func_149147_e();

            if (this.chance.getValue() != 100.0D) {
                double ch = Math.random();
                if (ch >= this.chance.getValue() / 100.0D) {
                    return;
                }
            }


            if (this.horizontal.getValue() != 100.0D) {
                motionX *= this.horizontal.getValue() / 100.0D;
                motionZ *= this.horizontal.getValue() / 100.0D;
            }

            if (this.vertical.getValue() != 100.0D) {
                motionY *= this.vertical.getValue() / 100.0D;
            }
            ReflectionUtil.setFieldValue(packet, motionX, "field_149152_f");
            ReflectionUtil.setFieldValue(packet, motionY, "field_149153_g");
            ReflectionUtil.setFieldValue(packet, motionZ, "field_149159_h");
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        setSuffix(String.format("%s%s %s%s", horizontal.getValue(), "%", vertical.getValue(), "%"));
    }
}
