package dev.tarico.module.gui.newdropdown;

import dev.tarico.Client;
import dev.tarico.module.gui.AbstractClickGui;
import dev.tarico.module.gui.newdropdown.windows.Component;
import dev.tarico.module.gui.newdropdown.windows.Window;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;


public class GuiNewDropdown extends AbstractClickGui {

    public static ArrayList<Window> windows;
    public static int color = -1;

    public GuiNewDropdown() {
        windows = new ArrayList<>();
        int frameX = 5;
        for (ModuleType category : ModuleType.values()) {
            Window window = new Window(category);
            window.setX(frameX);
            windows.add(window);
            frameX += window.getWidth() + 1;
        }
    }

    public void onGuiClosed() {
        Client.instance.configLoader.saveSetting();
        mc.entityRenderer.isShaderActive();
        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/fxaa.json"));
    }

    @Override
    public void doInit() {
        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        super.doInit();
    }

    @Override
    public void drawScr(int mouseX, int mouseY, float partialTicks) {
        for (Window window : windows) {
            window.renderFrame(this.fontRendererObj);
            for (Component comp : window.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClick(mouseX, mouseY, mouseButton);

        for (Window window : windows) {
            if (window.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
                window.setDrag(true);
                window.dragX = mouseX - window.getX();
                window.dragY = mouseY - window.getY();
            }
            if (window.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                window.setOpen(!window.isOpen());
            }
            if (window.isOpen()) {
                if (!window.getComponents().isEmpty()) {
                    for (Component component : window.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Window window : windows) {
            if (window.isOpen() && keyCode != 1) {
                if (!window.getComponents().isEmpty()) {
                    for (Component component : window.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {
        for (Window window : windows) {
            window.setDrag(false);
        }
        for (Window window : windows) {
            if (window.isOpen()) {
                if (!window.getComponents().isEmpty()) {
                    for (Component component : window.getComponents()) {
                        component.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
