package dev.tarico.module.modules.render;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

import java.awt.*;


public class Health extends Module {
    int lowHealth = 0;
    private int width;

    public Health() {
        super("Health", "health display", ModuleType.Render);
    }

    @EventTarget
    private void renderHud(EventRender2D event) {
        if (mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() < 10.0f) {
            this.width = 3;
        }
        if (mc.thePlayer.getHealth() >= 10.0f && mc.thePlayer.getHealth() < 100.0f) {
            this.width = 5;
        }
        mc.fontRendererObj.drawStringWithShadow("" + MathHelper.ceiling_float_int(mc.thePlayer.getHealth()), (float) (new ScaledResolution(mc).getScaledWidth() / 2 - this.width), (float) (new ScaledResolution(mc).getScaledHeight() / 2 - 5) - 7.0f - 5.0f, mc.thePlayer.getHealth() <= 10.0f ? new Color(255, 0, 0).getRGB() : new Color(0, 255, 0).getRGB());
    }

    @EventTarget
    public void onUpdate(EventPreUpdate event) {
        if (mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() < 6.0f && lowHealth == 0) {
            lowHealth = 1;
            NotificationsManager.addNotification(new Notification("Your health is low!", Notification.Type.Error));
        } else if (mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() > 6.0f && lowHealth == 1) {
            lowHealth = 0;
        }
    }
}
