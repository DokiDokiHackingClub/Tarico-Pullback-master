package dev.tarico.injection.mixins;

import dev.tarico.module.gui.hud.HUDManager;
import dev.tarico.module.gui.hud.HUDObject;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat {
    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_, CallbackInfo ci) {
        for (HUDObject object : HUDManager.hudObjects) {
            object.doDrag(p_drawScreen_1_, p_drawScreen_2_);
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    public void onGuiClosed(CallbackInfo ci) {
        HUDManager.savePos();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        for (HUDObject object : HUDManager.hudObjects) {
            object.mouseClick(mouseX, mouseY, mouseButton);
        }
    }
}
