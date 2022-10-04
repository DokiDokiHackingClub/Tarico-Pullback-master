package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.timer.TimerUtil;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public class ChestStealer extends Module {
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 50.0, 0.0, 1000.0, 10.0);
    private final TimerUtil timer = new TimerUtil();

    public ChestStealer() {
        super("ChestStealer", "Auto Steal Chest", ModuleType.Utils);
        this.inVape = true;
        this.vapeName = "ChestSteal";
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventTick event) {
        if (mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.hasReached(this.delay.getValue())) {
                    mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                    this.timer.reset();
                }
                ++i;
            }
            if (this.isEmpty()) {
                mc.thePlayer.closeScreen();
            }
        }
    }

    private boolean isEmpty() {
        if (mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() != null) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
}

