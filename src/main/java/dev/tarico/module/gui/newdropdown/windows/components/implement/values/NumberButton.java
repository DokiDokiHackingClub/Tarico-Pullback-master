package dev.tarico.module.gui.newdropdown.windows.components.implement.values;

import dev.tarico.Client;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.newdropdown.windows.Component;
import dev.tarico.module.gui.newdropdown.windows.components.ModuleButton;
import dev.tarico.module.value.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class NumberButton extends Component {

    private boolean hovered;

    private final NumberValue<Number> set;
    private final ModuleButton parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public NumberButton(NumberValue value, ModuleButton moduleButton, int offset) {
        this.set = value;
        this.parent = moduleButton;
        this.x = moduleButton.parent.getX() + moduleButton.parent.getWidth();
        this.y = moduleButton.parent.getY() + moduleButton.offset;
        this.offset = offset;
    }

    private static double roundToPlace(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(20, 20, 20).getRGB() : new Color(0, 0, 0, 191).getRGB());
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, Client.instance.mainColor.getColor().getRGB());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
        FontLoaders.F18.drawString(EnumChatFormatting.WHITE + this.set.getName() + ": " + EnumChatFormatting.RESET + this.set.getValue(), (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        double min = set.getMinimum().doubleValue();
        double max = set.getMaximum().doubleValue();

        renderWidth = (88) * (set.getValue().doubleValue() - min) / (max - min);

        if (dragging) {
            if (diff == 0) {
                set.setValue(set.getMinimum().doubleValue());
            } else {
                double newValue = roundToPlace(((diff / 88) * (max - min) + min));
                set.setValue(newValue);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
    }
}