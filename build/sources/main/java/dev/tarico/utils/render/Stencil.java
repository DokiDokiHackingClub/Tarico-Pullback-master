package dev.tarico.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public final class Stencil {
    private static final Stencil INSTANCE = new Stencil();
    private final HashMap<Integer, StencilFunc> stencilFuncs;
    private int layers;
    private boolean renderMask;

    public Stencil() {
        this.stencilFuncs = new HashMap();
        this.layers = 1;
    }

    public static Stencil getInstance() {
        return INSTANCE;
    }

    public static void checkSetupFBO() {
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

        if (fbo != null && fbo.depthBuffer > -1) {
            EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
            final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
            EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
            EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
            fbo.depthBuffer = -1;
        }
    }

    public static void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }

    public void setRenderMask(final boolean renderMask) {
        this.renderMask = renderMask;
    }

    public void startLayer() {
        if (this.layers == 1) {
            GL11.glClearStencil(0);
            GL11.glClear(1024);
        }

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        ++this.layers;

        if (this.layers > this.getMaximumLayers()) {
            System.out.println("StencilUtil: Reached maximum amount of layers!");
            this.layers = 1;
        }
    }

    public void stopLayer() {
        if (this.layers == 1) {
            System.out.println("StencilUtil: No layers found!");
            return;
        }

        --this.layers;

        if (this.layers == 1) {
            GL11.glDisable(GL11.GL_STENCIL_TEST);
        } else {
            final StencilFunc lastStencilFunc = this.stencilFuncs.remove(this.layers);

            if (lastStencilFunc != null) {
                lastStencilFunc.use();
            }
        }
    }

    public void clear() {
        GL11.glClearStencil(0);
        GL11.glClear(1024);
        this.stencilFuncs.clear();
        this.layers = 1;
    }

    public void setBuffer() {
        this.setStencilFunc(new StencilFunc(this, this.renderMask ? 519 : 512, this.layers, this.getMaximumLayers(), 7681, 7680, 7680));
    }

    public void setBuffer(final boolean set) {
        this.setStencilFunc(new StencilFunc(this, this.renderMask ? 519 : 512, set ? this.layers : (this.layers - 1), this.getMaximumLayers(), 7681, 7681, 7681));
    }

    public void cropOutside() {
        this.setStencilFunc(new StencilFunc(this, 517, this.layers, this.getMaximumLayers(), 7680, 7680, 7680));
    }

    public void cropInside() {
        this.setStencilFunc(new StencilFunc(this, 514, this.layers, this.getMaximumLayers(), 7680, 7680, 7680));
    }

    public StencilFunc getStencilFunc() {
        return this.stencilFuncs.get(this.layers);
    }

    public void setStencilFunc(final StencilFunc stencilFunc) {
        GL11.glStencilFunc(StencilFunc.func_func, StencilFunc.func_ref, StencilFunc.func_mask);
        GL11.glStencilOp(StencilFunc.op_fail, StencilFunc.op_zfail, StencilFunc.op_zpass);
        this.stencilFuncs.put(this.layers, stencilFunc);
    }

    public int getLayer() {
        return this.layers;
    }

    public int getStencilBufferSize() {
        return GL11.glGetInteger(GL11.GL_STENCIL_BITS);
    }

    public int getMaximumLayers() {
        return (int) (Math.pow(2.0D, getStencilBufferSize()) - 1.0D);
    }

    public void createCirlce(double x, double y, double radius) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++) {
            double sin = Math.sin(i * Math.PI / 180.0D) * radius;
            double cos = Math.cos(i * Math.PI / 180.0D) * radius;
            GL11.glVertex2d(x + sin, y + cos);
        }

        GL11.glEnd();
    }

    public void createRect(double x, double y, double x2, double y2) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static class StencilFunc {
        public static int func_func;
        public static int func_ref;
        public static int func_mask;
        public static int op_fail;
        public static int op_zfail;
        public static int op_zpass;

        public StencilFunc(final Stencil paramStencil, final int func_func, final int func_ref, final int func_mask, final int op_fail, final int op_zfail, final int op_zpass) {
            StencilFunc.func_func = func_func;
            StencilFunc.func_ref = func_ref;
            StencilFunc.func_mask = func_mask;
            StencilFunc.op_fail = op_fail;
            StencilFunc.op_zfail = op_zfail;
            StencilFunc.op_zpass = op_zpass;
        }

        public void use() {
            GL11.glStencilFunc(func_func, func_ref, func_mask);
            GL11.glStencilOp(op_fail, op_zfail, op_zpass);
        }
    }
}