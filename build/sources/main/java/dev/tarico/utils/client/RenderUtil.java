package dev.tarico.utils.client;

import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.Mapping;
import dev.tarico.utils.render.blur.BlurBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.renderer.RenderGlobal.drawSelectionBoundingBox;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void drawRoundedRect2(float left, float top, float right, float bottom, float radius, int points, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (left < right) left = left + right - (right = left);
        if (top < bottom) top = top + bottom - (bottom = top);

        float[][] corners = {
                {right + radius, top - radius, 270},
                {left - radius, top - radius, 360},
                {left - radius, bottom + radius, 90},
                {right + radius, bottom + radius, 180}};

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(f, f1, f2, f3);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL_POLYGON, DefaultVertexFormats.POSITION);
        for (float[] c : corners) {
            for (int i = 0; i <= points; i++) {
                double anglerad = (Math.PI * (c[2] + i * 90.0F / points) / 180.0f);
                renderer.pos(c[0] + (Math.sin(anglerad) * radius), c[1] + (Math.cos(anglerad) * radius), 0).endVertex();
            }
        }

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static boolean isHovered(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
    }

    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        if (speed < 0.0f) speed = 0.0f;
        else if (speed > 1.0f) speed = 1.0f;
        float dif = Math.abs(current - target);
        float factor = dif * speed;
//        if (factor < 0.1f) factor = 0.1f;
        if (larger) current += factor;
        else current -= factor;
        return current;
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null)
            color(color.getRGB());
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        stop();
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void start() {
        enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(GL11.GL_CULL_FACE);
        enable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_BLEND);
        color(Color.white.getRGB());
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }


    public void translate(final double x, final double y) {
        GL11.glTranslated(x, y, 0);
    }


    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        /*        GlStateManager.resrectangleBorderedetColor();*/
    }


    public static void skeetRect(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x, y +4.0, x1 + size, y1 + size, 0.5, new Color(60, 60, 60).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 1.0, y +3.0, x1 + size - 1.0, y1 + size - 1.0, 1.0, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y - 1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(40, 40, 40).getRGB(), new Color(60, 60, 60).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y - 1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(22, 22, 22).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void skeetRectSmall(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x + 4.35, y + 0.5, x1 + size - 84.5, y1 + size - 4.35, 0.5, new Color(48, 48, 48).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 5.0, y + 1.0, x1 + size - 85.0, y1 + size - 5.0, 0.5, new Color(17, 17, 17).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void rectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        RenderUtil.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void rectangle(double left, double top, double right, double bottom, final int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        final float var11 = (float) (color >> 24 & 0xFF) / 255.0f;
        final float var6 = (float) (color >> 16 & 0xFF) / 255.0f;
        final float var7 = (float) (color >> 8 & 0xFF) / 255.0f;
        final float var8 = (float) (color & 0xFF) / 255.0f;
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }


    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }


    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        R2DUtils.enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        RenderUtil.glColor(topColor);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        RenderUtil.glColor(bottomColor);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        R2DUtils.disableGL2D();
    }


    public static void scale(float x, float y, float scale, Runnable data) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-x, -y, 0);
        data.run();
        GL11.glPopMatrix();
    }

    public static void circle(float var0, float var1, float var2, int var3) {
        arc(var0, var1, 0.0F, 360.0F, var2, var3);
    }

    public static void arc(float var0, float var1, float var2, float var3, float var4, int var5) {
        arcEllipse(var0, var1, var2, var3, var4, var4, var5);
    }

    public static void arcEllipse(float var0, float var1, float var2, float var3, float var4, float var5, int var6) {
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
        float var7;
        if (var2 > var3) {
            var7 = var3;
            var3 = var2;
            var2 = var7;
        }

        float var8 = (float) (var6 >> 24 & 255) / 255.0F;
        float var9 = (float) (var6 >> 16 & 255) / 255.0F;
        float var10 = (float) (var6 >> 8 & 255) / 255.0F;
        float var11 = (float) (var6 & 255) / 255.0F;
        Tessellator var12 = Tessellator.getInstance();
        var12.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var9, var10, var11, var8);
        if (var8 > 0.5F) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);

            for (float var14 = var3; var14 >= var2; var14 -= 4.0F) {
                float var15 = (float) Math.cos((double) var14 * 3.141592653589793D / 180.0D) * var4 * 1.001F;
                float var16 = (float) Math.sin((double) var14 * 3.141592653589793D / 180.0D) * var5 * 1.001F;
                GL11.glVertex2f(var0 + var15, var1 + var16);
            }

            GL11.glEnd();
            GL11.glDisable(2848);
        }

        GL11.glBegin(6);

        for (float var18 = var3; var18 >= var2; var18 -= 4.0F) {
            float var19 = (float) Math.cos((double) var18 * 3.141592653589793D / 180.0D) * var4;
            float var20 = (float) Math.sin((double) var18 * 3.141592653589793D / 180.0D) * var5;
            GL11.glVertex2f(var0 + var19, var1 + var20);
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawImage(ResourceLocation var0, int var1, int var2, int var3, int var4, Color var5) {
        new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f((float) var5.getRed() / 255.0F, (float) var5.getGreen() / 255.0F, (float) var5.getBlue() / 255.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(var0);
        Gui.drawModalRectWithCustomSizedTexture(var1, var2, 0.0F, 0.0F, var3, var4, (float) var3, (float) var4);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void doGlScissor1(float var0, float var1, float var2, float var3) {
        int var4 = getScaleFactor();
        GL11.glScissor((int) (var0 * (float) var4), (int) ((float) Minecraft.getMinecraft().displayHeight - var3 * (float) var4), (int) ((var2 - var0) * (float) var4), (int) ((var3 - var1) * (float) var4));
    }

    public static int getScaleFactor() {
        int var0 = 1;
        boolean var1 = Minecraft.getMinecraft().isUnicode();
        int var2 = Minecraft.getMinecraft().gameSettings.guiScale;
        if (var2 == 0) {
            var2 = 1000;
        }

        while (var0 < var2 && Minecraft.getMinecraft().displayWidth / (var0 + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (var0 + 1) >= 240) {
            ++var0;
        }

        if (var1 && var0 % 2 != 0 && var0 != 1) {
            --var0;
        }

        return var0;
    }

    public static void drawImage(ResourceLocation resourceLocation, int n, int n2, int n3, int n4) {
        new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(n, n2, 0.0f, 0.0f, n3, n4, n3, n4);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawShadow2(float x, float y, float width, float height) {
        width = width - x;
        height = height - y;
        drawTexturedRect(x - 9, y - 9, 9, 9, "paneltopleft");
        drawTexturedRect(x - 9, y + height, 9, 9, "panelbottomleft");
        drawTexturedRect(x + width, y + height, 9, 9, "panelbottomright");
        drawTexturedRect(x + width, y - 9, 9, 9, "paneltopright");
        drawTexturedRect(x - 9, y, 9, height, "panelleft");
        drawTexturedRect(x + width, y, 9, height, "panelright");
        drawTexturedRect(x, y - 9, width, 9, "paneltop");
        drawTexturedRect(x, y + height, width, 9, "panelbottom");
    }

    public static void drawShadow3(float x, float y, float x2, float y2) {
        if (!HUD.hshadow.getValue())
            return;
        x2 = x2 -x;
        y2 = y2 -y;
        drawTexturedRect(x - 9, y - 9, 9, 9, "paneltopleft");
        drawTexturedRect(x - 9, y + y2, 9, 9, "panelbottomleft");
        drawTexturedRect(x + x2, y + y2, 9, 9, "panelbottomright");
        drawTexturedRect(x + x2, y - 9, 9, 9, "paneltopright");
        drawTexturedRect(x - 9, y, 9, y2, "panelleft");
        drawTexturedRect(x + x2, y, 9, y2, "panelright");
        drawTexturedRect(x, y - 9, x2, 9, "paneltop");
        drawTexturedRect(x, y + y2, x2, 9, "panelbottom");
    }

    public static void drawShadow(float x, float y, float width, float height) {
        if (!HUD.hshadow.getValue())
            return;
        drawTexturedRect(x - 9, y - 9, 9, 9, "paneltopleft");
        drawTexturedRect(x - 9, y + height, 9, 9, "panelbottomleft");
        drawTexturedRect(x + width, y + height, 9, 9, "panelbottomright");
        drawTexturedRect(x + width, y - 9, 9, 9, "paneltopright");
        drawTexturedRect(x - 9, y, 9, height, "panelleft");
        drawTexturedRect(x + width, y, 9, height, "panelright");
        drawTexturedRect(x, y - 9, width, 9, "paneltop");
        drawTexturedRect(x, y + height, width, 9, "panelbottom");
    }

    public static void drawTexturedRect(float x, float y, float width, float height, String image) {
        glPushMatrix();
        final boolean enableBlend = glIsEnabled(GL_BLEND);
        final boolean disableAlpha = !glIsEnabled(GL_ALPHA_TEST);
        if (!enableBlend) glEnable(GL_BLEND);
        if (!disableAlpha) glDisable(GL_ALPHA_TEST);
        mc.getTextureManager().bindTexture(new ResourceLocation("shaders/" + image + ".png"));
        GlStateManager.color(1F, 1F, 1F, 1F);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        if (!enableBlend) glDisable(GL_BLEND);
        if (!disableAlpha) glEnable(GL_ALPHA_TEST);
        glPopMatrix();
    }

    public static void drawSolidBlockESP(BlockPos pos, int color) {
        double renderPosX = Mapping.renderPosX;
        double renderPosY = Mapping.renderPosY;
        double renderPosZ = Mapping.renderPosZ;
        double xPos = pos.getX() - renderPosX, yPos = pos.getY() - renderPosY, zPos = pos.getZ() - renderPosZ;
        double height = mc.theWorld.getBlockState(pos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(pos).getBlock().getBlockBoundsMinY();
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (color & 0xFF) / 255.0f;
        float f4 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(f, f2, f3, f4);
        drawOutlinedBoundingBox(new AxisAlignedBB(xPos, yPos, zPos, xPos + 1.0, yPos + height, zPos + 1.0));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glDisable(3042);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(2929);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }


    public static void drawOutlinedBoundingBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }


    public static void drawBlurRect2(double x1, double y1, double x2, double y2, int color) {
        if (HUD.blurShader.getValue()) {
            BlurBuffer.blurArea((float) x1, (float) y1, (float) (x2 - x1), (float) (y2 - y1));
        } else {
            drawRect(x1, y1, x2, y2, color);
        }
    }

    public static void drawBlurRect2(float x1, float y1, float x2, float y2, int color) {
        if (HUD.blurShader.getValue()) {
            BlurBuffer.blurArea(x1, y1, x2 - x1, y2 - y1);
        } else {
            drawRect(x1, y1, x2, y2, color);
        }
    }

    public static void drawBlurRect(float x1, float y1, float width, float height, int color) {
        if (HUD.blurShader.getValue()) {
            BlurBuffer.blurArea(x1, y1, width, height);
        } else {
            GL11.glPushMatrix();
            GlStateManager.enableBlend();
            glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            glEnable(2848);
            GL11.glPushMatrix();
            RenderUtil.color(color);
            GL11.glBegin(7);
            GL11.glVertex2d((double) x1 + width, y1);
            GL11.glVertex2d(x1, y1);
            GL11.glVertex2d(x1, (double) y1 + height);
            GL11.glVertex2d((double) x1 + width, (double) y1 + height);
            GL11.glEnd();
            GL11.glPopMatrix();
            glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
            GlStateManager.disableBlend();
            GL11.glPopMatrix();
        }
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void enableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, true);
    }

    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, false);
    }

    public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }

    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }

    public static void resetCaps() {
        glCapMap.forEach(RenderUtil::setGlState);
        glCapMap.clear();
    }

    public static void drawRect(float x1, float y1, float x2, float y2, int color) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glPushMatrix();
        RenderUtil.color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawRect(double x1, double y1, double x2, double y2, int color) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glPushMatrix();
        RenderUtil.color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = ((float) 1 / 255) * c.getRed();
        float g = ((float) 1 / 255) * c.getGreen();
        float b = ((float) 1 / 255) * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void drawAxisAlignedBB(final AxisAlignedBB axisAlignedBB, final Color color, final boolean outline, final boolean box, final float outlineWidth) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(outlineWidth);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor(color);

        if (outline) {
            glLineWidth(outlineWidth);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color.getRed(), color.getGreen(), color.getBlue(), 95);
            drawSelectionBoundingBox(axisAlignedBB);
        }

        if (box) {
            glColor(color.getRed(), color.getGreen(), color.getBlue(), outline ? 26 : 35);
            drawFilledBox(axisAlignedBB);
        }

        GlStateManager.resetColor();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public static void drawFilledBox(AxisAlignedBB mask) {
        GL11.glBegin(4);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.maxZ);
        GL11.glVertex3d(mask.minX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.minX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.minZ);
        GL11.glVertex3d(mask.maxX, mask.maxY, mask.maxZ);
        GL11.glVertex3d(mask.maxX, mask.minY, mask.maxZ);
        GL11.glEnd();
    }

    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static Framebuffer createFramebuffer(Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }
}


class R2DUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void enableGL2D() {
        GL11.glDisable(2929);
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        glEnable(3553);
        GL11.glDisable(3042);
        glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawRect(double x2, double y2, double x1, double y1, int color) {
        R2DUtils.enableGL2D();
        R2DUtils.glColor(color, color, color, color);
//							R2DUtils.drawRect(x2, y2, x1, y1, color);
        R2DUtils.drawRect(x2, y2, x1, y1);
        R2DUtils.disableGL2D();
    }

    private static void drawRect(double x2, double y2, double x1, double y1) {
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
    }
    
    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

}
