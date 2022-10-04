package dev.tarico.injection.mixins;

import dev.tarico.event.EventBus;
import dev.tarico.event.events.rendering.EventBlur;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.hud.HUDManager;
import dev.tarico.module.modules.hudimplement.HotBar;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.math.vector.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static dev.tarico.utils.client.Helper.mc;

@SideOnly(Side.CLIENT)
@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    private Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (HUDManager.scoreboardHUD.m.getState())
            callbackInfo.cancel();
    }

    int rainbowTick = 0;

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && ModuleManager.instance.getModule(HotBar.class).getState()) {
            EntityPlayer entityPlayer = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

            int middleScreen = sr.getScaledWidth() / 2;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GuiIngame.drawRect(middleScreen - 91, sr.getScaledHeight() - 24, middleScreen + 90, sr.getScaledHeight(), new Color(0, 0, 0, 144).getRGB());
            GuiIngame.drawRect(middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 1, sr.getScaledHeight() - 24, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight() - 22 - 1 + 24, new Color(0, 0, 0, 144).getRGB());
            GuiIngame.drawRect(middleScreen - 91, sr.getScaledHeight() - 24, middleScreen + 90, sr.getScaledHeight() - 23, new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f)).getRGB());

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int j = 0; j < 9; ++j) {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(j, k, l, partialTicks, entityPlayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            ScaledResolution scaledresolution = new ScaledResolution(mc);
            Vec2i mousePos = new Vec2i(
                    Mouse.getX() * scaledresolution.getScaledWidth() / mc.displayWidth,
                    scaledresolution.getScaledHeight() - Mouse.getY() *
                            scaledresolution.getScaledHeight() / mc.displayHeight - 1);
            EventBus.getInstance().call(new EventRender2D(partialTicks, scaledresolution, mousePos));
            callbackInfo.cancel();
        }
    }

    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltip2(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        Vec2i mousePos = new Vec2i(
                Mouse.getX() * scaledresolution.getScaledWidth() / mc.displayWidth,
                scaledresolution.getScaledHeight() - Mouse.getY() *
                        scaledresolution.getScaledHeight() / mc.displayHeight - 1);
        EventBus.getInstance().call(new EventRender2D(partialTicks, scaledresolution, mousePos));
    }

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    private void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        Vec2i mousePos = new Vec2i(
                Mouse.getX() * scaledresolution.getScaledWidth() / mc.displayWidth,
                scaledresolution.getScaledHeight() - Mouse.getY() *
                        scaledresolution.getScaledHeight() / mc.displayHeight - 1);
        EventBus.getInstance().call(new EventRender2D(partialTicks, scaledresolution, mousePos));
        bloomFramebuffer = RenderUtil.createFramebuffer(bloomFramebuffer, true);
        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(true);
        EventBus.getInstance().call(new EventBlur());
        bloomFramebuffer.unbindFramebuffer();
    }

}
