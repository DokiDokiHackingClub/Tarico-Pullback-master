package dev.tarico.utils.render.shader.implementations;

import dev.tarico.utils.render.shader.ShaderProgram;
import net.minecraft.client.gui.ScaledResolution;

import static dev.tarico.utils.client.RenderUtil.mc;
import static org.lwjgl.opengl.GL11.*;

public class MenuShader {

    private final ShaderProgram menuShader = new ShaderProgram("fragment/menu.frag");

    private int pass;

    public MenuShader(int pass) {
        this.pass = pass;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public void render(final ScaledResolution scaledResolution) {
        menuShader.init();
        setupUniforms();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        menuShader.renderCanvas(scaledResolution);
        menuShader.uninit();
        pass++;
    }

    public void setupUniforms() {
        menuShader.setUniformf("time", pass / 100f);
        menuShader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

}
