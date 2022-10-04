package dev.tarico.module.gui.hud.implement;

import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class RadarHUD extends HUDObject {
    public static NumberValue<Double> scale = new NumberValue<>("Scale", 128.0, 30.0, 300.0, 1.0);
    public static int SIZE = 64;

    public RadarHUD() {
        super(SIZE, "Radar");
    }

    @Override
    public void drawHUD(int x, int y, float partialTicks) {
        SIZE = scale.getValue().intValue();
        int BGCOLOR = new Color(0, 0, 0, HUD.hudalpha.getValue().intValue()).getRGB();
        this.setWidth(SIZE);

        RenderUtil.drawShadow(x, y - 10, SIZE, SIZE + 10);

        RenderUtil.drawBlurRect(x, y, SIZE, SIZE, BGCOLOR);
        RenderUtil.drawRect((float) x + ((SIZE / 2f) - 0.5f), (float) y + 3.5f, (float) x + (SIZE / 2f) + 0.5f, ((float) y + SIZE) - 3.5f, new Color(255, 255, 255, 80).getRGB());
        RenderUtil.drawRect((float) x + 3.5f, (float) y + ((SIZE / 2f) - 0.5f), ((float) x + SIZE) - 3.5f, (float) y + (SIZE / 2) + 0.5f, new Color(255, 255, 255, 80).getRGB());

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        Entity player = mc.thePlayer;
        GL11.glPushMatrix();
        GL11.glTranslated((float) x, (float) y, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glScissor((int) ((float) x * sr.getScaleFactor()), (int) (mc.displayHeight - ((float) y + SIZE) * sr.getScaleFactor()), SIZE * sr.getScaleFactor(), SIZE * sr.getScaleFactor());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glTranslated(SIZE / (double) 2, SIZE / (double) 2, 0);
        GL11.glRotated(player.prevRotationYaw + (0.0f) * partialTicks, 0, 0, -1);
        GL11.glPointSize(4 * sr.getScaleFactor());
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glVertex2d(0, 0);
        GL11.glColor4f(1, 0, 0, 1);
        Entity[] entities = getAllMatchedEntity();
        for (Entity entity : entities) {
            double dx = (player.prevPosX + (player.posX - player.prevPosX) * partialTicks) - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks),
                    dz = (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks) - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
            GL11.glVertex2d(dx, dz);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
    }

    private Entity[] getAllMatchedEntity() {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        Entity player = mc.thePlayer;
        if (world != null) {
            ArrayList<Entity> entities = new ArrayList<>(world.loadedEntityList.size());
            double max = (SIZE * SIZE) * 2;
            for (Entity entity : world.loadedEntityList) {
                if (entity == player)
                    continue;
                double dx = player.posX - entity.posX,
                        dz = player.posZ - entity.posZ;
                double distance = dx * dx + dz * dz;
                if (distance > max)
                    continue;
                if (!(entity instanceof EntityPlayer))
                    continue;
                if (entity.isInvisible())
                    continue;
                entities.add(entity);
            }
            return entities.toArray(new Entity[0]);
        }
        return new Entity[0];
    }
}
