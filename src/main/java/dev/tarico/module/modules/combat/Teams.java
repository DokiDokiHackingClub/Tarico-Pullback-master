package dev.tarico.module.modules.combat;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Teams extends Module {

    public Teams() {
        super("Teams", "Make Modules exclude teamed players", ModuleType.Combat);
    }

    public static boolean isOnSameTeam(Entity entity) {
        if (!ModuleManager.instance.getModule(Teams.class).getState()) return false;
        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
                if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
                        || entity.getDisplayName().getUnformattedText().length() <= 2) {
                    return false;
                }
            return Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2)
                    .equals(entity.getDisplayName().getUnformattedText().substring(0, 2));
            }
        return false;
    }
}
