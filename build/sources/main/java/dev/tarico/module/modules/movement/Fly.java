package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.*;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.DamageUtil;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.client.PlayerUtil;
import dev.tarico.utils.client.invcleaner.MovementUtils;
import dev.tarico.utils.math.MathUtil;
import dev.tarico.utils.timer.MSTimer;
import dev.tarico.utils.timer.TickTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Random;

public class Fly extends Module {
    private final ModeValue<Enum<DmgMode>> mode = new ModeValue<>("Damage", DmgMode.values(), DmgMode.New);
    private final ModeValue<Enum<FlyMod>> flymode = new ModeValue<>("Mode", FlyMod.values(), FlyMod.Zoom);
    int counter, level;
    private final ArrayList<Packet> packetList = new ArrayList<>();
    private final MSTimer flyTimer = new MSTimer();
    private final TickTimer hypixelTimer = new TickTimer();
    int Const;
    float y;
    private boolean failedStart = false;
    private boolean noPacketModify;
    private int boostHypixelState = 1;
    double moveSpeed, lastDist;
    private double startY;
    private final NumberValue<Double> Speed = new NumberValue<Double>("Speed", 0.5, 0.1, 15.0, 0.1);
    boolean b2;
    private double lastDistance;

    @EventTarget
    public void onTick(EventTick e) {
        if (flymode.getValue() == FlyMod.Vanilla) {
            Double speed = this.Speed.getValue();
            EntityPlayerSP player = mc.thePlayer;
            player.jumpMovementFactor = 0.4f;
            player.motionX = 0.0;
            player.motionY = 0.0;
            player.motionZ = 0.0;
            player.jumpMovementFactor *= (speed.floatValue() * 3.0f);
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                player.motionY += speed;
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                player.motionY -= speed;
            }
        }
    }

    @Override
    public void disable() {
        if (flymode.getValue() == FlyMod.Zoom) {
            if (mc.thePlayer == null)
                return;
            mc.thePlayer.motionX = 0.0D;
            mc.thePlayer.motionZ = 0.0D;
            Client.getTimer().timerSpeed = 1F;
            mc.thePlayer.stepHeight = 0.6F;
            mc.thePlayer.jumpMovementFactor = 1.0f;
            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
            playerCapabilities.allowFlying = true;
            playerCapabilities.isFlying = true;
        }
    }

    public Fly() {
        super("Fly", "Flying", ModuleType.Movement);
    }

    @Native
    public static void damage() {
        double offset = 0.060100000351667404D;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        EntityPlayerSP player = mc.thePlayer;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        for (int i = 0; i < getMaxFallDist() / 0.05510000046342611D + 1.0D; i++) {
            netHandler.addToSendQueue(
                    new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404D, z, false));
            netHandler.addToSendQueue(
                    new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4D, z, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x,
                    y + 0.004999999888241291D + 6.01000003516674E-8D, z, false));
        }
        netHandler.addToSendQueue(new C03PacketPlayer(true));
    }

    @Native
    public static void NovolineDamage() {
        double offset = 0.060100000351667404;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        EntityPlayerSP player = mc.thePlayer;
        double x = player.posX;
        double y = player.posY;
        double z2 = player.posZ;
        int i = 0;
        while ((double) i < (double) PlayerUtil.getMaxFallDist() / 0.05510000046342611 + 1.0) {
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404, z2, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4, z2, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.004999999888241291 + 6.01000003516674E-8, z2, false));
            ++i;
        }
        netHandler.addToSendQueue(new C03PacketPlayer(true));
    }

    @Native
    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;
        return (mc.thePlayer.getMaxFallHeight() + f);
    }

    @Native
    public void ETBDamage(int damage) {
        if (damage < 1)
            damage = 1;
        if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
            damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

        double offset = 0.0625;
        if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
            for (int i = 0; i <= ((3 + damage) / offset); i++) { // TODO: teach rederpz (and myself) how math works
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
            }
        }
    }

    @Native
    @Override
    public void enable() {
        if (flymode.getValue() == FlyMod.Zoom) {
            if (mc.thePlayer == null)

            flyTimer.reset();

            noPacketModify = true;

            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            if (!mc.thePlayer.onGround)
            if (this.mode.getValue() == DmgMode.New) {
                NovolineDamage();
            }
            if (this.mode.getValue() == DmgMode.Hypixel) {
                damage();
            }
            if (this.mode.getValue() == DmgMode.ETB) {
                this.ETBDamage(1);
            }
            mc.thePlayer.jump();
            mc.thePlayer.posY += 0.42F; // Visual
            boostHypixelState = 1;
            moveSpeed = 0.1D;
            lastDistance = 0D;
            failedStart = false;
            startY = mc.thePlayer.posY;
            noPacketModify = false;
        } else if (flymode.getValue() == FlyMod.OldNCP) {
            DamageUtil.hypixelDamage();
            mc.thePlayer.motionY = 0.42f;
            level = 1;
            moveSpeed = 0.1D;
            b2 = true;
            lastDist = 0.0D;
        }
    }

    @EventTarget
    public void onStep(EventStep e) {
        if (flymode.getValue() == FlyMod.Zoom) {
            mc.thePlayer.stepHeight = 0.0F;
        }
    }

    @EventTarget
    public void onPost(EventPostUpdate event) {
        if (flymode.getValue() == FlyMod.Zoom) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDistance = Math.sqrt(xDist * xDist + zDist * zDist);
        } else if (flymode.getValue() == FlyMod.OldNCP) {
            double xDist = mc.thePlayer.posX
                    - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ
                    - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    @Native
    @EventTarget
    public void onPre(EventPreUpdate event) {
        if (flymode.getValue() == FlyMod.Zoom) {
            if (boostHypixelState > 1)
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001599999986864 + (new Random()).nextDouble() / 100000, mc.thePlayer.posZ);
            switch (++this.Const) {
                case 1: {
                    this.y *= -0.94666665455465;
                    break;
                }
                case 2:
                case 3:
                case 4: {
                    this.y += 1.45E-3;
                    break;
                }
                case 5: {
                    this.y += 1.0E-3;
                    this.Const = 0;
                    break;
                }
            }
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + this.y, mc.thePlayer.posZ);
            if (boostHypixelState > 1)
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001599999986864 + (new Random()).nextDouble() / 100000, mc.thePlayer.posZ);
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-16, mc.thePlayer.posZ);


            if (!failedStart) {
                mc.thePlayer.motionY = 0D;
            }
        } else if (flymode.getValue() == FlyMod.OldNCP) {
            ++counter;
            if (mc.thePlayer.moveForward == 0
                    && mc.thePlayer.moveStrafing == 0) {
                mc.thePlayer.setPosition(
                        mc.thePlayer.posX + 1.0D,
                        mc.thePlayer.posY + 1.0D,
                        mc.thePlayer.posZ + 1.0D);
                mc.thePlayer.setPosition(mc.thePlayer.prevPosX,
                        mc.thePlayer.prevPosY,
                        mc.thePlayer.prevPosZ);
                mc.thePlayer.motionX = 0.0D;
                mc.thePlayer.motionZ = 0.0D;
            }
            mc.thePlayer.motionY = 0.0D;
            if (mc.gameSettings.keyBindJump.isPressed())
                mc.thePlayer.motionY += 0.5f;
            if (mc.gameSettings.keyBindSneak.isPressed())
                mc.thePlayer.motionY -= 0.5f;
            if (counter == 2) {
                mc.thePlayer.setPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + 1.0E-10D,
                        mc.thePlayer.posZ);
                counter = 0;
            }
        }
    }

    @Native
    @EventTarget
    private void onUpdate(EventPreUpdate e) {
        if (flymode.getValue() == FlyMod.Zoom) {
            final int boostDelay = 1500;
            if (!flyTimer.hasTimePassed(boostDelay)) {
                Client.getTimer().timerSpeed = (float) (1F
                        + (2.03 * ((float) flyTimer.hasTimeLeft(boostDelay) / (float) boostDelay)));
            }

            hypixelTimer.update();

            if (hypixelTimer.hasTimePassed(2)) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                hypixelTimer.reset();
            }
        }
    }

    @Native
    @EventTarget
    private void onMove(EventMove e) {
        if (flymode.getValue() == FlyMod.Zoom) {
            if (!MovementUtils.isMoving()) {
                e.setX(0D);
                e.setZ(0D);
                return;
            }

            if (failedStart)
                return;

            final double amplifier = 1 + (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    ? 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1)
                    : 0);
            final double baseSpeed = 0.29D * amplifier;

            switch (boostHypixelState) {
                case 1:
                    moveSpeed = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.46 + ((new Random()).nextBoolean() ? new Random().nextDouble() / 1000 : -(new Random().nextDouble() / 1000)) : 1.945 + ((new Random()).nextBoolean() ? new Random().nextDouble() / 1000 : -(new Random().nextDouble() / 1000))) * baseSpeed;
                    boostHypixelState = 2;
                    break;
                case 2:
                    moveSpeed *= this.Speed.getValue() + ((new Random()).nextBoolean() ? ((new Random()).nextDouble() / 100000) : -((new Random()).nextDouble() / 100000));
                    boostHypixelState = 3;
                    break;
                case 3:
                    moveSpeed = lastDistance
                            - (mc.thePlayer.ticksExisted % 2 == 0
                            ? (0.2743D + ((new Random()).nextBoolean() ? new Random().nextDouble() / 10000 : -(new Random().nextDouble() / 10000)))
                            : (0.2633D + ((new Random()).nextBoolean() ? new Random().nextDouble() / 8900 : -(new Random().nextDouble() / 8900)))) * (lastDistance - baseSpeed);

                    boostHypixelState = 4;
                    break;
                case 4:
                    moveSpeed = lastDistance - lastDistance / 159.8D;
                    break;
            }

            moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed());
            MoveUtils.setMotion(e, moveSpeed);
        } else if (flymode.getValue() == FlyMod.OldNCP) {
            float forward = mc.thePlayer.moveForward;
            float strafe = mc.thePlayer.moveStrafing;
            float yaw = mc.thePlayer.rotationYaw;
            double mx = Math.cos(Math.toRadians(yaw + 90.0F));
            double mz = Math.sin(Math.toRadians(yaw + 90.0F));

            if (forward == 0.0F && strafe == 0.0F) {
                e.setX(0.0D);
                e.setZ(0.0D);
            } else if (forward != 0.0F) {
                if (strafe >= 1.0F) {
                    strafe = 0.0F;
                } else if (strafe <= -1.0F) {
                    strafe = 0.0F;
                }

                if (forward > 0.0F) {
                    forward = 1.0F;
                } else if (forward < 0.0F) {
                    forward = -1.0F;
                }
            }
            if (b2) {
                if (level != 1 || mc.thePlayer.moveForward == 0.0F
                        && mc.thePlayer.moveStrafing == 0.0F) {
                    if (level == 2) {
                        level = 3;
                        moveSpeed *= 2.1499999D;
                    } else if (level == 3) {
                        level = 4;
                        double difference = (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0103D : 0.0123D)
                                * (lastDist - MathUtil.getBaseMovementSpeed());
                        moveSpeed = lastDist - difference;
                    } else {
                        if (mc.theWorld
                                .getCollidingBoundingBoxes(mc.thePlayer,
                                        mc.thePlayer.getEntityBoundingBox().offset(0.0D,
                                                mc.thePlayer.motionY, 0.0D))
                                .size() > 0 || mc.thePlayer.isCollidedVertically) {
                            level = 1;
                        }
                        moveSpeed = lastDist - lastDist / 159.0D;
                    }
                } else {
                    level = 2;
                    int amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed)
                            ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed)
                            .getAmplifier() + 1
                            : 0;
                    double boost = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56
                            : 2.034;
                    moveSpeed = boost * MathUtil.getBaseMovementSpeed();
                }
                moveSpeed = Math.max(moveSpeed, MathUtil.getBaseMovementSpeed());

                e.setX((double) forward * moveSpeed * mx + (double) strafe * moveSpeed * mz);
                e.setZ((double) forward * moveSpeed * mz - (double) strafe * moveSpeed * mx);
                if (forward == 0.0F && strafe == 0.0F) {
                    e.setX(0.0D);
                    e.setZ(0.0D);
                }
            }
        }
    }

    enum FlyMod {
        Zoom,
        Vanilla,
        OldNCP
    }

    enum DmgMode {
        New,
        Hypixel,
        ETB
    }


}


