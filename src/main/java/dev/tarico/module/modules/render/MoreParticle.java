package dev.tarico.module.modules.render;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoreParticle extends Module {
    public static BooleanValue<Boolean> critical = new BooleanValue<>("Critical Particle", true);
    public static BooleanValue<Boolean> mcritical = new BooleanValue<>("Enchant Particle", false);
    private final NumberValue<Double> crack = new NumberValue<>("CrackSize", 1.0, 0.0, 5.0, 0.1);

    public MoreParticle() {
        super("MoreParticle", "Display More Particle when attack", ModuleType.Render);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        int crackSize = this.crack.getValue().intValue();
        int i2 = 0;
        while (i2 < crackSize && e.target != null) {
            if (critical.getValue())
                mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT);
            if (mcritical.getValue())
                mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT_MAGIC);
            i2++;
        }
    }
}
