package dev.tarico.injection.mixins;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.*;

@Mixin({Block.class})
public abstract class MixinBlock {

    @Shadow
    @Final
    protected BlockState blockState;
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;
    @Mutable
    @Final
    @Shadow
    protected MapColor blockMapColor;


    @Mutable
    @Final
    @Shadow
    protected Material blockMaterial;
    Minecraft mc;
    int blockID;
    private Block BLOCK;


    @Shadow
    public static int getIdFromBlock(final Block blockIn) {
        return 0;
    }

    public boolean shouldSideBeRendered_(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return side == EnumFacing.DOWN && this.minY > 0.0D || side == EnumFacing.UP && this.maxY < 1.0D
                || side == EnumFacing.NORTH && this.minZ > 0.0D || side == EnumFacing.SOUTH && this.maxZ < 1.0D
                || side == EnumFacing.WEST && this.minX > 0.0D || side == EnumFacing.EAST && this.maxX < 1.0D
                || !worldIn.getBlockState(pos).getBlock().isOpaqueCube();
    }

    /**
     * @author
     * @reason
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public boolean shouldSideBeRendered(final IBlockAccess world, final BlockPos blockPos, final EnumFacing facing) {
        if (ModuleManager.instance.getModule(Xray.class).getState() && Xray.blockIdList.contains(getIdFromBlock((Block) (Object) this))) {
            final EnumFacing[] values;
            final int length = (values = EnumFacing.VALUES).length;
            int n = 0;
            do {
                final EnumFacing u2 = values[n];
                if (this.shouldSideBeRendered_(world, blockPos.offset(u2), u2)) {
                    return true;
                }
            } while (++n < length);
        }
        return this.shouldSideBeRendered_(world, blockPos, facing);
    }

    /**
     * @author ho3
     * @reason XRAY
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        return ModuleManager.instance.getModule(Xray.class).getState() ? (Xray.blockIdList.contains(getIdFromBlock((Block) (Object) this)) ? EnumWorldBlockLayer.SOLID : EnumWorldBlockLayer.TRANSLUCENT) : EnumWorldBlockLayer.SOLID;
    }

}
