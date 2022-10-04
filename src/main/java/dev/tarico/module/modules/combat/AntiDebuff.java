package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.potion.Potion;

public class AntiDebuff extends Module {
    public AntiDebuff() {
        super("AntiDebuff", "Remove debuff", ModuleType.Combat);
    }

    @Native
    @EventTarget
    public void onUpdate(EventPostUpdate event) {
        if (mc.thePlayer.isPotionActive(Potion.blindness.getId())) {
            mc.thePlayer.removePotionEffect(Potion.blindness.getId());
        }
        if (mc.thePlayer.isPotionActive(Potion.confusion.getId())) {
            mc.thePlayer.removePotionEffect(Potion.confusion.getId());
        }
        if (mc.thePlayer.isPotionActive(Potion.digSlowdown.getId())) {
            mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());
        }
    }
}
