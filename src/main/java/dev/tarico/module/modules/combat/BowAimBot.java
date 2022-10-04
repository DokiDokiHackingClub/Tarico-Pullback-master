package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.management.FriendManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;

public class BowAimBot extends Module {
    public static ArrayList<Entity> attackList = new ArrayList<>();
    public static ArrayList<Entity> targets = new ArrayList<>();
    public static int currentTarget;
    private final BooleanValue<Boolean> silent = new BooleanValue<>("Silent", true);

    public BowAimBot() {
        super("BowAimBot", "Bow Auto Aim", ModuleType.Combat);
    }

    public boolean isValidTarget(Entity entity) {
        boolean valid = false;
        if (entity == mc.thePlayer.ridingEntity) {
            return false;
        }
        if (AntiBot.isServerBot(entity))
            return false;
        if (entity.isInvisible()) {
            valid = true;
        }
        if (FriendManager.isFriend(entity.getName()) && entity instanceof EntityPlayer || !mc.thePlayer.canEntityBeSeen(entity)) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            valid = mc.thePlayer.getDistanceToEntity(entity) <= 50.0f && entity != mc.thePlayer && entity.isEntityAlive() && !FriendManager.isFriend(entity.getName());
        }
        return valid;
    }

    @Native
    @EventTarget
    public void onPre(EventPreUpdate pre) {
        Entity e;
        for (Entity o : mc.theWorld.loadedEntityList) {
            e = o;
            if (e instanceof EntityPlayer && !targets.contains(e)) {
                targets.add(e);
            }
            if (!targets.contains(e) || !(e instanceof EntityPlayer)) continue;
            targets.remove(e);
        }
        if (currentTarget >= attackList.size()) {
            currentTarget = 0;
        }
        for (Entity o : mc.theWorld.loadedEntityList) {
            e = o;
            if (this.isValidTarget(e) && !attackList.contains(e)) {
                attackList.add(e);
            }
            if (this.isValidTarget(e) || !attackList.contains(e)) continue;
            attackList.remove(e);
        }
        this.sortTargets();
        if (mc.thePlayer != null && attackList.size() != 0 && attackList.get(currentTarget) != null && this.isValidTarget(attackList.get(currentTarget)) && mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem().getItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            int bowCurrentCharge = mc.thePlayer.getItemInUseDuration();
            float bowVelocity = (float) bowCurrentCharge / 20.0f;
            bowVelocity = (bowVelocity * bowVelocity + bowVelocity * 2.0f) / 3.0f;
            bowVelocity = MathHelper.clamp_float(bowVelocity, 0.0f, 1.0f);
            double v = bowVelocity * 3.0f;
            double g = 0.05000000074505806;
            if ((double) bowVelocity < 0.1) {
                return;
            }
            if (bowVelocity > 1.0f) {
                bowVelocity = 1.0f;
            }
            double xDistance = BowAimBot.attackList.get(BowAimBot.currentTarget).posX - mc.thePlayer.posX + (BowAimBot.attackList.get(BowAimBot.currentTarget).posX - BowAimBot.attackList.get(BowAimBot.currentTarget).lastTickPosX) * (double) (bowVelocity * 10.0f);
            double zDistance = BowAimBot.attackList.get(BowAimBot.currentTarget).posZ - mc.thePlayer.posZ + (BowAimBot.attackList.get(BowAimBot.currentTarget).posZ - BowAimBot.attackList.get(BowAimBot.currentTarget).lastTickPosZ) * (double) (bowVelocity * 10.0f);
            float trajectoryTheta90 = (float) (Math.atan2(zDistance, xDistance) * 180.0 / 3.141592653589793) - 90.0f;
            float bowTrajectory = (float) ((double) ((float) (-Math.toDegrees(this.getLaunchAngle((EntityLivingBase) attackList.get(currentTarget), v, g)))) - 3.8);
            if (trajectoryTheta90 <= 360.0f && bowTrajectory <= 360.0f) {
                if (!this.silent.getValue()) {
                    mc.thePlayer.rotationYaw = trajectoryTheta90;
                    mc.thePlayer.rotationPitch = bowTrajectory;
                } else {
                    pre.setYaw(trajectoryTheta90);
                    pre.setPitch(bowTrajectory);
                }
            }
        }
    }

    public void sortTargets() {
        attackList.sort((ent1, ent2) -> {
            double d2;
            double d1 = mc.thePlayer.getDistanceToEntity(ent1);
            return d1 < (d2 = mc.thePlayer.getDistanceToEntity(ent2)) ? -1 : (d1 == d2 ? 0 : 1);
        });
    }

    @Override
    public void disable() {
        targets.clear();
        attackList.clear();
        currentTarget = 0;
    }

    @Native
    private float getLaunchAngle(EntityLivingBase targetEntity, double v, double g) {
        double yDif = targetEntity.posY + (double) (targetEntity.getEyeHeight() / 2.0f) - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double xDif = targetEntity.posX - mc.thePlayer.posX;
        double zDif = targetEntity.posZ - mc.thePlayer.posZ;
        double xCoord = Math.sqrt(xDif * xDif + zDif * zDif);
        return this.theta(v + 2.0, g, xCoord, yDif);
    }

    @Native
    private float theta(double v, double g, double x, double y) {
        double yv = 2.0 * y * (v * v);
        double gx = g * (x * x);
        double g2 = g * (gx + yv);
        double insqrt = v * v * v * v - g2;
        double sqrt = Math.sqrt(insqrt);
        double numerator = v * v + sqrt;
        double numerator2 = v * v - sqrt;
        double atan1 = Math.atan2(numerator, g * x);
        double atan2 = Math.atan2(numerator2, g * x);
        return (float) Math.min(atan1, atan2);
    }
}

