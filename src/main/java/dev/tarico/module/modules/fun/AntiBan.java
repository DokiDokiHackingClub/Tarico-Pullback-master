package dev.tarico.module.modules.fun;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Random;

public class AntiBan extends Module {

    private static final char[] randomString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public AntiBan() {
        super("AntiBan", "Anti Hypixel Ban", ModuleType.Fun);
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = randomString[random.nextInt(randomString.length)];
        }
        return new String(array);
    }

    @Override
    public void enable() {
        this.setState(false);
        mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText(
                "§cYou are temporarily banned for §f29d 23h 59m 59s §cfrom this server!"
                        + "\n\n§7Reason: §fCheating through the use of unfair game advantages."
                        + "\n§7Find out more: " + EnumChatFormatting.AQUA + "§nhttps://www.hypixel.net/appeal" + "\n\n§7Ban ID:§f #"
                        + getRandomString(8).toUpperCase() + ""
                        + "\n§7Sharing your Ban ID may affect the processing of your appeal!"));
    }
}

