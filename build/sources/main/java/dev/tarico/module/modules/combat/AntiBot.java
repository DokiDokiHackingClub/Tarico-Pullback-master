package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.management.FriendManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.entity.Entity;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "Make Module not targeting Server Bots", ModuleType.Combat);
        this.inVape = true;
        this.setVSuffix("Hypixel");
    }

    @Native
    public static boolean isServerBot(Entity entity) {
        if (ModuleManager.instance.getModule(AntiBot.class).getState()) {
            return !entity.getDisplayName().getFormattedText().startsWith("\u00a7") || entity.isInvisible() || entity.getName().equals("Blink") || entity.getDisplayName().getFormattedText().toLowerCase().contains("npc") || FriendManager.isFriend(entity.getName());
        }
        return false;
    }
}
