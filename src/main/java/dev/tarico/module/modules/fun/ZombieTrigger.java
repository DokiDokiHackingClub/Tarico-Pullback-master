package dev.tarico.module.modules.fun;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.RotationUtil;
import dev.tarico.utils.client.WorldUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ZombieTrigger extends Module {
    public static BooleanValue<Boolean> headshot = new BooleanValue<>("Headshot", false);
    public static BooleanValue<Boolean> gunattack = new BooleanValue<>("GunAttack", false);
    public static NumberValue<Double> range = new NumberValue<>("Range", 100.0, 1.0, 500.0, 5.0);
    public static NumberValue<Double> deviation = new NumberValue<>("Deviation", 1.5, 0.0, 10.0, 0.1);

    public ZombieTrigger() {
        super("ZombieTrigger", "ZombieAimbot for hypixel minigame", ModuleType.Fun);
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

    @EventTarget
    @SuppressWarnings("unused")
    private void onUpdatePre(EventPreUpdate event) {
        Vec3 aimed;
        if (event.isCancelled() || !mc.gameSettings.keyBindUseItem.isPressed() && !gunattack.getValue()) {
            return;
        }

        final List<EntityLivingBase> targets = WorldUtil.getLivingEntities().stream().filter(this::isValid).sorted(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e))).collect(Collectors.toList());

        if (targets.size() <= 0)
            return;

        aimed = this.getFixedLocation(targets.get(0), deviation.getValue().floatValue(), headshot.getValue());

        final float[] rotations = RotationUtil.getVecRotation(aimed);

        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);

        if (gunattack.getValue()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            if (targets.get(0).getHealth() <= 0)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

            if (targets.isEmpty())
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

        }

    }

    @Override
    public void disable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }

    private Vec3 getFixedLocation(final EntityLivingBase entity, final float velocity, final boolean head) {
        double x = entity.posX + ((entity.posX - entity.lastTickPosX) * velocity);
        double y = entity.posY + ((entity.posY - entity.lastTickPosY) * (velocity * 0.3)) + (head ? entity.getEyeHeight() : 1.0);
        double z = entity.posZ + ((entity.posZ - entity.lastTickPosZ) * velocity);

        return new Vec3(x, y, z);
    }

    private boolean isValid(final EntityLivingBase entity) {
        if (!(entity instanceof EntityZombie))
            return false;

        if (entity.isDead || entity.getHealth() <= 0)
            return false;

        if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue())
            return false;

        return canEntityBeSeen(entity);
    }

}
