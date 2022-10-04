package dev.tarico.utils.render.blur;

import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.RenderUtil;

import java.awt.*;

public class BlurBuffer {
    public static void blurArea(float x, float y, float width, float height) {
        StencilUtil.initStencilToWrite();
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(-2).getRGB());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(HUD.radius.getValue().floatValue());
        StencilUtil.uninitStencilBuffer();
    }
    public static void blurRoundArea(float x, float y, float width, float height, int radius) {
        StencilUtil.initStencilToWrite();
        RenderUtil.drawRoundedRect2(x, y, x + width, y + height, radius, 6, new Color(-2).getRGB());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(8.0F);
        StencilUtil.uninitStencilBuffer();
    }
}
