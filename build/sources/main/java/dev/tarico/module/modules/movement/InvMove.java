package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.ReflectionUtil;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.Arrays;
import java.util.List;

public class InvMove extends Module {
    private static final List<KeyBinding> keys = Arrays.asList(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    );
    public static NumberValue<Double> delay = new NumberValue<>("Delay", 340.0, 0.0, 5000.0, 100.0);
    private final BooleanValue<Boolean> packet = new BooleanValue<>("Send Packet", false);
    private final TimerUtil delayTimer = new TimerUtil();

    public InvMove() {
        super("InvMove", "Move when open inv", ModuleType.Movement);
        this.inVape = true;
        this.vapeName = "InvWalk";
    }

    public static void updateStates() {
        if (mc.currentScreen != null) {
            for (KeyBinding k : keys) {
                ReflectionUtil.setFieldValue(k, GameSettings.isKeyDown(k), "pressed", "field_74513_e");
            }
        }
    }

    @EventTarget
    public void onMotion(final EventPreUpdate event) {
        setSuffix("Delay: " + delay.getValue());
        if (mc.currentScreen instanceof GuiContainer) {
            if (delayTimer.hasTimeElapsed(delay.getValue().intValue())) {
                updateStates();
                delayTimer.reset();
            }
        }
    }

    @Native
    @EventTarget
    public void onPacketSend(final EventPacketSend event) {
        final Packet<?> p = event.getPacket();

        if (packet.getValue()) {
            if (p instanceof C0DPacketCloseWindow) {
                Helper.sendMessage("Close");
            }

            if (p instanceof C0EPacketClickWindow) {
                event.setCancelled(true);
            }

            if (p instanceof C16PacketClientStatus)
                Helper.sendMessage("C16");

            if (p instanceof C0FPacketConfirmTransaction)
                Helper.sendMessage("C0f");
        }
    }
}
