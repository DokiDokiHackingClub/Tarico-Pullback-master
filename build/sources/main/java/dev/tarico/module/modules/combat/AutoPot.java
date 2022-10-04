package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventMove;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.movement.Scaffold;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.timer.TimeHelper;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class AutoPot extends Module {

    public static TimeHelper timer = new TimeHelper();
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 500.0, 100.0, 1500.0, 50.0);
    private final NumberValue<Double> health = new NumberValue<>("Health", 10.0, 1.0, 20.0, 1.0);
    private final BooleanValue<Boolean> jump = new BooleanValue<>("Jump", false);
    private final BooleanValue<Boolean> heal = new BooleanValue<>("Heal", false);
    private final BooleanValue<Boolean> regen = new BooleanValue<>("Regen", false);
    private final BooleanValue<Boolean> speed = new BooleanValue<>("Speed", false);
    private final BooleanValue<Boolean> nofrog = new BooleanValue<>("Nofrog", false);
    private final TimeHelper cooldown = new TimeHelper();
    private boolean jumping;
    private boolean rotated;
    private int lastPottedSlot;

    public AutoPot() {
        super("AutoPot", "Auto throw potion", ModuleType.Combat);
    }

    public static int findEmptySlot() {
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] == null)
                return i;
        }

        return mc.thePlayer.inventory.currentItem + (mc.thePlayer.inventory.getCurrentItem() == null ? 0 : ((mc.thePlayer.inventory.currentItem < 8) ? 4 : -1));
    }

    public static int findEmptySlot(int priority) {
        if (mc.thePlayer.inventory.mainInventory[priority] == null)
            return priority;

        return findEmptySlot();
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onMove(final EventMove event) {
        if (this.jumping) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            event.setX(0);
            event.setZ(0);

            if (cooldown.hasReached(100) && mc.thePlayer.onGround) {
                this.jumping = false;
            }
        }
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    private void onPreUpdate(final EventPreUpdate event) {
        setSuffix("Delay:" + delay.getValue());
        if (MoveUtils.getBlockUnderPlayer(mc.thePlayer, 0.01) instanceof BlockGlass || !MoveUtils.isOnGround(0.01)) {
            timer.reset();
            return;
        }

        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                timer.reset();
                return;
            }
        }

        if (ModuleManager.instance.getModule(Scaffold.class).getState())
            return;

        if (LegitAura.target != null) {
            rotated = false;
            timer.reset();
            return;
        }

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasReached(delay.getValue())) {
            if (jump.getValue() && !mc.thePlayer.isInWater()) {
                event.setPitch(-89.5f);

                this.jumping = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    cooldown.reset();
                }
            } else {
                event.setPitch(89.5f);
            }

            rotated = true;
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onPostUpdate(final EventPostUpdate event) {
        if (!rotated)
            return;

        rotated = false;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasReached(delay.getValue()) && mc.thePlayer.isCollidedVertically) {
            final int prevSlot = mc.thePlayer.inventory.currentItem;
            if (potSlot < 9) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot));
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(prevSlot));
                mc.thePlayer.inventory.currentItem = prevSlot;
                timer.reset();

                this.lastPottedSlot = potSlot;
            }
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick event) {
        if (mc.currentScreen != null)
            return;

        final int potSlot = this.getPotFromInventory();
        if (potSlot > 8 && mc.thePlayer.ticksExisted % 4 == 0) {
            this.swap(potSlot, findEmptySlot(this.lastPottedSlot));
        }
    }

    private void swap(final int slot, final int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
                mc.thePlayer);
    }

    private int getPotFromInventory() {
        // heals
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                ItemPotion pot = (ItemPotion) item;

                if (!ItemPotion.isSplash(is.getMetadata())) {
                    continue;
                }

                List<PotionEffect> effects = pot.getEffects(is);

                for (PotionEffect effect : effects) {
                    if (mc.thePlayer.getHealth() < health.getValue() && ((heal.getValue() && effect.getPotionID() == Potion.heal.id) || (regen.getValue() && effect.getPotionID() == Potion.regeneration.id && hasEffect(Potion.regeneration.id))))
                        return i;
                }
            }
        }

        // others
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                List<PotionEffect> effects = ((ItemPotion) item).getEffects(is);

                for (PotionEffect effect : effects) {
                    if (effect.getPotionID() == Potion.moveSpeed.id && speed.getValue()
                            && hasEffect(Potion.moveSpeed.id))
                        if (!is.getDisplayName().contains("\247a") || !nofrog.getValue())
                            return i;
                }
            }
        }

        return -1;
    }

    private boolean hasEffect(int potionId) {
        for (PotionEffect item : mc.thePlayer.getActivePotionEffects()) {
            if (item.getPotionID() == potionId)
                return false;
        }
        return true;
    }
}

