package dev.tarico.injection.mixins;


import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockGrass.class})
public abstract class MixinBlockGrass extends Block {

    public MixinBlockGrass(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author Czf_233
     * @reason Xray
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        return ModuleManager.instance.getModule(Xray.class).getState() ? super.getBlockLayer() : EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
}
