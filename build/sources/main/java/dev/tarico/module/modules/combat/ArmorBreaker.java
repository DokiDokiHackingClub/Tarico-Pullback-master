package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class ArmorBreaker extends Module {

    public ArmorBreaker() {
        super("ArmorBreaker", "Deals more damage to armor", ModuleType.Combat);
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            breaker(((C02PacketUseEntity) e.getPacket()).getEntityFromWorld(mc.theWorld));
        }
    }

    public void breaker(Entity en) {
        if (!mc.thePlayer.onGround) {
            return;
        }
        ItemStack current = mc.thePlayer.getHeldItem();
        for (int i = 0; i < 45; i++) {
            ItemStack toSwitch = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if ((current != null) && (toSwitch != null) && ((toSwitch.getItem() instanceof ItemSword))) {
                mc.playerController.windowClick(0, i, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer);
            }
        }
    }
}

