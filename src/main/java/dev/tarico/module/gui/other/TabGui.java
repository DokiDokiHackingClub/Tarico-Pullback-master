package dev.tarico.module.gui.other;


import dev.tarico.Client;
import dev.tarico.event.EventBus;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.misc.EventKey;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.RenderUtil;

import java.awt.*;

public class TabGui {
    public static int height = 16;
    private static int[] Sections;
    float modY = 0;
    float cateY = 0;
    private Section section = Section.TYPES;
    private ModuleType selectedType = ModuleType.values()[0];
    private Module selectedModule = null;
    private int currentType = 0;
    private int currentModule = 0;
    private int currentValue = 0;
    private int maxType;
    private int maxModule;
    private float modulesXanim = 0;

    static int[] Section() {
        int[] arrn;
        int[] arrn2 = Sections;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Section.values().length];
        try {
            arrn[Section.MODULES.ordinal()] = 2;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[Section.TYPES.ordinal()] = 1;
        } catch (NoSuchFieldError ignored) {
        }
        Sections = arrn;
        return Sections;
    }

    public void init() {
        ModuleType[] arrmoduleType = ModuleType.values();
        int n = arrmoduleType.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleType mt = arrmoduleType[n2];
            if (this.maxType <= Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4) {
                this.maxType = Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4;
            }
            ++n2;
        }
        for (Module m : ModuleManager.instance.getModules()) {
            if (this.maxModule > Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4) continue;
            this.maxModule = Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4;
        }
        this.maxModule += 12;
        this.maxType = Math.max(this.maxType, this.maxModule);
        this.maxModule += this.maxType;
        EventBus.getInstance().register(this);
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void renderTabGUI(EventRender2D e) {
        if (!HUD.tabgui.getValue()) {
            return;
        }
        if (HUD.wmmode.getValue() == HUD.WM.GameSense)
            return;
        CFontRenderer font = FontLoaders.F16;
        if (Helper.mc.gameSettings.showDebugInfo || !ModuleManager.instance.getModule(HUD.class).getState())
            return;
        int categoryY = height;
        float moduleY = categoryY;
        int valueY = categoryY;
        RenderUtil.drawBlurRect2(0, 0, 0, 0, 0);
        RenderUtil.drawBlurRect2(2.0f, categoryY, 55, categoryY + 12 * ModuleType.values().length, new Color(33, 33, 33, 180).getRGB());
        RenderUtil.drawShadow(2.0f, categoryY, 55 - 2, categoryY + 12 * ModuleType.values().length - categoryY);
        ModuleType[] moduleArray = ModuleType.values();
        int mA = moduleArray.length;
        int mA2 = 0;
        while (mA2 < mA) {
            ModuleType mt = moduleArray[mA2];
            if (this.selectedType == mt) {
                if (cateY != categoryY) {
                    cateY += (categoryY - cateY) / 10;
                }
                if (HUD.tab.getValue() == HUD.TabGUIS.Default) {
                    RenderUtil.drawBlurRect2(2, cateY, 55, (cateY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2, Client.instance.mainColor.getColor().getRGB());
                } else if (HUD.tab.getValue() == HUD.TabGUIS.Gradual) {
                    RenderUtil.drawGradientRect(2, cateY, 55, (cateY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2, Client.instance.mainColor.getColor().getRGB(), Client.instance.mainColor.getColor().brighter().getRGB());

                }
                moduleY = categoryY;
            }
            font.drawString(mt.name(), 6, categoryY + 4, -1);
            categoryY += 12;
            ++mA2;
        }
        if ((this.section == Section.MODULES || modulesXanim > 1)) {
            if (modulesXanim < 60) {
                modulesXanim += (60 - modulesXanim) / 20.0;
            }
            RenderUtil.drawBlurRect2(60, moduleY, 60 + modulesXanim, moduleY + 12 * ModuleManager.instance.getModulesInType(this.selectedType).size(), new Color(33, 33, 33, 180).getRGB());
            for (Module m : ModuleManager.instance.getModulesInType(this.selectedType)) {
                if (this.selectedModule == m) {
                    if (modY != moduleY) {
                        modY += (moduleY - modY) / 10;
                    }
                    RenderUtil.drawBlurRect2(60, modY, 60 + modulesXanim, (modY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2, Client.instance.mainColor.getColor().getRGB());
                }
                if (this.selectedModule == m) {
                    font.drawString(m.getName(), 64, moduleY + 3, m.getState() ? -1 : 11184810);
                } else {
                    font.drawString(m.getName(), 64, moduleY + 3, m.getState() ? -1 : 11184810);
                }
                moduleY += 12;
            }
        }
        if (modulesXanim > 0 && this.section != Section.MODULES) {
            modulesXanim -= 5;
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onKey(EventKey e) {
        if (!Helper.mc.gameSettings.showDebugInfo) {
            block0:
            switch (e.getKey()) {
                case 208: {
                    switch (TabGui.Section()[this.section.ordinal()]) {
                        case 1: {
                            ++this.currentType;
                            if (this.currentType > ModuleType.values().length - 1) {
                                this.currentType = 0;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            ++this.currentModule;
                            if (this.currentModule > ModuleManager.instance.getModulesInType(this.selectedType).size() - 1) {
                                this.currentModule = 0;
                            }
                            this.selectedModule = ModuleManager.instance.getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            ++this.currentValue;
                            if (this.currentValue > this.selectedModule.getValues().size() - 1) {
                                this.currentValue = 0;
                            }
                        }
                    }
                    break;
                }
                case 200: {
                    switch (TabGui.Section()[this.section.ordinal()]) {
                        case 1: {
                            --this.currentType;
                            if (this.currentType < 0) {
                                this.currentType = ModuleType.values().length - 1;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            --this.currentModule;
                            if (this.currentModule < 0) {
                                this.currentModule = ModuleManager.instance.getModulesInType(this.selectedType).size() - 1;
                            }
                            this.selectedModule = ModuleManager.instance.getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            --this.currentValue;
                            if (this.currentValue < 0) {
                                this.currentValue = this.selectedModule.getValues().size() - 1;
                            }
                        }
                    }
                    break;
                }
                case 205: {
                    if (TabGui.Section()[this.section.ordinal()] == 1) {
                        this.currentModule = 0;
                        this.selectedModule = ModuleManager.instance.getModulesInType(this.selectedType).get(this.currentModule);
                        this.section = Section.MODULES;
                        modulesXanim = 0;
                        break;
                    }
                    break;
                }
                case 28: {
                    switch (TabGui.Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.selectedModule.toggle();
                            break block0;
                        }
                        case 3: {
                            this.section = Section.MODULES;
                        }
                    }
                    break;
                }
                case 203: {
                    switch (TabGui.Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.section = Section.TYPES;
                            this.currentModule = 0;
                            break block0;
                        }
                        case 3: {

                        }
                    }
                }
            }
        }
    }

    public enum Section {
        TYPES,
        MODULES,
    }

}

