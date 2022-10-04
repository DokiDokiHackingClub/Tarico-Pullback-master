package dev.tarico.injection.mixins;


import dev.tarico.event.EventBus;
import dev.tarico.event.events.misc.EventLoadWorld;
import net.minecraft.client.gui.GuiDownloadTerrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDownloadTerrain.class)
public class MixinGuiDownloadTerrain {
    @Inject(method = "initGui", at = @At("HEAD"))
    public void initGui(CallbackInfo ci) {
        EventBus.getInstance().call(new EventLoadWorld());
    }
}
