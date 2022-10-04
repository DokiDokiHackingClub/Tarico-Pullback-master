package dev.tarico.api.pluginapi;

import net.minecraft.util.BlockPos;


/**
 * 方块位置数据
 */
public class BlockPosition {

    public int x, y, z = 0;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPosition getBlockPosition(BlockPos blockPos) {
        return new BlockPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public BlockPos getNativeBlockPos() {
        return new BlockPos(x, y, z);
    }
}
