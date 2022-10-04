package dev.tarico.module.gui.hud.implement;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import dev.tarico.Client;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.gui.hud.util.AnimationUtils;
import dev.tarico.module.modules.combat.LegitAura;
import dev.tarico.module.modules.combat.TPAura;
import dev.tarico.module.modules.movement.Faker;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.math.MathUtil;
import dev.tarico.utils.math.MathUtils;
import dev.tarico.utils.render.ColorUtils;
import dev.tarico.utils.render.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class TargetHUD extends HUDObject {
    public static final Ordering<NetworkPlayerInfo> targets = Ordering
            .from(new PlayerComparator());
    public static ModeValue<Mode> modes = new ModeValue<>("Mode", Mode.values(), Mode.Old);
    public static BooleanValue<Boolean> twoHead = new BooleanValue<>("Two Helmet", false);
    public float animation = 0;
    private float health;
    EntityLivingBase target = null;


    public TargetHUD() {
        super(120, "TargetHUD");
        setHide(true);
    }

    @Override
    public void drawHUD(int x, int y, float p) {
        render(x, y);
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void render(float x, float y) {
        if (mc.currentScreen instanceof GuiChat) {
            target = mc.thePlayer;
        } else {
            if (ModuleManager.instance.getModule(TPAura.class).getState()) {
                target = TPAura.target;
            } else {
                target = LegitAura.target;
            }
        }

        if (target == null || !(target instanceof EntityPlayer))
            return;
        if (modes.getValue() == Mode.Old) {


            GL11.glPushMatrix();
            target.getName();

            GL11.glTranslatef(x, y, 0);

            if (!ModuleManager.instance.getModule(Faker.class).getState()) {
                String playerName2 = target.getName();
                String healthStr2 = "Health: " + Math.round(target.getHealth() * 10) / 10d;
                float namewidth = FontLoaders.F18.getStringWidth(playerName2) + 4;
                float healthwidth = FontLoaders.F16.getStringWidth(healthStr2) + 4;
                float width2 = Math.max(namewidth, healthwidth);


                RenderUtil.drawRect(0, 0, 26 + width2, 40, new Color(23, 23, 23).getRGB());
                RenderUtil.drawShadow(0, 0, 26 + width2, 40);
                FontLoaders.F18.drawString(playerName2, 26f, 2f, ColorUtils.WHITE.c);
                FontLoaders.F16.drawString(healthStr2, 26f, 14f, ColorUtils.WHITE.c);

                float health = target.getHealth();
                float maxHealth = target.getMaxHealth();
                float healthPercent = MathUtils.clampValue(health / maxHealth, 0, 1);
                float drawPercent = ((16.5f + width2 - 2) / 100) * (healthPercent * 100);

                if (this.animation <= 0) {
                    this.animation = drawPercent;
                }

                if (this.animation > 25.5f + width2 - 2) {
                    this.animation = drawPercent;
                }

                if (target.hurtTime <= 6) {
                    this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
                }

                RenderUtil.drawRect(10f, 27.5f, 25.5f + width2 - 2, 29.5f, new Color(23, 23, 23, 136).getRGB());
                this.setWidth((int) (25.5f + width2) + 1);

                if (drawPercent > 0) {
                    float f2 = Math.max((10f + drawPercent - 1), 10f);
                    //RenderUtil.drawRect(10f, 27.5f, f1, 29.5f, RenderUtil.reAlpha(0xfffeb647, 0.95f));
                    if (f2 + target.hurtTime < 25.5f + width2 - 2) {
                        //Anmi Fix
                        RenderUtil.drawRect(10f, 27.5f, f2 + target.hurtTime, 29.5f, RenderUtil.reAlpha(Colors.RED.getColor().getRGB(), 0.95f));
                    }
                    RenderUtil.drawRect(10f, 27.5f, f2, 29.5f, Client.instance.mainColor.getColor().getRGB());
                }

                FontLoaders.I10.drawString("s", 2.5f, 28, ColorUtils.WHITE.c);
                FontLoaders.I10.drawString("r", 2.5f, 35, ColorUtils.WHITE.c);

                float f3 = Math.max((10f + ((16.5f + width2 - 2) / 100) * (target.getTotalArmorValue() * 5) - 1), 10f);
                RenderUtil.drawRect(10f, 34.5f, 25.5f + width2 - 2, 36.5f, RenderUtil.reAlpha(ColorUtils.BLACK.c, 0.35f));
                RenderUtil.drawRect(10f, 34.5f, f3, 36.5f, new Color(0xff4286f5).getRGB());

                //rectangleBordered(1.5f, 1.5f, 24.5f, 24.5f, 0.5f, 0x00000000, ColorUtils.GREEN.c);
                GlStateManager.resetColor();
                for (NetworkPlayerInfo info : targets.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
                    if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == target) {
                        mc.getTextureManager().bindTexture(info.getLocationSkin());
                        drawScaledCustomSizeModalRect(2f, 2f, 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
                        GL11.glColor3f(target.hurtTime * 10, 1, 1);
                        GlStateManager.bindTexture(0);
                        break;
                    }
                }
            } else {
                String playerName2 = target.getName();
                String healthStr2 = "Health: " + Math.round(Faker.getFakeData(target).FakeHealth * 10) / 10d;
                float namewidth = FontLoaders.F18.getStringWidth(playerName2) + 4;
                float healthwidth = FontLoaders.F16.getStringWidth(healthStr2) + 4;
                float width2 = Math.max(namewidth, healthwidth);
                RenderUtil.drawRect(0, 0, 26 + width2, 40, new Color(23, 23, 23).getRGB());
                RenderUtil.drawShadow(0, 0, 26 + width2, 40);
                FontLoaders.F18.drawString(playerName2, 26f, 2f, ColorUtils.WHITE.c);
                FontLoaders.F16.drawString(healthStr2, 26f, 14f, ColorUtils.WHITE.c);

                float health = Faker.getFakeData(target).FakeHealth;
                float maxHealth = target.getMaxHealth();
                float healthPercent = MathUtils.clampValue(health / maxHealth, 0, 1);
                float drawPercent = ((16.5f + width2 - 2) / 100) * (healthPercent * 100);

                if (this.animation <= 0) {
                    this.animation = drawPercent;
                }

                if (this.animation > 25.5f + width2 - 2) {
                    this.animation = drawPercent;
                }

                if (Faker.getFakeData(target).hurtTime <= 6) {
                    this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
                }

                RenderUtil.drawRect(10f, 27.5f, 25.5f + width2 - 2, 29.5f, new Color(23, 23, 23, 136).getRGB());
                this.setWidth((int) (25.5f + width2) + 1);

                if (drawPercent > 0) {
                    float f2 = Math.max((10f + drawPercent - 1), 10f);
                    //RenderUtil.drawRect(10f, 27.5f, f1, 29.5f, RenderUtil.reAlpha(0xfffeb647, 0.95f));
                    if (f2 + Faker.getFakeData(target).hurtTime < 25.5f + width2 - 2) {
                        //Anmi Fix
                        RenderUtil.drawRect(10f, 27.5f, f2 + Faker.getFakeData(target).hurtTime, 29.5f, RenderUtil.reAlpha(Colors.RED.getColor().getRGB(), 0.95f));
                    }
                    RenderUtil.drawRect(10f, 27.5f, f2, 29.5f, Client.instance.mainColor.getColor().getRGB());
                }

                FontLoaders.I10.drawString("s", 2.5f, 28, ColorUtils.WHITE.c);
                FontLoaders.I10.drawString("r", 2.5f, 35, ColorUtils.WHITE.c);

                float f3 = Math.max((10f + ((16.5f + width2 - 2) / 100) * (target.getTotalArmorValue() * 5) - 1), 10f);
                RenderUtil.drawRect(10f, 34.5f, 25.5f + width2 - 2, 36.5f, RenderUtil.reAlpha(ColorUtils.BLACK.c, 0.35f));
                RenderUtil.drawRect(10f, 34.5f, f3, 36.5f, new Color(0xff4286f5).getRGB());

                //rectangleBordered(1.5f, 1.5f, 24.5f, 24.5f, 0.5f, 0x00000000, ColorUtils.GREEN.c);
                GlStateManager.resetColor();
                for (NetworkPlayerInfo info : targets.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
                    if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == target) {
                        mc.getTextureManager().bindTexture(info.getLocationSkin());
                        drawScaledCustomSizeModalRect(2f, 2f, 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
                        GL11.glColor3f(Faker.getFakeData(target).hurtTime * 10, 1, 1);
                        GlStateManager.bindTexture(0);
                        break;
                    }
                }
            }

            GL11.glPopMatrix();
        } else if (modes.getValue() == Mode.Astolfo) {
            GL11.glPushMatrix();
            Color color;
            float width2 = Math.max(75, mc.fontRendererObj.getStringWidth(target.getName()) + 20);
            String healthStr2 = Math.round(target.getHealth() * 10) / 10d + " ❤";
            GL11.glTranslatef(x, y, 0);
            RenderUtil.drawRect(0, 0, 55 + width2, 47, new Color(0, 0, 0, 55).getRGB());
            setWidth((int) (55 + width2));

            mc.fontRendererObj.drawStringWithShadow(target.getName(), 35, 3f, ColorUtils.WHITE.c);

            boolean isNaN = Float.isNaN(target.getHealth());
            float health = isNaN ? 20 : target.getHealth();
            float maxHealth = isNaN ? 20 : target.getMaxHealth();
            float healthPercent = MathUtils.clampValue(health / maxHealth, 0, 1);

            int hue = (int) (healthPercent * 120);
            color = Color.getHSBColor(hue / 360f, 0.7f, 1f);

            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0, 2.0, 2.0);
            mc.fontRendererObj.drawStringWithShadow(healthStr2, 18, 7.5f, color.getRGB());
            GlStateManager.popMatrix();

            RenderUtil.drawRect(36, 36.5f, 45 + width2, 44.5f, RenderUtil.reAlpha(color.darker().darker().getRGB(), 0.35f));

            float barWidth = (43 + width2 - 2) - 37;
            float drawPercent = 43 + (barWidth / 100) * (healthPercent * 100);

/*            RenderUtil.drawRect(36, 36.5f, this.animation + 6, 44.5f, color.darker().darker().getRGB());
            RenderUtil.drawRect(36, 36.5f, this.animation, 44.5f, color.getRGB());*/
            if (!(drawPercent + target.hurtTime > (int) (55 + width2)))
                RenderUtil.drawRect(36, 36.5f, drawPercent + target.hurtTime, 44.5f, color.getRGB());
            RenderUtil.drawRect(36, 36.5f, drawPercent, 44.5f, color.getRGB());

            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();

            GlStateManager.resetColor();
            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1, 1);
            GuiInventory.drawEntityOnScreen(17, 46, (int) (42 / target.height), 0, 0, target);
            GL11.glPopMatrix();
        } else if (modes.getValue() == Mode.Exhibition) {
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }
            GlStateManager.pushMatrix();
            // Width and height
            final float width = x + 680;
            final float height = y + 280;
            GlStateManager.translate(width - 680, height - 274, 0.0f);
            setWidth(125);
            // Draws the skeet rectangles.
            RenderUtil.skeetRect(0, -2.0, mc.fontRendererObj.getStringWidth(target.getName()) > 70.0f ? (double) (124.0f + mc.fontRendererObj.getStringWidth(((EntityPlayer) target).getName()) - 70.0f) : 124.0, 38.0, 1.0);
            RenderUtil.skeetRectSmall(0.0f, -2.0f, 124.0f, 38.0f, 1.0);
            // Draws name.
            mc.fontRendererObj.drawStringWithShadow(((EntityPlayer) target).getName(), 42.3f, 0.3f, -1);
            // Gets health.
            final float health = ((EntityPlayer) target).getHealth();
            // Gets health and absorption
            final float healthWithAbsorption = ((EntityPlayer) target).getHealth() + ((EntityPlayer) target).getAbsorptionAmount();
            // Color stuff for the healthBar.
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            // Max health.
            final float progress = health / ((EntityPlayer) target).getMaxHealth();
            // Color.
            final Color healthColor = health >= 0.0f ? ColorUtils.blendColors(fractions, colors, progress).brighter() : Color.RED;
            // Round.
            double cockWidth = 0.0;
            cockWidth = MathUtil.round(cockWidth, (int) 5.0);
            if (cockWidth < 50.0) {
                cockWidth = 50.0;
            }
            // Healthbar + absorption
            final double healthBarPos = cockWidth * (double) progress;
            RenderUtil.rectangle(42.5, 10.3, 103, 13.5, healthColor.darker().darker().darker().darker().getRGB());
            RenderUtil.rectangle(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
            if (((EntityPlayer) target).getAbsorptionAmount() > 0.0f) {
                RenderUtil.rectangle(97.5 - (double) ((EntityPlayer) target).getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
            }
            // Draws rect around health bar.
            RenderUtil.rectangleBordered(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.getRGB());
            // Draws the lines between the healthbar to make it look like boxes.
            for (int dist = 1; dist < 10; ++dist) {
                final double cock = cockWidth / 8.5 * (double) dist;
                RenderUtil.rectangle(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
            }
            // Draw targets hp number and distance number.
            GlStateManager.scale(0.5, 0.5, 0.5);
            final int distance = (int) mc.thePlayer.getDistanceToEntity(target);
            final String nice = "HP: " + (int) healthWithAbsorption + " | Dist: " + distance;
            mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
            GlStateManager.scale(2.0, 2.0, 2.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            // Draw targets armor and tools and weapons and shows the enchants.
            if (target != null) drawEquippedShit(28, 20);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            // Draws targets model.
            GlStateManager.scale(0.31, 0.31, 0.31);
            GlStateManager.translate(73.0f, 102.0f, 40.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtil.drawModel(target.rotationYaw, target.rotationPitch, (EntityLivingBase) target);
            GlStateManager.popMatrix();
        } else if (modes.getValue() == Mode.Remix) {
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }
            GlStateManager.pushMatrix();
            // Width and height
            final float width = x + 678;
            final float height = y + 280;
            GlStateManager.translate(width - 680, height - 274, 0.0f);
            setWidth(154);
            // Border rect.
            RenderUtil.rectangle(2, -6, 156.0, 47.0, new Color(25, 25, 25).getRGB());
            // Main rect.
            RenderUtil.rectangle(4, -4, 154.0, 45.0, new Color(45, 45, 45).getRGB());
            // Draws name.
            mc.fontRendererObj.drawStringWithShadow(((EntityPlayer) target).getName(), 46f, 0.3f, -1);
            // Gets health.
            final float health = ((EntityPlayer) target).getHealth();
            // Color stuff for the healthBar.
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            // Max health.
            final float progress = health / ((EntityPlayer) target).getMaxHealth();
            // Color.
            final Color healthColor = health >= 0.0f ? ColorUtils.blendColors(fractions, colors, progress).brighter() : Color.RED;
            // $$ draws the 4 fucking boxes killing my self btw. $$
            RenderUtil.rect(45, 11, 20, 20, new Color(25, 25, 25));
            RenderUtil.rect(46, 12, 18, 18, new Color(95, 95, 95));
            RenderUtil.rect(67, 11, 20, 20, new Color(25, 25, 25));
            RenderUtil.rect(68, 12, 18, 18, new Color(95, 95, 95));
            RenderUtil.rect(89, 11, 20, 20, new Color(25, 25, 25));
            RenderUtil.rect(90, 12, 18, 18, new Color(95, 95, 95));
            RenderUtil.rect(111, 11, 20, 20, new Color(25, 25, 25));
            RenderUtil.rect(112, 12, 18, 18, new Color(95, 95, 95));
            // Draws the current ping/ms.
            NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
            final String ping = (Objects.isNull(networkPlayerInfo) ? "0ms" : networkPlayerInfo.getResponseTime() + "ms");
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.6, 0.6, 0.6);
            FontLoaders.F16.drawCenteredStringWithShadow(ping, 240, 40, Color.WHITE.getRGB());
            GlStateManager.popMatrix();
            // Draws the ping thingy from tab. :sunglasses:
            if (target != null && networkPlayerInfo != null) {
                drawPing(103, 50, 14, networkPlayerInfo);
            }
            // Round.
            double cockWidth = 0.0;
            cockWidth = MathUtil.round(cockWidth, (int) 5.0);
            if (cockWidth < 50.0) {
                cockWidth = 50.0;
            }
            // Bar behind healthbar.
            RenderUtil.rectangle(6.5, 37.3, 151, 43, Color.RED.darker().darker().getRGB());
            final double healthBarPos = cockWidth * (double) progress;
            // health bar.
            RenderUtil.rect(6f, 37.3f, (healthBarPos * 2.9), 6f, healthColor);
            // Gets the armor thingy for the bar.
            float armorValue = ((EntityPlayer) target).getTotalArmorValue();
            double armorWidth = armorValue / 20D;
            // Bar behind armor bar.
            RenderUtil.rect(45.5f, 32.3f, 105, 2.5f, new Color(0, 0, 255));
            // Armor bar.
            RenderUtil.rect(45.5f, 32.3f, (105 * armorWidth), 2.5f, new Color(0, 45, 255));
            // White rect around head.
            RenderUtil.rect(6, -2, 37, 37, new Color(205, 205, 205));
            // Draws head.
            renderPlayerModelTexture(7, -1, 3, 3, 3, 3, 35, 35, 24, 24, (AbstractClientPlayer) target);
            // Draws armor.
            GlStateManager.scale(1.1, 1.1, 1.1);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            // Draw targets armor the worst way possible.
            if (twoHead.getValue()) {
                if (target != null) drawHelmet(24, 11);
                drawHelmet(44, 11);
                drawLegs(64, 11);
                drawBoots(84, 11);
            } else {
                if (target != null) drawHelmet(24, 11);
                drawChest(44, 11);
                drawLegs(64, 11);
                drawBoots(84, 11);
            }
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } else if (modes.getValue() == Mode.ZeroDay) {
            ScaledResolution sr = new ScaledResolution(mc);
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }

            final EntityPlayer enti = (EntityPlayer) target;
            final double distan = mc.thePlayer.getDistanceToEntity(target);

            this.health = (float) MathUtil.lerp(this.health, enti.getHealth(), 0.1);
            final String distance = String.valueOf(distan).split("\\.")[0] + "." + String.valueOf(distan).split("\\.")[1].charAt(0);

            RenderUtil.color(new Color(255, 255, 255, 255).getRGB());

            if (target instanceof EntityLivingBase)
                GuiInventory.drawEntityOnScreen((int) (x) + 4 + 16, (int) (y) + 4 + 65, 32, 0, 0, (EntityLivingBase) target);

            RenderUtil.drawRect(x, y, x + 160, y + 80, 0xcc000000);
            setWidth(160);

            for (int i = 0; i < (this.health / ((EntityPlayer) target).getMaxHealth()) * 160; i++) {
                RenderUtil.rect(x + i, y + 78.5, 1, 1.5, new Color(ColorUtils.getStaticColor(i / 8f, 0.7f, 1)));
            }

            FontLoaders.F18.drawString(((EntityPlayer) target).getName(), (x) + 4 + 35, y + 6, Color.WHITE.getRGB());

            if (enti.getHealth() / enti.getMaxHealth() <= mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth())
                FontLoaders.F18.drawString("Winning", (x) + 4 + 35, y + 45 + FontLoaders.F18.getHeight(), Color.WHITE.getRGB());
            else
                FontLoaders.F18.drawString("Losing", (x) + 4 + 35, y + 45 + FontLoaders.F18.getHeight(), Color.WHITE.getRGB());

            FontLoaders.F18.drawString("Dist: " + distance, (float) ((x) + 4 + 35.5), y + FontLoaders.F18.getHeight() + 6, Color.WHITE.getRGB());
            FontLoaders.F18.drawString("Hurt Resistant Time: " + enti.hurtResistantTime, (float) ((x) + 4 + 35.5), y + FontLoaders.F18.getHeight() + 6 + FontLoaders.F18.getHeight() + 1, Color.WHITE.getRGB());
        }
    }

    private void drawEquippedShit(final int x, final int y) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            final ItemStack armor = ((EntityPlayer) target).getCurrentArmor(geraltOfNigeria);
            if (armor != null) {
                stuff.add(armor);
            }
        }
        if (((EntityPlayer) target).getHeldItem() != null) {
            stuff.add(((EntityPlayer) target).getHeldItem());
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x, y);
            /*            RenderUtil.renderEnchantText(yes, cock + x, (y + 0.5f));*/
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    public void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        GL11.glColor4f(1, 1, 1, 1);
        float hurtPercent;
        if (!ModuleManager.instance.getModule(Faker.class).getState())
            hurtPercent = target.hurtTime;
        else
            hurtPercent = Faker.getFakeData(target).hurtTime;
        float scale;
        if (hurtPercent == 0f) {
            scale = 1.0f;
        } else if (hurtPercent < 0.5f) {
            scale = 1.0f - (0.2f * hurtPercent * 1.0f);
        } else {
            scale = 0.8f + (0.2f * (hurtPercent - 0.5f) * 0.1f);
        }
        int size = 26;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f);
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + uWidth) * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + uWidth) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        int i = 0;
        int j = 0;

        if (networkPlayerInfoIn.getResponseTime() < 0) {
            j = 5;
        } else if (networkPlayerInfoIn.getResponseTime() < 150) {
            j = 0;
        } else if (networkPlayerInfoIn.getResponseTime() < 300) {
            j = 1;
        } else if (networkPlayerInfoIn.getResponseTime() < 600) {
            j = 2;
        } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
            j = 3;
        } else {
            j = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0 + i * 10, 176 + j * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawHelmet(final int x, final int y) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack helmet = ((EntityPlayer) target).getCurrentArmor(3);
        if (helmet != null) {
            stuff.add(helmet);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR,
                            p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR)
                    .compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "",
                            scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "")
                    .compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }

    private void drawChest(final int x, final int y) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack chest = ((EntityPlayer) target).getCurrentArmor(2);
        if (chest != null) {
            stuff.add(chest);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void drawLegs(final int x, final int y) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack legs = ((EntityPlayer) target).getCurrentArmor(1);
        if (legs != null) {
            stuff.add(legs);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void drawBoots(final int x, final int y) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack boots = ((EntityPlayer) target).getCurrentArmor(0);
        if (boots != null) {
            stuff.add(boots);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    enum Mode {
        Astolfo,
        Old,
        Exhibition,
        Remix,
        ZeroDay
    }
}
