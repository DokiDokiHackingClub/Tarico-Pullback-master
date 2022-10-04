package dev.tarico.module.gui.newdropdown.windows.components.implement.values;

import dev.tarico.Client;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.newdropdown.windows.Component;
import dev.tarico.module.gui.newdropdown.windows.components.ModuleButton;
import dev.tarico.module.value.BooleanValue;
import net.minecraft.client.gui.Gui;

import java.awt.*;


public class OptionButton extends Component {

    private boolean hovered;
    private final BooleanValue<Boolean> op;
    private final ModuleButton parent;
    private int offset;
    private int x;
    private int y;

    public OptionButton(BooleanValue booleanValue, ModuleButton moduleButton, int offset) {
        this.op = booleanValue;
        this.parent = moduleButton;
        this.x = moduleButton.parent.getX() + moduleButton.parent.getWidth();
        this.y = moduleButton.parent.getY() + moduleButton.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
        Gui.drawRect(parent.parent.getX() + 1 + 5, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 3, parent.parent.getY() + offset + 9, new Color(89, 89, 89, 191).getRGB());
        FontLoaders.F18.drawString(this.op.getName(), (parent.parent.getX()) + 17, (parent.parent.getY() + offset + 2) + 1, new Color(255, 255, 255, 255).getRGB());
        if (this.op.getValue()) {
            Gui.drawRect(parent.parent.getX() + 1 + 5, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 3, parent.parent.getY() + offset + 9, Client.instance.mainColor.getColor().getRGB());
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.op.setValue(!op.getValue());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
