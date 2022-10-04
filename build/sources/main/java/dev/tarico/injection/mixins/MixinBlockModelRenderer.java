package dev.tarico.injection.mixins;


import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;


@Mixin(value = {BlockModelRenderer.class})
public class MixinBlockModelRenderer {
    @Shadow
    public boolean renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides) {
        return false;
    }

    @Shadow
    public boolean renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides) {
        return checkSides;
    }

    /**
     * @author Czf_233
     * @reason Xray
     */
    @Overwrite
    public boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();
        try {
            Block block = blockStateIn.getBlock();
            if (ModuleManager.instance.getModule(Xray.class).getState()) {
                for (EnumFacing enumfacing : EnumFacing.VALUES) {
                    final List list = modelIn.getFaceQuads(enumfacing);
                    if (!list.isEmpty()) {
                        final BlockPos blockpos = blockPosIn.offset(enumfacing);
                        if (!checkSides || block.isBlockSolid(blockAccessIn, blockpos, enumfacing)) {

                            if (ModuleManager.instance.getModule(Xray.class).getState()) {
                                final Block blockk = Minecraft.getMinecraft().theWorld.getBlockState(blockPosIn).getBlock();
                                if (blockk instanceof BlockOre || blockk instanceof BlockRedstoneOre) {
                                    final double x = blockPosIn.getX();
                                    final double y = blockPosIn.getY();
                                    final double z = blockPosIn.getZ();
                                    if (Minecraft.getMinecraft().thePlayer.getDistance(x, y, z) <= Xray.getDistance()) {
                                        final BlockPos pos = new BlockPos(x, y, z);
                                        if (!Xray.blockPosList.contains(pos)) {
                                            Xray.blockPosList.add(pos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return flag ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides) : this.renderModelStandard(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides);
        } catch (Throwable var11) {
            CrashReport crashreport = CrashReport.makeCrashReport((Throwable) var11, (String) "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo((CrashReportCategory) crashreportcategory, (BlockPos) blockPosIn, (IBlockState) blockStateIn);
            crashreportcategory.addCrashSection("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }
}
