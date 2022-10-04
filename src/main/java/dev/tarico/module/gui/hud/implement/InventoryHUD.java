package dev.tarico.module.gui.hud.implement;

import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class InventoryHUD extends HUDObject {

    public InventoryHUD() {
        super(182, "Inventory");
    }

    @Override
    public void drawHUD(int x, int y, float p) {
        RenderUtil.drawShadow(x, y - 10, (20 * 9) + 2, (20 * 3) + 2 + 10);
        RenderUtil.drawBlurRect(x, y, (20 * 9) + 2, (20 * 3) + 2, new Color(0, 0, 0, HUD.hudalpha.getValue().intValue()).getRGB());

        int startX = x + 2;
        int startY = y + 3;
        int curIndex = 0;
        for (int i = 9; i < 36; ++i) {
            ItemStack slot = mc.thePlayer.inventory.mainInventory[i];
            if (slot == null) {
                startX += 20;
                curIndex += 1;

                if (curIndex > 8) {
                    curIndex = 0;
                    startY += 20;
                    startX = x + 2;
                }

                continue;
            }
            drawItemStack(slot, startX, startY);
            startX += 20;
            curIndex += 1;
            if (curIndex > 8) {
                curIndex = 0;
                startY += 20;
                startX = x + 2;
            }
        }
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, null);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.enableAlpha();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
