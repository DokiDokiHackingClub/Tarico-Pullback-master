package dev.tarico.injection.mixins;

import dev.tarico.event.EventBus;
import dev.tarico.event.events.misc.EventMouse;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.io.IOException;

@SideOnly(Side.CLIENT)
@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
    @Shadow
    private long lastMouseEvent;
    @Shadow
    private int eventButton;
    @Shadow
    private int touchValue;
    @Shadow
    protected Minecraft mc;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected abstract void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException;

    @Shadow
    protected abstract void mouseReleased(int mouseX, int mouseY, int state);

    @Shadow
    protected abstract void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);

    /**
     * @author QianyeYou
     * @reason Mouse
     */
    @Overwrite
    public void handleMouseInput() throws IOException {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
            EventBus.getInstance().call(new EventMouse(i, j, this.eventButton, EventMouse.Type.CLICK));
        } else if (k != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
            EventBus.getInstance().call(new EventMouse(i, j, this.eventButton, EventMouse.Type.RELEASED));
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
            EventBus.getInstance().call(new EventMouse(i, j, this.eventButton, EventMouse.Type.CLICK_MOVE));
        }
    }

    /**
     * @author Czf_233
     * @reason Invbackground
     */
    @Overwrite
    public void drawWorldBackground(int p_drawWorldBackground_1_) {
        if (this.mc.theWorld != null) {
            if (HUD.invback.getValue())
                RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16, HUD.invalpha.getValue().intValue()).getRGB());
        } else {
            this.drawBackground(p_drawWorldBackground_1_);
        }
    }


    @Shadow
    public void drawBackground(int p_drawBackground_1_) {
    }
}