package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
    static int old = 1;
    private final BooleanValue<Boolean> noSword = new BooleanValue<>("Ignore sword", false);
    public AutoTool() {
        super("AutoTool", "Auto Switch Tools", ModuleType.Utils);
        this.inVape = true;
    }

    public static void updateTool(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        float strength = 1.0F;
        int bestItemIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            if ((itemStack.getStrVsBlock(block) > strength)) {
                strength = itemStack.getStrVsBlock(block);
                bestItemIndex = i;
            }
        }
        if (bestItemIndex != -1) {
            mc.thePlayer.inventory.currentItem = bestItemIndex;
        }
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            mc.thePlayer.inventory.currentItem = old;
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onEvent(EventPreUpdate event) {
        if (this.noSword.getValue()) {
            if (mc.thePlayer.getCurrentEquippedItem() != null) {
                if ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
                    return;
                }
            }
        }
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        old = mc.thePlayer.inventory.currentItem;
        if (mc.objectMouseOver == null) {
            return;
        }
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        updateTool(pos);
    }
}
