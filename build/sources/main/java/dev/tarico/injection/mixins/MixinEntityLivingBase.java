package dev.tarico.injection.mixins;


import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.movement.Faker;
import dev.tarico.module.modules.movement.NoJumpDelay;
import dev.tarico.utils.client.FakeData;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Shadow
    private int jumpTicks;

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (ModuleManager.instance.getModule(NoJumpDelay.class).getState())
            jumpTicks = 0;
        for (FakeData d : Faker.data) {
            if (d.hurtTime > 0)
                d.hurtTime--;
        }
    }
}
