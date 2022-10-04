package dev.tarico.injection.mixins;


import dev.tarico.event.EventBus;
import dev.tarico.event.events.misc.EventStrafe;
import dev.tarico.event.events.world.EventMove;
import dev.tarico.event.events.world.EventStep;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.movement.SafeWalk;
import dev.tarico.module.modules.movement.Scaffold;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;

    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;

    @Shadow
    public float stepHeight;

    @Shadow
    public boolean onGround;

    @Inject(method = "moveEntity", at = @At("HEAD"))
    public void moveEntity(double x, double y, double z, CallbackInfo ci) {
        EventMove eventMove = new EventMove(x, y, z);
        EventBus.getInstance().call(eventMove);
        EventBus.getInstance().call(new EventStep(true, stepHeight));
    }

    @Redirect(method = {"moveEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return ModuleManager.instance.getModule(SafeWalk.class).getState() || entity.isSneaking() || (ModuleManager.instance.getModule(Scaffold.class).getState() && Scaffold.safe.getValue());
    }

    @Inject(method = "moveFlying", at = @At("RETURN"))
    public void moveFlying(float p_moveFlying_1_, float p_moveFlying_2_, float p_moveFlying_3_, CallbackInfo ci) {
        EventBus.getInstance().call(new EventStrafe());
    }
}
