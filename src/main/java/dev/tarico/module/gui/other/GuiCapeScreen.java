package dev.tarico.module.gui.other;

import dev.tarico.management.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class GuiCapeScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField nameField;

    public GuiCapeScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        this.nameField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        }

        this.nameField.setText(this.nameField.getText().replace(" ", "").replace("#", "").replace("_NONE", ""));
    }

    public void initGui() {
        this.nameField = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - 100, this.height / 6 + 20, 200, 20);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 40 + 22 * 5, "Apply"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 6 + 40 + 22 * 6, "Cancel"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == 3) {
            try {

            } catch (Exception e) {
                BufferedImage image = getCapeFromUrl(nameField.getText());
                CapeManager.capeLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("czfcape_", new DynamicTexture(image));
            }
            mc.displayGuiScreen(this.parent);
        }

        if (button.id == 4) {
            mc.displayGuiScreen(this.parent);
        }
    }


    private BufferedImage getCapeFromUrl(String imageLink) throws IOException {
        return ImageIO.read(new URL(imageLink));
    }


    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.nameField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Cape URL", this.width / 2 - 89, this.height / 6 + 10, 0xFFFFFF);
        this.nameField.drawTextBox();

        this.drawCenteredString(this.fontRendererObj, "Apply Custom Capes.", this.width / 2, 30, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
