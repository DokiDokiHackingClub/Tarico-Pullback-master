package dev.tarico.module.gui.mainmenu;

import dev.tarico.Client;
import dev.tarico.module.gui.alt.GuiAltManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.list.Button;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.render.ZoomUtil;
import dev.tarico.utils.render.blur.BlurBuffer;
import dev.tarico.utils.render.shader.implementations.MenuShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public final class GuiCustomMainMenu extends GuiScreen {
    public Minecraft mc = Minecraft.getMinecraft();

    public ScaledResolution sr;

    ArrayList<Button> arrayList=new ArrayList<>();
    private final ResourceLocation logo;
    private final MenuShader backgroundShader;
    private final float zoomValue = 0.298f;
    private ZoomUtil LogoZoom, singleplayerZoom, multiplayerZoom, altsZoom, settingsZoom, quitZoom;

    public GuiCustomMainMenu(int pass) {
        backgroundShader = new MenuShader(pass);
        logo = new ResourceLocation("fonts/logo.png");

    }

    @Override
    public void initGui() {
        sr = new ScaledResolution(mc);
        LogoZoom = new ZoomUtil(sr.getScaledWidth() / 2f + 5, sr.getScaledHeight() / 2f - 105, 24 + 90, 24, 12, zoomValue, 6);
        singleplayerZoom = new ZoomUtil(sr.getScaledWidth() / 2f - 12 - 40, sr.getScaledHeight() / 2f - 60, 24 + 90, 24, 12, zoomValue, 6);
        multiplayerZoom = new ZoomUtil(sr.getScaledWidth() / 2f - 12 - 40, sr.getScaledHeight() / 2f - 30, 24 + 90, 24, 12, zoomValue, 6);
        altsZoom = new ZoomUtil(sr.getScaledWidth() / 2f - 12 - 40, sr.getScaledHeight() / 2f, 24 + 90, 24, 12, zoomValue, 6);
        settingsZoom = new ZoomUtil(sr.getScaledWidth() / 2f - 12 - 40,  sr.getScaledHeight() / 2f + 30, 24 + 90, 24, 12, zoomValue, 6);
        quitZoom = new ZoomUtil(sr.getScaledWidth() / 2f - 12 - 40, sr.getScaledHeight() / 2f + 60, 24 + 90, 24, 12, zoomValue, 6);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        backgroundShader.render(sr);
        singleplayerZoom.update(mouseX, mouseY);
        multiplayerZoom.update(mouseX, mouseY);
        altsZoom.update(mouseX, mouseY);
        settingsZoom.update(mouseX, mouseY);
        quitZoom.update(mouseX, mouseY);
        BlurBuffer.blurRoundArea(singleplayerZoom.getX(), singleplayerZoom.getY(), singleplayerZoom.getWidth(), singleplayerZoom.getHeight(), 8);
        FontLoaders.B18.drawCenteredStringWithShadow("SinglePlayer", sr.getScaledWidth() / 2f + 5f, sr.getScaledHeight() / 2f - 52, -1);
        BlurBuffer.blurRoundArea(multiplayerZoom.getX(), multiplayerZoom.getY(), multiplayerZoom.getWidth(), multiplayerZoom.getHeight(), 8);
        FontLoaders.B18.drawCenteredStringWithShadow("MultiPlayer", sr.getScaledWidth() / 2f + 5f, sr.getScaledHeight() / 2f - 22, -1);
        BlurBuffer.blurRoundArea(altsZoom.getX(), altsZoom.getY(), altsZoom.getWidth(), altsZoom.getHeight(), 8);
        FontLoaders.B18.drawCenteredStringWithShadow("Alt Manager", sr.getScaledWidth() / 2f + 5f, sr.getScaledHeight() / 2f + 8, -1);
        BlurBuffer.blurRoundArea(settingsZoom.getX(), settingsZoom.getY(), settingsZoom.getWidth(), settingsZoom.getHeight(), 8);
        FontLoaders.B18.drawCenteredStringWithShadow("Options", sr.getScaledWidth() / 2f + 5f, sr.getScaledHeight() / 2f + 38, -1);
        BlurBuffer.blurRoundArea(quitZoom.getX(), quitZoom.getY(), quitZoom.getWidth(), quitZoom.getHeight(), 8);
        FontLoaders.B18.drawCenteredStringWithShadow("Quit Game", sr.getScaledWidth() / 2f + 5f, sr.getScaledHeight() / 2f + 68, -1);
        String text = "Welcome back, " + Client.instance.user + "!";
        String text$1 = "Tarico - Build " + "220825";
        FontLoaders.F18.drawStringWithShadow(text$1, 2, new ScaledResolution(mc).getScaledHeight() - FontLoaders.F18.getHeight() - 1, -1);
        FontLoaders.F18.drawStringWithShadow(text, 2, new ScaledResolution(mc).getScaledHeight() - FontLoaders.F18.getHeight() - 10, -1);
        // MainMenu by Qianyeyou QQ:445851057 垃圾mainmenu
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtil.isHovered(LogoZoom.getX(), LogoZoom.getY(), LogoZoom.getWidth(), LogoZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 2));
        }
        if (RenderUtil.isHovered(singleplayerZoom.getX(), singleplayerZoom.getY(), singleplayerZoom.getWidth(), singleplayerZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (RenderUtil.isHovered(multiplayerZoom.getX(), multiplayerZoom.getY(), multiplayerZoom.getWidth(), multiplayerZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (RenderUtil.isHovered(altsZoom.getX(), altsZoom.getY(), altsZoom.getWidth(), altsZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            mc.displayGuiScreen(new GuiAltManager());
        }
        if (RenderUtil.isHovered(settingsZoom.getX(), settingsZoom.getY(), settingsZoom.getWidth(), settingsZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (RenderUtil.isHovered(quitZoom.getX(), quitZoom.getY(), quitZoom.getWidth(), quitZoom.getHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            mc.shutdown();
        }
    }

    public void setPass(int pass) {
        backgroundShader.setPass(pass);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void updateScreen() {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

