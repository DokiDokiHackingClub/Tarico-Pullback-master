package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.ReflectionUtil;
import dev.tarico.utils.timer.CPSDelay;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("all")
public class AutoClicker extends Module {
    private final BooleanValue<Boolean> left = new BooleanValue<>("Left Clicker", true);
    private final BooleanValue<Boolean> right = new BooleanValue<>("Right Clicker", false);
    private final NumberValue<Double> maxCps = new NumberValue<>("Left MaxCPS", 14.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> minCps = new NumberValue<>("Left MinCPS", 10.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> RmaxCps = new NumberValue<>("Right MaxCPS", 14.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> RminCps = new NumberValue<>("Right MinCPS", 10.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> jitter = new NumberValue<>("Jitter", 0.0, 0.0, 3.0, 0.1);
    private final BooleanValue<Boolean> blockHit = new BooleanValue<>("BlockHit", false);
    private final BooleanValue<Boolean> autoUnBlock = new BooleanValue<>("AutoUnblock", false);
    private final BooleanValue<Boolean> weaponOnly = new BooleanValue<>("Weapon Only", false);

    private final CPSDelay cpsDelay = new CPSDelay();
    private final Random random = new Random();

    public AutoClicker() {
        super("AutoClicker", "AutoClicker when hold mouse", ModuleType.Combat);
        this.inVape = true;
    }

    @Native
    public static void clickMouse() {
        int leftClickCounter = (int) ReflectionUtil.getFieldValue(Minecraft.getMinecraft(), "leftClickCounter", "field_71429_W");
        if (leftClickCounter <= 0) {
            Minecraft.getMinecraft().thePlayer.swingItem();
            if (Minecraft.getMinecraft().objectMouseOver == null) {
                if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                    ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 10, "leftClickCounter", "field_71429_W");
                }
            } else {
                switch (Minecraft.getMinecraft().objectMouseOver.typeOfHit) {
                    case ENTITY:
                        try {
                            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().objectMouseOver.entityHit);
                        } catch (NullPointerException exception) {
                            exception.printStackTrace();
                        }
                        break;

                    case BLOCK:
                        BlockPos blockpos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();

                        try {
                            if (Minecraft.getMinecraft().theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                                Minecraft.getMinecraft().playerController.clickBlock(blockpos, Minecraft.getMinecraft().objectMouseOver.sideHit);
                                break;
                            }
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }

                    case MISS:
                    default:
                        if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                            ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 10, "leftClickCounter", "field_71429_W");
                        }
                }
            }
        }
    }

    @Native
    @SubscribeEvent
    public void onTick(TickEvent e) {
        setSuffix(String.format("CPS:%s - %s", minCps.getValue(), maxCps.getValue()));
        if (!state)
            return;
        if (mc.currentScreen == null && Mouse.isButtonDown(0)) {
            if (!left.getValue())
                return;
            if (this.weaponOnly.getValue()) {
                if (mc.thePlayer.getCurrentEquippedItem() == null) {
                    return;
                }
                if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                    return;
                }
            }
            if (!blockHit.getValue() && mc.thePlayer.isUsingItem()) return;

            if (cpsDelay.shouldAttack(Objects.equals(minCps.getValue().intValue(), maxCps.getValue().intValue()) ? maxCps.getValue().intValue() : ThreadLocalRandom.current().nextInt(minCps.getValue().intValue(), maxCps.getValue().intValue()))) {
                ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 0, "leftClickCounter", "field_71429_W");
                clickMouse();

                if (autoUnBlock.getValue()) {
                    if (Mouse.isButtonDown(1)) {
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                            if (mc.thePlayer.isBlocking()) {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                                mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
                            } else {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                            }
                        }
                    }
                }
            }
            if (jitter.getValue() > 0.0) {
                final double a = jitter.getValue() * 0.45;
                if (this.random.nextBoolean()) {
                    final EntityPlayerSP thePlayer = AutoClicker.mc.thePlayer;
                    thePlayer.rotationYaw += (float) (this.random.nextFloat() * a);
                } else {
                    final EntityPlayerSP thePlayer2 = AutoClicker.mc.thePlayer;
                    thePlayer2.rotationYaw -= (float) (this.random.nextFloat() * a);
                }
                if (this.random.nextBoolean()) {
                    final EntityPlayerSP thePlayer3 = AutoClicker.mc.thePlayer;
                    thePlayer3.rotationPitch += (float) (this.random.nextFloat() * (a * 0.45));
                } else {
                    final EntityPlayerSP thePlayer4 = AutoClicker.mc.thePlayer;
                    thePlayer4.rotationPitch -= (float) (this.random.nextFloat() * (a * 0.45));
                }
            }
        }

        if (mc.currentScreen == null && Mouse.isButtonDown(1)) {
            if (!right.getValue())
                return;
            if (cpsDelay.shouldAttack(RminCps.getValue().intValue() == RmaxCps.getValue().intValue() ? RmaxCps.getValue().intValue() : ThreadLocalRandom.current().nextInt(RminCps.getValue().intValue(), RmaxCps.getValue().intValue() + 1))) {
                try {
                    final Field rightClickDelay = Minecraft.class.getDeclaredField("field_71467_ac");
                    rightClickDelay.setAccessible(true);
                    rightClickDelay.set(mc, 0);
                } catch (Exception d) {
                    try {
                        final Field ex = Minecraft.class.getDeclaredField("rightClickDelayTimer");
                        ex.setAccessible(true);
                        ex.set(mc, 0);
                    } catch (Exception f) {
                        this.disable();
                    }
                }
            }
        }
    }
}
