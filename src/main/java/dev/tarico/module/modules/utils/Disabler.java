package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.misc.EventLoadWorld;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.movement.Scaffold;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;

public class Disabler extends Module {
    private TimerUtil moveTimer = null;
    private final TimerUtil timer1 = new TimerUtil();
    private final TimerUtil timer2 = new TimerUtil();
    private final ArrayList<Packet<?>> packets = new ArrayList<>();
    String state = EnumChatFormatting.RED + "Error";
    private boolean cancel;
    private boolean active = false;

    public Disabler() {
        super("Disabler", "Spoof AntiCheat", ModuleType.Utils);
    }

    @EventTarget
    public void onWorld(EventLoadWorld e){
        enable();
    }

    public void enable() {
        Helper.sendMessageWithoutPrefix("Disabler Reset");
        active = false;
        state = EnumChatFormatting.YELLOW + "Disabler is launching, Please don't xia jb move";
        moveTimer = new TimerUtil();
    }

    @EventTarget
    public void onTick2(EventTick e){
        if(moveTimer == null)
            return;
        if(moveTimer.hasTimeElapsed(10000)){
            NotificationsManager.addNotification(new Notification("Disabler is working!", Notification.Type.Info));
            state = EnumChatFormatting.GREEN + "Disabler is working.";
            active = true;
            moveTimer = null;
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D e){
        ScaledResolution sr = new ScaledResolution(mc);
        FontLoaders.F18.drawCenteredStringWithShadow(state,sr.getScaledWidth() / 2 , 30, -1);
    }

    @EventTarget
    private void onTick(EventTick e) {
        if (timer1.hasTimeElapsed(10000, true)) {
            cancel = true;
            timer2.reset();
        }
    }

    @EventTarget
    private void onPacket(EventPacketSend e) {
        if(!active)
            return;
        if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            if (mc.thePlayer.ticksExisted < 50) {
                e.setCancelled(true);
            }
        }
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer) e.getPacket();
            if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
                e.setCancelled(true);
                packets.add(c03);
            }
            if (cancel) {
                if (!timer2.hasTimeElapsed(400, false)) {
                    if(!ModuleManager.instance.getModule(Scaffold.class).getState()) {
                        e.setCancelled(true);
                        packets.add(e.getPacket());
                    }
                } else {
                    packets.forEach(PacketUtils::sendPacketNoEvent);
                    packets.clear();
                    cancel = false;
                }
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacketReceive e) {
        if(!active)
            return;
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(packet.getX(), packet.getY(), packet.getZ(), false));
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
            mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
            mc.thePlayer.prevPosX = mc.thePlayer.posX;
            mc.thePlayer.prevPosY = mc.thePlayer.posY;
            mc.thePlayer.prevPosZ = mc.thePlayer.posZ;
            mc.displayGuiScreen(null);
            e.setCancelled(true);
        }
    }
}