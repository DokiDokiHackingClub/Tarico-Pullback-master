package dev.tarico.utils;

import dev.tarico.utils.client.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class Mapping {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static double renderPosX = (double) ReflectionUtil.getFieldValue(mc.getRenderManager(), "renderPosX", "field_78725_b");
    public static double renderPosY = (double) ReflectionUtil.getFieldValue(mc.getRenderManager(), "renderPosY", "field_78726_c");
    public static double renderPosZ = (double) ReflectionUtil.getFieldValue(mc.getRenderManager(), "renderPosZ", "field_78728_n");
    public static Timer timer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");

    public static boolean getPressed(KeyBinding key) {
        return (boolean) ReflectionUtil.getFieldValue(key, "pressed", "field_74513_e");
    }
}
