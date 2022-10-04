package dev.tarico.module.gui.font;

import net.minecraft.client.Minecraft;

public class Utils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static int add(int number, int add, int max) {
        return Math.min(number + add, max);
    }

}

