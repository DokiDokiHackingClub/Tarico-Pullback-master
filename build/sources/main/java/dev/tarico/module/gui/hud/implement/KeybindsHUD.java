package dev.tarico.module.gui.hud.implement;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class KeybindsHUD extends HUDObject {
    public KeybindsHUD() {
        super(100, "KeyBinds");
    }

    @Override
    public void drawHUD(int x, int y, float p) {
        int modules = 0;
        ArrayList<Module> bindM = new ArrayList<>();
        for (Module m : ModuleManager.instance.getModules()) {
            if (m.getKey() != 0) {
                bindM.add(m);
                modules++;
            }
        }
        RenderUtil.drawShadow(x, y - 10, 100, (FontLoaders.F18.getHeight() + 2) * modules + 4 + 10);

        RenderUtil.drawBlurRect((float) x, (float) y, 100, (FontLoaders.F18.getHeight() + 2) * modules + 4, new Color(0, 0, 0, HUD.hudalpha.getValue().intValue()).getRGB());
        int y2 = (int) ((float) y + 2);
        for (Module m : bindM) {
            FontLoaders.F18.drawString(m.getName(), (float) x + 2, y2, -1);
            FontLoaders.F18.drawString(Keyboard.getKeyName(m.getKey()), (float) x + 100 - FontLoaders.F18.getStringWidth(Keyboard.getKeyName(m.getKey())) - 2, y2, -1);
            y2 += FontLoaders.F18.getHeight() + 2;
        }
    }
}
