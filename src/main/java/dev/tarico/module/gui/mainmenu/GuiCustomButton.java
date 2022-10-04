package dev.tarico.module.gui.mainmenu;

import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.utils.anim.AnimationUtils;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public final class GuiCustomButton
        extends net.minecraft.client.gui.GuiButton {
    private int color = 170;
    private float animation = 0.0f;

    public GuiCustomButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x - (int) ((double) Minecraft.getMinecraft().fontRendererObj.getStringWidth(buttonText) / 2.0), y, Minecraft.getMinecraft().fontRendererObj.getStringWidth(buttonText), 10, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        this.mouseDragged(mc, mouseX, mouseY);
        if (this.hovered) {
            if (this.color < 255) {
                this.color += 5;
            }
            if (this.animation < (double) this.width / 2.0) {
                this.animation = AnimationUtils.animate((float) ((double) this.width / 2.0f), this.animation, 0.10000000149011612f);
            }
        } else {
            if (this.color > 170) {
                this.color -= 5;
            }
            if (this.animation > 0.0) {
                this.animation = AnimationUtils.animate(0.0f, this.animation, 0.10000000149011612f);
            }
        }
        RenderUtil.drawRect((float) (this.xPosition + this.width / 2.0 - this.animation), this.yPosition + this.height + 2, this.xPosition + this.width / 2.0f + this.animation, this.yPosition + this.height + 3, new Color(this.color, this.color, this.color).getRGB());
        FontLoaders.F18.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, new Color(this.color, this.color, this.color).getRGB());
    }
}

