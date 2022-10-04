package dev.tarico.module.gui.newdropdown.windows;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.newdropdown.windows.components.ModuleButton;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;


public class Window {

    public ArrayList<Component> components;
    public ModuleType category;
    public int dragX;
    public int dragY;
    private boolean open;
    private final int width;
    private int y;
    private int x;
    private final int barHeight;
    private boolean isDragging;

    public Window(ModuleType cat) {
        this.components = new ArrayList<>();
        this.category = cat;
        this.width = 88;
        this.x = 5;
        this.y = 5;
        this.barHeight = 13;
        this.dragX = 0;
        this.open = true;
        this.isDragging = false;
        int tY = this.barHeight;

        for (Module mod : ModuleManager.instance.getModules()) {
            if (!mod.getCategory().equals(cat)) continue;
            ModuleButton modModuleButton = new ModuleButton(mod, this, tY);
            this.components.add(modModuleButton);
            tY += 12;
        }
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void renderFrame(FontRenderer fontRenderer) {
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, new Color(14, 14, 14).getRGB());
        FontLoaders.F18.drawCenteredString(this.category.name(), (this.x + 40) + 3, (this.y + 0.0f) * 1 + 4, new Color(255, 255, 255, 255).getRGB());
        if (this.open) {
            if (!this.components.isEmpty()) {
                for (Component component : components) {
                    component.renderComponent();
                }
            }
        }
    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getWidth() {
        return width;
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }

}
