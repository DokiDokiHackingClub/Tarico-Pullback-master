package dev.tarico.module.gui;

import dev.tarico.utils.render.GLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class AbstractClickGui extends GuiScreen {
    public static int config_X = -1;
    public static int config_Y = -1;
    public static float config_W = -1;
    public static float config_H = -1;
    public float scale;
    public float curWidth = 0;
    public float curHeight = 0;

    public AbstractClickGui() {
        this(2);
    }

    public AbstractClickGui(int scale) {
        this.scale = scale;
    }

    public void doInit() {
    }

    ;

    public void drawScr(int mouseX, int mouseY, float partialTicks) {
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseRelease(int mouseX, int mouseY, int state) {
    }

    @Override
    public void initGui() {
        this.doInit();
        if (config_W < 270) {
            config_W = 395;
        }
        if (config_H < 250) {
            config_H = 280;
        }
        if (config_X < 0) {
            config_X = (int) ((new ScaledResolution(mc).getScaledWidth() - config_W) / 2);
        }

        if (config_Y < 0) {
            config_Y = (int) ((new ScaledResolution(mc).getScaledHeight() - config_H) / 2);
        }
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GLUtils.INSTANCE.rescale(this.scale);
        curWidth = mc.displayWidth / scale;
        curHeight = mc.displayHeight / scale;
        this.drawScr(this.getRealMouseX(), this.getRealMouseY(), partialTicks);

        GLUtils.INSTANCE.rescaleMC();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.mouseClick(this.getRealMouseX(), this.getRealMouseY(), mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.mouseRelease(this.getRealMouseX(), this.getRealMouseY(), state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    public int getRealMouseX() {
        return (int) ((Mouse.getX() * (mc.displayWidth / scale)) / mc.displayWidth);
    }

    public int getRealMouseY() {
        float scaleHeight = (mc.displayHeight / scale);
        return (int) (scaleHeight - (Mouse.getY() * scaleHeight) / mc.displayHeight);
    }

    public void doGlScissor(int x, int y, float width, float height) {
        int scaleFactor = 1;
        float sc = scale;

        while (scaleFactor < sc && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        GL11.glScissor((int) (x * scaleFactor), (int) (mc.displayHeight - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }
}
