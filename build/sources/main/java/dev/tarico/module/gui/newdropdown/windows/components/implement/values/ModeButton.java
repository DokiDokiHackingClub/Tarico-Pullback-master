package dev.tarico.module.gui.newdropdown.windows.components.implement.values;

import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.newdropdown.windows.Component;
import dev.tarico.module.gui.newdropdown.windows.components.ModuleButton;
import dev.tarico.module.modules.Module;
import dev.tarico.module.value.ModeValue;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ModeButton extends Component {

    private boolean hovered;
    private final ModuleButton parent;
    private final ModeValue set;
    private int offset;
    private int x;
    private int y;
    private final Module mod;

    private final int modeIndex;

    public ModeButton(ModeValue set, ModuleButton moduleButton, Module mod, int offset) {
        this.set = set;
        this.parent = moduleButton;
        this.mod = mod;
        this.x = moduleButton.parent.getX() + moduleButton.parent.getWidth();
        this.y = moduleButton.parent.getY() + moduleButton.offset;
        this.offset = offset;
        this.modeIndex = 0;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 1, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0).getRGB());
        FontLoaders.F18.drawString(set.getName() + ": " + set.getValue(), (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
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
            final ModeValue m = this.set;
            final Enum current = (Enum) m.getValue();
            final int next = (current.ordinal() + 1 >= m.getModes().length) ? 0 : (current.ordinal() + 1);
            this.set.setValue(m.getModes()[next]);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
