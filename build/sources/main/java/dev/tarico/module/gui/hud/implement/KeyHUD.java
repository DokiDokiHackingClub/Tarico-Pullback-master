package dev.tarico.module.gui.hud.implement;

import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class KeyHUD extends HUDObject {
    float anima;
    float anima2;
    float anima3;
    float anima4;
    float anima5;
    float anima6;
    private double rainbowTick;

    public KeyHUD() {
        super(78, "KeyStrokes");
        setHide(true);
    }


    @Override
    public void drawHUD(int x, int y, float partialTicks) {
        CFontRenderer font2 = FontLoaders.F18;
        float xOffset = (float) x;
        int color = -1;
        float yOffset = (float) y;
        RenderUtil.drawBlurRect2((double) xOffset + 26, yOffset, xOffset + 51, yOffset + 25, new Color(0, 0, 0, 150).getRGB());//w
        RenderUtil.drawBlurRect2((double) xOffset + 26, (double) yOffset + 26, xOffset + 51, yOffset + 51, new Color(0, 0, 0, 150).getRGB());//s
        RenderUtil.drawBlurRect2((double) xOffset, (double) yOffset + 26, xOffset + 25, yOffset + 51, new Color(0, 0, 0, 150).getRGB());//a
        RenderUtil.drawBlurRect2((double) xOffset + 52, (double) yOffset + 26, xOffset + 77, yOffset + 51, new Color(0, 0, 0, 150).getRGB());//d
        RenderUtil.drawBlurRect2((double) xOffset + 1 + 77 / 2, (double) yOffset + 52, xOffset + 77, yOffset + 77, new Color(0, 0, 0, 150).getRGB());//LMB
        RenderUtil.drawBlurRect2((double) xOffset, (double) yOffset + 52, xOffset + 77 / 2, yOffset + 77, new Color(0, 0, 0, 150).getRGB());//RMB

        RenderUtil.drawShadow2(xOffset + 26, yOffset, xOffset + 51, yOffset + 25);//w
        RenderUtil.drawShadow2(xOffset + 26, yOffset + 26, xOffset + 51, yOffset + 51);//s
        RenderUtil.drawShadow2(xOffset, yOffset + 26, xOffset + 25, yOffset + 51);//a
        RenderUtil.drawShadow2(xOffset + 52, yOffset + 26, xOffset + 77, yOffset + 51);//d
        RenderUtil.drawShadow2(xOffset + 1 + 77 / 2, yOffset + 52, xOffset + 77, yOffset + 77);//LMB
        RenderUtil.drawShadow2(xOffset, yOffset + 52, xOffset + 77 / 2, yOffset + 77);//RMB
        font2.drawStringWithShadow("W", xOffset + (float) 34.5, yOffset + 9, color);
        font2.drawStringWithShadow("S", xOffset + (float) 36, yOffset + 35, color);
        font2.drawStringWithShadow("A", xOffset + (float) 10, yOffset + 35, color);
        font2.drawStringWithShadow("D", xOffset + (float) 62, yOffset + 35, color);
        font2.drawStringWithShadow("LMB", xOffset + (float) 10, yOffset + 60, color);
        font2.drawStringWithShadow("RMB", xOffset + (float) 50, yOffset + 60, color);
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            RenderUtil.drawRect((double) xOffset + 26, (double) yOffset, (double) (xOffset + 51), (double) (yOffset + 25), new Color(255, 255, 255, 128).getRGB());//w
        } else if (this.anima > 0) {
            this.anima = this.anima - 360f / Minecraft.getDebugFPS();
        }
        //s
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            RenderUtil.drawRect((double) xOffset + 26, (double) yOffset + 26, (double) (xOffset + 51), (double) (yOffset + 51), new Color(255, 255, 255, 128).getRGB());//s
        } else if (this.anima2 > 0) {
            this.anima2 = this.anima2 - 360f / Minecraft.getDebugFPS();
        }
        //a
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            RenderUtil.drawRect((double) xOffset, (double) yOffset + 26, (double) (xOffset + 25), (double) (yOffset + 51), new Color(255, 255, 255, 128).getRGB());//a
        } else if (this.anima3 > 0) {
            this.anima3 = this.anima3 - 360f / Minecraft.getDebugFPS();
        }
        //d
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            RenderUtil.drawRect((double) xOffset + 52, (double) yOffset + 26, (double) (xOffset + 77), (double) (yOffset + 51), new Color(255, 255, 255, 128).getRGB());//d
        } else if (this.anima4 > 0) {
            this.anima4 = this.anima4 - 360f / Minecraft.getDebugFPS();
        }
        //LMB
        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
            RenderUtil.drawRect((double) xOffset + 1 + 77.0 / 2, (double) yOffset + 52, (double) (xOffset + 77), (double) (yOffset + 77), new Color(255, 255, 255, 128).getRGB());//LMB
        } else if (anima5 > 0) {
            anima5 = anima5 - 360f / Minecraft.getDebugFPS();
        }
        //RMB
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            RenderUtil.drawRect(xOffset, (double) yOffset + 52, (double) (xOffset + 77 / 2), (double) (yOffset + 77), new Color(255, 255, 255, 128).getRGB());//RMB
        } else if (anima6 > 0) {
            anima6 = anima6 - 360f / Minecraft.getDebugFPS();
        }
    }
}
