package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.client.NoSlowInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MovementInput;

public class NoSlow extends Module {
    MovementInput origmi;

    public static final BooleanValue<Boolean> snake = new BooleanValue<>("Shift", false);

    public NoSlow() {
        super("NoSlow", "NoSlowdown when you blocking or eating", ModuleType.Movement);
        this.inVape = true;
        this.vapeName = "NoSlowdown";
    }

    @Native
    @EventTarget
    public void onTick(EventTick e) {
        if (mc.thePlayer == null) return;
        if (mc.gameSettings.keyBindSneak.isPressed())
            return;
        if (!(mc.thePlayer.movementInput instanceof NoSlowInput)) {
            origmi = mc.thePlayer.movementInput;
            mc.thePlayer.movementInput = new NoSlowInput(mc.gameSettings);
        }
        if ((mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) || !mc.gameSettings.keyBindSneak.isKeyDown()) {
            NoSlowInput move = (NoSlowInput) mc.thePlayer.movementInput;
            move.setNSD(true);
        }
    }

    @Override
    public void disable() {
        mc.thePlayer.movementInput = origmi;
    }

    @EventTarget
    public void Pre(EventPreUpdate e) {
        if (mc.gameSettings.keyBindSneak.isPressed())
            mc.thePlayer.movementInput = origmi;
        if (isUsingFood()) {
            if (mc.thePlayer.getItemInUseDuration() >= 1) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    }

    public boolean isUsingFood() {
        if (mc.thePlayer.getItemInUse() == null)
            return false;
        Item usingItem = mc.thePlayer.getItemInUse().getItem();
        return mc.thePlayer.isUsingItem() && (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion);
    }
}


