package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class Reach extends Module {
    public static NumberValue<Double> MinReach = new NumberValue<>("Min", 3.5, 0.0, 6.0, 0.1);
    public static NumberValue<Double> MaxReach = new NumberValue<>("Max", 6.0, 0.1, 6.0, 0.1);
    private final BooleanValue<Boolean> RandomReach = new BooleanValue<>("Random Reach", true);
    private final BooleanValue<Boolean> weaponOnly = new BooleanValue<>("Weapon Only", false);
    private final BooleanValue<Boolean> movingOnly = new BooleanValue<>("Moving Only", false);
    private final BooleanValue<Boolean> sprintOnly = new BooleanValue<>("Sprint Only", false);
    private final BooleanValue<Boolean> hitThroughBlocks = new BooleanValue<>("HitThroughBlocks", false);

    public Reach() {
        super("Reach", "Make you can attack far targets", ModuleType.Combat);
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    @Native
    public static Object[] doReach(final double reachValue, final double AABB) {
        final Entity target = mc.getRenderViewEntity();
        Entity entity = null;
        if (target == null || mc.theWorld == null) {
            return null;
        }
        mc.mcProfiler.startSection("pick");
        final Vec3 targetEyes = target.getPositionEyes(0.0f);
        final Vec3 targetLook = target.getLook(0.0f);
        final Vec3 targetVector = targetEyes.addVector(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue);
        Vec3 targetVec = null;
        final List<Entity> targetHitbox = mc.theWorld.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox().addCoord(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue).expand(1.0, 1.0, 1.0));
        double reaching = reachValue;
        for (final Entity targetEntity : targetHitbox) {
            if (targetEntity.canBeCollidedWith()) {
                final float targetCollisionBorderSize = targetEntity.getCollisionBorderSize();
                AxisAlignedBB targetAABB = targetEntity.getEntityBoundingBox().expand(targetCollisionBorderSize, targetCollisionBorderSize, targetCollisionBorderSize);
                targetAABB = targetAABB.expand(AABB, AABB, AABB);
                final MovingObjectPosition tagetPosition = targetAABB.calculateIntercept(targetEyes, targetVector);
                if (targetAABB.isVecInside(targetEyes)) {
                    if (0.0 < reaching || reaching == 0.0) {
                        entity = targetEntity;
                        targetVec = ((tagetPosition == null) ? targetEyes : tagetPosition.hitVec);
                        reaching = 0.0;
                    }
                } else if (tagetPosition != null) {
                    final double targetHitVec = targetEyes.distanceTo(tagetPosition.hitVec);
                    if (targetHitVec < reaching || reaching == 0.0) {
                        if (targetEntity == target.ridingEntity) {
                            if (reaching == 0.0) {
                                entity = targetEntity;
                                targetVec = tagetPosition.hitVec;
                            }
                        } else {
                            entity = targetEntity;
                            targetVec = tagetPosition.hitVec;
                            reaching = targetHitVec;
                        }
                    }
                }
            }
        }
        if (reaching < reachValue && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }
        mc.mcProfiler.endSection();
        if (entity == null || targetVec == null) {
            return null;
        }
        return new Object[]{entity, targetVec};
    }


    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        setSuffix(String.format("Min:%s Max:%s", MinReach.getValue(), MaxReach.getValue()));
    }

    @Native
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onMove(final MouseEvent ev) {
        if (this.weaponOnly.getValue()) {
            if (mc.thePlayer.getCurrentEquippedItem() == null) {
                return;
            }
            if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                return;
            }
        }
        if (this.movingOnly.getValue() && mc.thePlayer.moveForward == 0.0 && mc.thePlayer.moveStrafing == 0.0) {
            return;
        }
        if (this.sprintOnly.getValue() && !mc.thePlayer.isSprinting()) {
            return;
        }
        if (!this.hitThroughBlocks.getValue() && mc.objectMouseOver != null) {
            final BlockPos blocksReach = mc.objectMouseOver.getBlockPos();
            if (blocksReach != null && mc.theWorld.getBlockState(blocksReach).getBlock() != Blocks.air) {
                return;
            }
        }
        double Reach;
        if (this.RandomReach.getValue()) {
            Reach = getRandomDoubleInRange(MinReach.getValue(), MaxReach.getValue()) + 0.1;
        } else {
            Reach = MinReach.getValue();
        }
        final Object[] reachs = doReach(Reach, 0.0);
        if (reachs == null) {
            return;
        }
        mc.objectMouseOver = new MovingObjectPosition((Entity) reachs[0], (Vec3) reachs[1]);
        mc.pointedEntity = (Entity) reachs[0];
    }

}

