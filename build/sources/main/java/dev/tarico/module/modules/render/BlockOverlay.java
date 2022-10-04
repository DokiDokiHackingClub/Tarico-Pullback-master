package dev.tarico.module.modules.render;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class BlockOverlay extends Module {
    public NumberValue<Number> r = new NumberValue<>("Red", 255, 0, 255, 1);
    public NumberValue<Number> g = new NumberValue<>("Green", 155, 0, 255, 1);
    public NumberValue<Number> b = new NumberValue<>("Blue", 255, 0, 255, 1);
    public BooleanValue<Boolean> str = new BooleanValue<>("draw string", true);

    public BlockOverlay() {
        super("BlockOverlay", "Show your block hitbox overlay", ModuleType.Render);
    }

    @SubscribeEvent
    public void onRender3D(final RenderWorldLastEvent event) {
        try {
            if (mc.objectMouseOver == null)
                return;
            if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return;
            if (mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockAir)
                return;
            RenderHelper.drawBlockESP(mc.objectMouseOver.getBlockPos(), new Color(r.getValue().intValue(), g.getValue().intValue(), b.getValue().intValue()).getRGB());
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        if (!str.getValue())
            return;
        if (mc.objectMouseOver == null)
            return;
        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return;
        if (mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockAir)
            return;
        final ScaledResolution res = new ScaledResolution(BlockOverlay.mc);
        final int x = res.getScaledWidth() / 2 + 6;
        final int y = res.getScaledHeight() / 2 - 1;
        final BlockPos pos = BlockOverlay.mc.objectMouseOver.getBlockPos();
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        final int id = Block.getIdFromBlock(block);
        final String s = block.getLocalizedName() + EnumChatFormatting.GRAY + " ID:" + id;
        RenderUtil.drawRect(x + 3.0f, y - 1.65f, x + 4.0f + mc.fontRendererObj.getStringWidth(s), y - 2.65f + mc.fontRendererObj.FONT_HEIGHT, new Color(0, 0, 0, 144).getRGB());
        mc.fontRendererObj.drawStringWithShadow(s, x + 4.0f, y - 1.65f, -1);
    }
}
