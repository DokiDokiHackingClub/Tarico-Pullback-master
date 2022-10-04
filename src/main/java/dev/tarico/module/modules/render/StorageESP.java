package dev.tarico.module.modules.render;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.render.Colors;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StorageESP extends Module {
    private static Minecraft mc;

    static {
        StorageESP.mc = Minecraft.getMinecraft();
    }

    private final BooleanValue<Boolean> Chest = new BooleanValue<>("Chest", true);
    private final BooleanValue<Boolean> EnderChest = new BooleanValue<>("EnderChest", false);

    public StorageESP() {
        super("StorageESP", "Render Chests ESP", ModuleType.Render);
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent ev) {
        for (final TileEntity te : mc.theWorld.loadedTileEntityList) {
            if (te instanceof TileEntityChest && this.Chest.getValue()) {
                int rgb = Colors.YELLOW.getColor().getRGB();
                if (te.getBlockType() != Blocks.trapped_chest) {
                    RenderHelper.drawBlockESP(te.getPos(), rgb);
                }

            }
            if (te instanceof TileEntityEnderChest && this.EnderChest.getValue()) {
                int rgb = Colors.PURPLE.getColor().getRGB();
                RenderHelper.drawBlockESP(te.getPos(), rgb);
            }
        }
    }
}
