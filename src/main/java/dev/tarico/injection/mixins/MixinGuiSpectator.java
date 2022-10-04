package dev.tarico.injection.mixins;

import dev.tarico.event.EventBus;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.utils.math.vector.Vec2i;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.tarico.utils.client.Helper.mc;

@SideOnly(Side.CLIENT)
@Mixin(GuiSpectator.class)
public class MixinGuiSpectator {

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        Vec2i mousePos = new Vec2i(
                Mouse.getX() * scaledresolution.getScaledWidth() / mc.displayWidth,
                scaledresolution.getScaledHeight() - Mouse.getY() *
                        scaledresolution.getScaledHeight() / mc.displayHeight - 1);
        EventBus.getInstance().call(new EventRender2D(partialTicks, scaledresolution, mousePos));
    }

}
