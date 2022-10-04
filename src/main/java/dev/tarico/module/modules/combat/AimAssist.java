package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module {
    public static NumberValue<Double> speed = new NumberValue<>("Speed", 45.0, 1.0, 100.0, 1.0);
    public static NumberValue<Double> fov = new NumberValue<>("FOV", 90.0, 15.0, 180.0, 1.0);
    public static NumberValue<Double> range = new NumberValue<>("Range", 4.5, 3.0, 6.0, 0.1);
    public static BooleanValue<Boolean> clickAim = new BooleanValue<>("Click", true);
    public static BooleanValue<Boolean> weaponOnly = new BooleanValue<>("Weapon only", false);
    public static BooleanValue<Boolean> locky = new BooleanValue<>("Blatant Lock", false);

    public AimAssist() {
        super("AimAssist", "Help your aim targets", ModuleType.Combat);
        this.inVape = true;
    }

    @Native
    public static double getRot(final Entity en) {
        return ((mc.thePlayer.rotationYaw - getRotion(en)) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    @Native
    public static void getFoc(final Entity s, final boolean packet) {
        if (s != null) {
            final float[] t = getArray(s);
            if (t != null) {
                final float y = t[0];
                final float p = t[1] + 4.0f;
                if (!packet) {
                    mc.thePlayer.rotationYaw = y;
                    mc.thePlayer.rotationPitch = p;
                } else {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(y, p, mc.thePlayer.onGround));
                }
            }
        }
    }

    @Native
    public static float[] getArray(final Entity q) {
        if (q == null) {
            return null;
        }
        final double diffX = q.posX - mc.thePlayer.posX;
        double diffY;
        if (q instanceof EntityLivingBase) {
            final EntityLivingBase EntityLivingBase = (EntityLivingBase) q;
            diffY = EntityLivingBase.posY + EntityLivingBase.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (q.getEntityBoundingBox().minY + q.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        final double diffZ = q.posZ - mc.thePlayer.posZ;
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }

    @Native
    public static float getRotion(final Entity ent) {
        final double x = ent.posX - mc.thePlayer.posX;
        final double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        yaw = -yaw;
        return (float) yaw;
    }

    public static boolean isTarget(final Entity entity, float b) {
        b *= 0.5;
        final double v = ((mc.thePlayer.rotationYaw - getRotion(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
        return (v > 0.0 && v < b) || (-b < v && v < 0.0);
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void update(EventTick event) {
        setSuffix(String.format("Speed:%s Fov:%s", speed.getValue(), fov.getValue()));
        if (state) {
            if (weaponOnly.getValue()) {
                if (mc.thePlayer.getCurrentEquippedItem() == null) {
                    return;
                }
                if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                    return;
                }
            }
            if (clickAim.getValue() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
                return;
            }
            final Entity h = this.getTarget();
            if (!locky.getValue()) {
                if (h != null && (getRot(h) > 1.0 || getRot(h) < -1.0)) {
                    final boolean i = getRot(h) > 0.0;
                    final EntityPlayerSP thePlayer = mc.thePlayer;
                    thePlayer.rotationYaw += (float) (i ? (-(Math.abs(getRot(h)) / (101.0 - speed.getValue()))) : (Math.abs(getRot(h)) / (101.0 - speed.getValue())));
                }
            } else {
                getFoc(h, false);
            }
        }
    }

    public Entity getTarget() {
        Entity k = null;
        int f = fov.getValue().intValue();
        for (final Entity ent : mc.theWorld.loadedEntityList) {
            if (ent.isEntityAlive() && ent != mc.thePlayer && mc.thePlayer.getDistanceToEntity(ent) <= range.getValue() && ent instanceof EntityLivingBase) {
                if (state && AntiBot.isServerBot(ent)) {
                    return null;
                }
                if (!locky.getValue() && !isTarget(ent, (float) f)) {
                    return null;
                }
                if (ent.isInvisible()) {
                    return null;
                }
                k = ent;
                f = (int) getRot(ent);
            }
        }
        return k;
    }
}
