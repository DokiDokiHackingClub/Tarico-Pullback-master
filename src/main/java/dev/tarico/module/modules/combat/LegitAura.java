package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.injection.mixins.IAccessorMinecraft;
import dev.tarico.injection.mixins.IRenderManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.fun.FarmHunt;
import dev.tarico.module.modules.movement.Scaffold;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.module.modules.utils.BedFucker;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.math.MathUtils;
import dev.tarico.utils.render.RenderHelper;
import dev.tarico.utils.timer.DelayTimer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LegitAura extends Module {
    private static final BooleanValue<Boolean> autoBlock = new BooleanValue<>("AutoBlock", true);
    public static float[] facing;
    public static EntityLivingBase target;
    public static boolean blocking;
    public static boolean attack;
    public static ModeValue<Enum<ESPMode>> mode = new ModeValue<>("ESP", ESPMode.values(), ESPMode.Sigma);
    public BooleanValue<Boolean> twall = new BooleanValue<>("Through Wall", false);
    public static NumberValue<Double> fov = new NumberValue<>("FOV", 90.0, 15.0, 360.0, 1.0);
    private final NumberValue<Double> aps = new NumberValue<>("CPS", 10.0, 1.0, 20.0, 0.5);
    private final NumberValue<Double> reach = new NumberValue<>("Reach", 4.5, 1.0, 6.0, 0.1);
    private final NumberValue<Double> crack = new NumberValue<>("CrackSize", 1.0D, 0.0D, 5.0D, 1.0D);
    private final BooleanValue<Boolean> hurtTime = new BooleanValue<>("HurtTime", false);
    private final BooleanValue<Boolean> esp = new BooleanValue<>("DrawESP", true);
    public static final BooleanValue<Boolean> players = new BooleanValue<>("Players", true);
    public static final BooleanValue<Boolean> animals = new BooleanValue<>("Animals", true);
    public static final BooleanValue<Boolean> mobs = new BooleanValue<>("Mobs", true);
    public static final BooleanValue<Boolean> invis = new BooleanValue<>("Invisibles", false);
    public List<EntityLivingBase> targets = new ArrayList<>();
    public DelayTimer timer = new DelayTimer();

    public LegitAura() {
        super("LegitAura", "Auto Attack Entity", ModuleType.Combat);
        this.inVape = true;
        this.vapeName = "Killaura";
        this.setVSuffix("Switch");
    }

    @Native
    public static boolean isFovInRange(final Entity entity, float fov) {
        fov *= 0.5;
        final double v = ((mc.thePlayer.rotationYaw - getRotion(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
        return (v > 0.0 && v < fov) || (-fov < v && v < 0.0);
    }

    @Native
    public static float getRotion(final Entity ent) {
        final double x = ent.posX - mc.thePlayer.posX;
        final double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        yaw = -yaw;
        return (float) yaw;
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    private void onPre(EventPreUpdate e) {
        setSuffix("Normal");
        if (ModuleManager.instance.getModule(Scaffold.class).getState() || ModuleManager.instance.getModule(BedFucker.class).getState() || mc.thePlayer.isDead || mc.thePlayer.isSpectator())
            return;
        if (targets.isEmpty())
            return;
        target = targets.get(0);
        facing = getRotationsToEnt(target);
        facing[0] += MathUtils.getRandomInRange(1, 5);
        facing[1] += MathUtils.getRandomInRange(1, 5);
        facing[0] = facing[0] + MathUtils.getRandomFloat(1.98f, -1.98f);

        e.setYaw(facing[0]);
        e.setPitch(facing[1]);

        mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = facing[0];
        attack = true;
        if (timer.delay(aps.getValue().intValue())) {
            if (hurtTime.getValue() && target.hurtTime > 0)
                return;
            if (target.getHealth() > 0) {
                mc.thePlayer.swingItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }

    @SubscribeEvent
    @Native
    @SuppressWarnings("unused")
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (target != null && esp.getValue()) {
            if (mode.getValue() == ESPMode.Sigma) {
                drawCircle(target);
            } else if (mode.getValue() == ESPMode.Box) {
                if (HUD.mode.getValue() == HUD.HUDModes.Vape) {
                    RenderHelper.drawESP(target, new Color(0xFF0E0E).getRGB(), true, 3);
                } else {
                    RenderHelper.drawESP(target, Client.instance.mainColor.getColor().getRGB(), true, 3);
                }
            }
        }
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    private void onPost(EventPostUpdate e) {
        sortTargets();
        if (ModuleManager.instance.getModule(Scaffold.class).getState() || ModuleManager.instance.getModule(BedFucker.class).getState() || mc.thePlayer.isDead || mc.thePlayer.isSpectator())
            return;
        int crackSize = this.crack.getValue().intValue();
        if (!targets.isEmpty()) {
            if (autoBlock.getValue() && mc.thePlayer.getItemInUse() == null) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (target != null) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                            mc.getItemRenderer().resetEquippedProgress2();
                        }
                        blocking = true;
                    } else {
                        blocking = false;
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    }
                }
            }
            int i2 = 0;
            while (i2 < crackSize && target != null) {
                mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                i2++;
            }
        }
        if (targets.isEmpty()) {
            if (blocking) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
            attack = false;
            blocking = false;
            target = null;
        }
    }

    @Override
    public void disable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        mc.playerController.onStoppedUsingItem(mc.thePlayer);
        targets.clear();
        target = null;
        blocking = false;
        attack = false;
    }

    public void sortTargets() {
        targets.clear();
        if (FarmHunt.legitAura.getValue() && ModuleManager.instance.getModule(FarmHunt.class).getState()) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if ((entity instanceof EntityLivingBase) && !(entity instanceof EntityPlayer) && !entity.isInvisible()) {
                    EntityLivingBase target = (EntityLivingBase) entity;
                    if ((target.isSprinting() || target.hurtTime > 0 || (target.rotationYaw != 0 && target.rotationPitch != 0)) && !targets.contains(target) && mc.thePlayer.getDistanceToEntity(entity) < reach.getValue()) {
                        targets.add(target);
                    }
                }
            }
        } else {
            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entLiving = (EntityLivingBase) entity;
                    if (mc.thePlayer.getDistanceToEntity(entLiving) < reach.getValue() && entLiving != mc.thePlayer && !entLiving.isDead && isValid(entLiving)) {
                        targets.add(entLiving);
                    }
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }

    public boolean isValid(EntityLivingBase ent) {
        if (AntiBot.isServerBot(ent))
            return false;
        if (Teams.isOnSameTeam(ent))
            return false;
        if (ent instanceof EntityPlayer && !players.getValue())
            return false;
        if (ent instanceof EntityMob && !mobs.getValue())
            return false;
        if (ent instanceof EntityAnimal && !animals.getValue())
            return false;
        if (ent.isInvisible() && !invis.getValue())
            return false;
        if (!isFovInRange(ent, fov.getValue().floatValue())) {
            return false;
        }
        if(!canEntityBeSeen(ent) && !twall.getValue())
            return false;
        if (ent.getHealth() <= 0)
            return false;
        if (ent.isDead) {
            target = null;
            return false;
        }
        return true;
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;

        vec2 = new Vec3(maxx, maxy, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, maxy, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        return see;
    }

    private float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.thePlayer.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.thePlayer.posY + mc.thePlayer.height) - 0.5;
        final double differenceZ = ent.posZ - mc.thePlayer.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.thePlayer.getDistanceToEntity(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.thePlayer.rotationYaw
                + MathHelper.wrapAngleTo180_float(rotationYaw - mc.thePlayer.rotationYaw);
        final float finishedPitch = mc.thePlayer.rotationPitch
                + MathHelper.wrapAngleTo180_float(rotationPitch - mc.thePlayer.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90, 90)};
    }

    private void drawCircle(final Entity entity) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosY()) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + 0.66 * Math.cos(i);
            final double vecZ = z + 0.66 * Math.sin(i);

            c = HUD.mode.getValue() == HUD.HUDModes.Sigma ? new Color(-1) : Client.instance.mainColor.getColor();

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0
            );
            GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.85F
            );
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }

    enum ESPMode {
        Box,
        Sigma
    }
}