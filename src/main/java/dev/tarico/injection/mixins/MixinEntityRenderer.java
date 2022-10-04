package dev.tarico.injection.mixins;

import dev.tarico.event.EventBus;
import dev.tarico.event.events.rendering.EventRender;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.NoHurtCam;
import dev.tarico.module.modules.render.ViewClip;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinEntityRenderer {
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void injectHurtCameraEffect(CallbackInfo callbackInfo) {
        if (ModuleManager.instance.getModule(NoHurtCam.class).getState())
            callbackInfo.cancel();
    }

    @Inject(method = "renderWorldPass", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableFog()V", shift = At.Shift.AFTER))
    private void eventRender3D(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        EventRender eventRender = new EventRender(pass, partialTicks, finishTimeNano);
        EventBus.getInstance().call(eventRender);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }
}