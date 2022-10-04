package dev.tarico.module.gui.newdropdown.windows.components;

import dev.tarico.Client;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.newdropdown.windows.Component;
import dev.tarico.module.gui.newdropdown.windows.components.implement.values.ModeButton;
import dev.tarico.module.gui.newdropdown.windows.components.implement.values.NumberButton;
import dev.tarico.module.gui.newdropdown.windows.components.implement.values.OptionButton;
import dev.tarico.module.modules.Module;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.module.value.Value;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;


public class ModuleButton extends Component {

    public Module mod;
    public dev.tarico.module.gui.newdropdown.windows.Window parent;
    public int offset;
    public boolean open = false;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    private final int height;

    public ModuleButton(Module mod, dev.tarico.module.gui.newdropdown.windows.Window parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<>();
        height = 12;
        int opY = offset + 12;
        if (mod.getValues() != null) {
            for (Value s : mod.getValues()) {
                if (s instanceof ModeValue) {
                    this.subcomponents.add(new ModeButton((ModeValue) s, this, mod, opY));
                    opY += 12;
                }
            }
            for (Value s : mod.getValues()) {
                if (s instanceof NumberValue) {
                    this.subcomponents.add(new NumberButton((NumberValue) s, this, opY));
                    opY += 12;
                }
            }
            for (Value s : mod.getValues()) {
                if (s instanceof BooleanValue) {
                    this.subcomponents.add(new OptionButton((BooleanValue) s, this, opY));
                    opY += 12;
                }
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.getState() ? Client.instance.mainColor.getColor().darker().getRGB() : new Color(11, 11, 11).getRGB()) : (this.mod.getState() ? Client.instance.mainColor.getColor().getRGB() : new Color(30, 30, 30).getRGB()));
        FontLoaders.F18.drawString(this.mod.getName(), (parent.getX() + 2) + 2, (parent.getY() + offset + 2) + 1, new Color(255, 255, 255).getRGB());
        if (this.subcomponents.size() > 0)
            FontLoaders.F18.drawString(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10), (parent.getY() + offset) + 4, new Color(255, 255, 255, 255).getRGB());
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent();
                }
                Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), new Color(44, 44, 44).getRGB());
            }
        }
    }


    @Override
    public int getHeight() {
        if (this.open) {
            return (12 * (this.subcomponents.size() + 1));
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
    }

}
