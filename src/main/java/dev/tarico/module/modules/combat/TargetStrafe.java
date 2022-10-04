package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventMove;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.movement.Fly;
import dev.tarico.module.modules.movement.LegitSpeed;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.client.PlayerUtil;
import dev.tarico.utils.client.RotationUtil;
import dev.tarico.utils.render.Vector3d;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class TargetStrafe extends Module {
    public static final BooleanValue<Boolean> onlyspeed = new BooleanValue<>("OnlySpeed", true);
    public static final BooleanValue<Boolean> jumpkey = new BooleanValue<>("OnlyJump", true);
    public static boolean direction = true;
    private final NumberValue<Double> range = new NumberValue<>("Range", 2.0, 0.0, 6.0, 0.1);
    private final ModeValue<Enum<TargetStrafeMode>> mode = new ModeValue<>("Mode", TargetStrafeMode.values(), TargetStrafeMode.Adaptive);
    private final BooleanValue<Boolean> lockPersonView = new BooleanValue<>("LockPersonView", true);

    public TargetStrafe() {
        super("TargetStrafe", "Strafe around target of aura", ModuleType.Combat);
    }

    @Native
    private static boolean isBlockUnder(Entity entity) {
        for (int i = (int) (entity.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(entity.posX,
                    i, entity.posZ);
            if (Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return false;
        }
        return true;
    }

    @EventTarget
    private void onUpdate(EventPreUpdate e) {
        if (lockPersonView.getValue() && ModuleManager.instance.getModule(LegitAura.class).getState()) {
            if ((ModuleManager.instance.getModule(LegitSpeed.class).getState() || ModuleManager.instance.getModule(Fly.class).getState())) {
                if (LegitAura.target != null) {
                    mc.gameSettings.thirdPersonView = 1;
                } else {
                    mc.gameSettings.thirdPersonView = 0;
                }
            }
        }
    }

    @EventTarget
    private void onMove(EventMove em) {
        if (PlayerUtil.isMoving2()) {
            if (LegitAura.target != null) {
                if (onlyspeed.getValue() && ModuleManager.instance.getModule(LegitSpeed.class).getState()) {
                    if (jumpkey.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
                        move(em, MoveUtils.defaultSpeed(), LegitAura.target);
                    } else if (!jumpkey.getValue()) {
                        move(em, MoveUtils.defaultSpeed(), LegitAura.target);
                    }
                } else if (!onlyspeed.getValue()) {
                    if (jumpkey.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
                        move(em, MoveUtils.defaultSpeed(), LegitAura.target);
                    }
                }
            }
        }
    }

    public void move(EventMove event, double speed, Entity entity) {
        if (isBlockUnder(entity) && mode.getValue() == TargetStrafeMode.Adaptive) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            if (event != null) {
                event.setX(0);
                event.setZ(0);
            }
            return;
        }
        if (isBlockUnder(mc.thePlayer) && mode.getValue() == TargetStrafeMode.Adaptive && !ModuleManager.instance.getModule(Fly.class).getState())
            direction = !direction;

        if (mc.thePlayer.isCollidedHorizontally && mode.getValue() == TargetStrafeMode.Adaptive)
            direction = !direction;

        float strafe = direction ? 1 : -1;
        float diff = (float) (speed / (range.getValue() * Math.PI * 2)) * 360 * strafe;
        float[] rotation = RotationUtil.getNeededRotations(new Vector3d(entity.posX, entity.posY, entity.posZ), new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

        rotation[0] += diff;
        float dir = rotation[0] * (float) (Math.PI / 180F);

        double x = entity.posX - Math.sin(dir) * range.getValue();
        double z = entity.posZ + Math.cos(dir) * range.getValue();

        float yaw = RotationUtil.getNeededRotations(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3d(x, entity.posY, z))[0] * (float) (Math.PI / 180F);

        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
        if (event != null) {
            event.setX(mc.thePlayer.motionX);
            event.setZ(mc.thePlayer.motionZ);
        }
    }

    static enum TargetStrafeMode {
        Simple,
        Adaptive
    }
}

