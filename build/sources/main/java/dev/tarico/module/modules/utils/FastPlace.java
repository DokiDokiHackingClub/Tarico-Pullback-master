package dev.tarico.module.modules.utils;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;

public class FastPlace extends Module {
    private final BooleanValue<Boolean> blockOnly = new BooleanValue<>("BlockOnly", false);

    public FastPlace() {
        super("FastPlace", "Place block faster", ModuleType.Utils);
        this.inVape = true;
    }

    @SubscribeEvent
    @SuppressWarnings("all")
    public void onTick(final TickEvent.PlayerTickEvent event) {
        if (!this.state)
            return;
        if (this.blockOnly.getValue()) {
            if (mc.thePlayer.getCurrentEquippedItem() == null) {
                return;
            }
            if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) {
                return;
            }
        }
        try {
            final Field rightClickDelay = Minecraft.class.getDeclaredField("field_71467_ac");
            rightClickDelay.setAccessible(true);
            rightClickDelay.set(FastPlace.mc, 0);
        } catch (Exception d) {
            try {
                final Field e = Minecraft.class.getDeclaredField("rightClickDelayTimer");
                e.setAccessible(true);
                e.set(FastPlace.mc, 0);
            } catch (Exception f) {
                this.disable();
            }
        }
    }

}
