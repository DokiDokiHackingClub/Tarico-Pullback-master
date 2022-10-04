package dev.tarico.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Helper {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String message) {
        if (nullCheck())
            return;
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[" + EnumChatFormatting.LIGHT_PURPLE + "Tarico" + EnumChatFormatting.DARK_PURPLE + "]" + message));
    }

    public static void sendMessageWithoutPrefix(String message) {
        if (nullCheck())
            return;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static boolean nullCheck() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer == null || mc.theWorld == null;
    }
}

