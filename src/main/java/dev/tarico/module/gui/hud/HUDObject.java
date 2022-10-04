package dev.tarico.module.gui.hud;

import dev.tarico.Client;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.Value;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.lang.reflect.Field;

public class HUDObject extends Gui {
    public static Minecraft mc = Minecraft.getMinecraft();
    public Module m;
    int PosX = 0;
    int PosY = 0;
    int width;
    String name;
    private float dragX, dragY;
    private boolean drag = false;
    private boolean hideBar = false;


    public HUDObject(int width, String name) {
        this.width = width;
        this.name = name;
        m = new Module(name, "enable " + name, ModuleType.HUD);
        ModuleManager.instance.registerModule(m);
        for (final Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(this);
                if (obj instanceof Value) m.getValues().add((Value<?>) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ModuleManager.instance.sortModules();
    }

    public void setHide(boolean b) {
        this.hideBar = b;
    }

    public HUDObject(int PosX, int PosY, int width, String name) {
        this.PosX = PosX;
        this.PosY = PosY;
        this.width = width;
        this.name = name;
    }

    public boolean doDraw() {
        return true;
    }

    public String getTitle() {
        return name;
    }

    public int getPosX() {
        return PosX;
    }

    public void setPosX(int posX) {
        PosX = posX;
    }

    public int getPosY() {
        return PosY;
    }

    public void setPosY(int posY) {
        PosY = posY;
    }

    public void drawHUD(int x, int y, float partialTicks) {
    }

    public void doDrag(int mouseX, int mouseY) {
        if (this.drag && m.getState()) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.PosX = (int) (mouseX - this.dragX);
            this.PosY = (int) (mouseY - this.dragY);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (mouseX > this.PosX - 2 && mouseX < this.PosX + getWidth() && mouseY > this.PosY - 2 && mouseY < this.PosY + 17 && m.getState()) {
            if (button == 1) {
                m.toggle();
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.PosX;
                this.dragY = mouseY - this.PosY;
            }
        }
    }

    public void draw(float partialTicks) {
        if (!doDraw())
            return;
        int color = (mc.currentScreen instanceof GuiChat) ? new Color(0x01B97A).getRGB() : Client.instance.mainColor.getColor().getRGB();

        if (m.getState()) {
            if (mc.currentScreen instanceof GuiChat) {
                RenderUtil.drawBlurRect2(PosX, PosY, PosX + width, PosY + 10, color);
                FontLoaders.I14.drawString("P", PosX + 2, PosY + 4, -1);
                FontLoaders.F14.drawString(getName(), PosX + 3 + FontLoaders.I14.getStringWidth("P"), PosY + 4, -1);
            } else {
                if (!hideBar) {
                    RenderUtil.drawBlurRect2(PosX, PosY, PosX + width, PosY + 10, color);
                    FontLoaders.F14.drawString(getTitle(), PosX + 2, PosY + 4, -1);
                }
            }
            drawHUD(PosX, PosY + 10, partialTicks);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }
}
