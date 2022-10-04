package dev.tarico.module.modules.utils;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class AutoDoor extends Module {
    public AutoDoor() {
        super("AutoDoor", "Auto Open Doors", ModuleType.Utils);
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent e) {
        final double yaw = MoveUtils.getDirection();
        final double x = mc.thePlayer.posX + -Math.sin(yaw) * 1;
        final double z = mc.thePlayer.posZ + Math.cos(yaw) * 1;

        final BlockPos pos = new BlockPos(x, mc.thePlayer.posY, z);
        final Block b = mc.theWorld.getBlockState(pos).getBlock();
        if (b instanceof BlockDoor) {
            if (!BlockDoor.isOpen(mc.theWorld, pos)) {
                mc.thePlayer.swingItem();
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);
            }
        }
    }
}
