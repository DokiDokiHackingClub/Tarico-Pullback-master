package dev.tarico.injection.mixins;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.ItemPhysics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityItem f1, double f2, double f3, double f4, float f5, float j, CallbackInfo ci) {
        ItemPhysics physics = (ItemPhysics) ModuleManager.instance.getModule(ItemPhysics.class);
        if (physics.getState()) {
            physics.doRender(Minecraft.getMinecraft().getTextureManager(), f1, f2, f3, f4, f5, j);
            ci.cancel();
        }
    }

}
