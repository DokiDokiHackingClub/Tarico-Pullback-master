package dev.tarico.module.modules.render;

import dev.tarico.module.gui.list.ClickUi;
import dev.tarico.module.gui.newdropdown.GuiNewDropdown;
import dev.tarico.module.gui.powerx.PowerClickGui;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    public static PowerClickGui px = new PowerClickGui();
    public static ModeValue<Enum<Clickguis>> style = new ModeValue<>("Clickgui Style", Clickguis.values(), Clickguis.Dropdown);

    public ClickGui() {
        super("ClickGui", "Display ClickGui Screen", ModuleType.Render);
        this.setKey(Keyboard.KEY_RSHIFT);
        noToggle = true;
    }

    @Override
    public void enable() {
        this.setState(false);
        if (style.getValue() == Clickguis.Dropdown) {
            mc.displayGuiScreen(new GuiNewDropdown());
        } else if (style.getValue() == Clickguis.PowerX) {
            mc.displayGuiScreen(px);
        } else if (style.getValue() == Clickguis.List) {
            mc.displayGuiScreen(new ClickUi());
        }
    }

    enum Clickguis {
        Dropdown,
        PowerX,
        List
    }
}
