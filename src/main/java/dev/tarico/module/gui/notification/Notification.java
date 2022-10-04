package dev.tarico.module.gui.notification;

import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.utils.anim.AnimationUtils;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.render.Colors;
import dev.tarico.utils.timer.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Notification {
    public float x;
    public float width, height;
    public String name;
    public float lastTime;
    public TimerUtil timer;
    public Type type;
    public boolean setBack;
    private float fy, cy = 0;
    private final TimerUtil anitimer = new TimerUtil();
    private final AnimationUtils animationUtils = new AnimationUtils();
    private final AnimationUtils animationUtils2 = new AnimationUtils();


    public Notification(String name, Type type) {
        this.name = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = FontLoaders.F16.getStringWidth(name);
        this.height = 20;
    }

    public Notification(String name, Type type, float lastTime) {
        this.name = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = FontLoaders.F16.getStringWidth(name) + 25 + 2;
        this.height = 20;
    }

    public void render(float y) {
        fy = y;
        if (cy == 0) {
            cy = fy + 30;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, new Color(10, 10, 10, 220).getRGB());
        RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy + height - 1, (float) (sr.getScaledWidth_double() - x) + width, cy + height, new Color(180, 180, 180).getRGB());
        if (timer != null) {
            RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy + height - 1, (float) ((sr.getScaledWidth_double() - x) + (this.timer.getTime() - timer.lastMS) / (lastTime * 1000) * width), cy + height, type.getcolor());
        }
        FontLoaders.F16.drawString(name, (sr.getScaledWidth() - x) + 2, cy + 7, -1);
    }

    public void update() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (timer == null && Math.abs(x - width) < 0.1f) {
            timer = new TimerUtil();
            timer.reset();
        }
        if (anitimer.delay(10)) {
            cy = AnimationUtils.animate(fy, cy, 0.1f * (200f/Math.max(Minecraft.getDebugFPS(),1)));

            if (!setBack) {
                x = AnimationUtils.animate(width, x, 0.2f * (200f/Math.max(Minecraft.getDebugFPS(),1)));
            } else {
                x = AnimationUtils.animate(0, x, 0.2f * (200f/Math.max(Minecraft.getDebugFPS(),1)));
            }
            anitimer.reset();
        }
    }

    public enum Type {
        Info(Colors.INDIGO.getColor().getRGB()),
        Alert(Colors.YELLOW.getColor().getRGB()),
        Error(Colors.RED.getColor().getRGB());

        int c;

        Type(int color) {
            this.c = color;
        }

        public int getcolor() {
            return c;
        }
    }

}

