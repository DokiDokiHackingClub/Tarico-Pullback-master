package dev.tarico.utils.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BlockUtils {
    public static Block getBlock(BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static boolean isSolidFullCube(Block block) {
        return ((Material) ReflectionUtil.getFieldValue(block, "blockMateria", "field_149764_J")).blocksMovement() && block.isFullCube();
    }
}
