package dev.tarico.module.modules.fun;

import dev.tarico.event.EventTarget;
import dev.tarico.event.Type;
import dev.tarico.event.events.rendering.EventModel;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.model.ModelBiped;

public class Loli extends Module {
    public int y;

    public Loli() {
        super("Loli", "Player Render", ModuleType.Fun);
    }

    @Override
    public void enable() {
        y = 0;
    }


    @EventTarget
    @SuppressWarnings("unused")
    public void onEmote(EventModel event) {
        if (event.type == Type.POST) {
            if (event.entity == mc.thePlayer) {
                setBiped(event.getBiped());
            }
        }
    }

    public void setBiped(ModelBiped biped) {
        if (mc.gameSettings.thirdPersonView > 0) {
            biped.bipedRightArm.rotateAngleX = 0.5F;
            biped.bipedRightArm.rotateAngleY = -2.25F;
            biped.bipedLeftArm.rotateAngleX = 0.5F;
            biped.bipedLeftArm.rotateAngleY = 2.25F;
            biped.aimedBow = true;
            biped.isChild = true;
        }
    }
}
