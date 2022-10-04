package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;


public class MCP extends Module {
    private boolean down;

    public MCP() {
        super("MCP", "Middle Click Pearl", ModuleType.Utils);
    }

    public static boolean checkSlot(int slot) {
        ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);

        return itemInSlot != null && itemInSlot.getDisplayName().equalsIgnoreCase("ender pearl");
    }


    @EventTarget
    @SuppressWarnings("unused")
    private void onClick(EventPreUpdate e) {
        if (Mouse.isButtonDown(2) && !this.down) {
            for (int slot = 0; slot <= 8; slot++) {
                if (checkSlot(slot)) {
                    mc.thePlayer.inventory.currentItem = slot;
                    return;
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown(2)) {
            this.down = false;
        }
    }
}
