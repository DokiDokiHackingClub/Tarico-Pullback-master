package dev.tarico.module.modules.fun;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import dev.tarico.utils.render.Colors;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class FarmHunt extends Module {
    public static BooleanValue<Boolean> legitAura = new BooleanValue<>("Aura Targeting", false);
    public static NumberValue<Double> delay = new NumberValue<>("Sync delay", 1000d, 20d, 5000d, 100d);

    public static ArrayList<EntityLivingBase> entityLivingBases = new ArrayList<>();

    TimerUtil timer = new TimerUtil();

    public FarmHunt() {
        super("FarmHunt", "FarmHunt minigame Assistance", ModuleType.Fun);
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        if (timer.hasTimeElapsed(delay.getValue().intValue()))
            entityLivingBases.clear();

        if (mc.thePlayer.isDead)
            entityLivingBases.clear();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if ((entity instanceof EntityLivingBase) && !(entity instanceof EntityPlayer) && !entity.isInvisible()) {
                EntityLivingBase target = (EntityLivingBase) entity;
                if ((target.isSprinting() || target.hurtTime > 0 || (target.rotationYaw != 0 && target.rotationPitch != 0)) && !entityLivingBases.contains(target)) {
                    entityLivingBases.add(target);
//                    NotificationsManager.addNotification(new Notification("Detected a animal is control by player!", Notification.Type.Alert));
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (mc.thePlayer == null || mc.theWorld == null)
            entityLivingBases.clear();
    }

    @Override
    public void disable() {
        entityLivingBases.clear();
    }

    @SubscribeEvent
    public void onR3d(RenderWorldLastEvent e) {
        for (EntityLivingBase ent : entityLivingBases) {
            if (ent.isDead || ent.getHealth() < 0) continue;
            RenderHelper.drawTracers(ent, Colors.GREEN.getColor().getRGB(), 0.5F);
            RenderHelper.drawESP(ent, Colors.GREEN.getColor().getRGB(), true, 3);
        }
    }
}