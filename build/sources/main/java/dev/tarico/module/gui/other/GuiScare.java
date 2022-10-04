package dev.tarico.module.gui.other;

import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.render.SoundFxPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class GuiScare extends GuiScreen {

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        if(!Minecraft.getMinecraft().isFullScreen())
            Minecraft.getMinecraft().toggleFullscreen();
        ScaledResolution sr = new ScaledResolution(mc);
        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.SCARE,-8);
        RenderUtil.drawImage(new ResourceLocation("client/clickgui/scare.png"),0,0,sr.getScaledWidth(),sr.getScaledHeight());
    }

    @Override
    public void onGuiClosed() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScare());
    }
}
