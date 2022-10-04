package dev.tarico.module.modules.utils;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

public class FakeFPS extends Module {
    private final NumberValue<Double> targetFPS = new NumberValue<>("FPS", 1000.0, 0.0, 3000.0, 10.0);
    public int ticksPassed;

    public FakeFPS() {
        super("FakeFPS", "Render FakeFPS", ModuleType.Utils);
    }

    public void enable() {
        ticksPassed = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Field debugFPS = ReflectionHelper.findField(Minecraft.class, "field_71420_M", "fpsCounter");
        debugFPS.setAccessible(true);
        if (event.phase == TickEvent.Phase.START) {
            try {
                int fpsN = ThreadLocalRandom.current().nextInt(targetFPS.getValue().intValue(), targetFPS.getValue().intValue() + 10);
                debugFPS.set(mc, fpsN);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                this.disable();
            }
            ticksPassed = 0;
            ticksPassed++;
        }
    }


}