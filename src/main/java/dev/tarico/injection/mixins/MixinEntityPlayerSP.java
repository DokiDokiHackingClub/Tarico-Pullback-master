package dev.tarico.injection.mixins;

import dev.tarico.event.EventBus;
import dev.tarico.event.events.misc.EventChat;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.combat.KeepSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;
    EventPostUpdate post;
    private double cachedX;
    private double cachedY;
    private double cachedZ;
    private float cachedRotationPitch;
    private float cachedRotationYaw;

    public MixinEntityPlayerSP() {
        super(null, null);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo callbackInfo) {
        EventChat event = new EventChat(message);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        cachedX = posX;
        cachedY = posY;
        cachedZ = posZ;

        cachedRotationYaw = rotationYaw;
        cachedRotationPitch = rotationPitch;

        EventPreUpdate event = new EventPreUpdate(this.rotationYaw, this.rotationPitch, this.posY, this.onGround);
        post = new EventPostUpdate(this.rotationYaw, this.rotationPitch);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            EventBus.getInstance().call(post);
            ci.cancel();
        }
        posY = event.getY();
        onGround = event.isOnground();
        rotationYaw = event.getYaw();
        rotationPitch = event.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
        posX = cachedX;
        posY = cachedY;
        posZ = cachedZ;

        rotationYaw = cachedRotationYaw;
        rotationPitch = cachedRotationPitch;

        EventBus.getInstance().call(post);
    }

    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {

        if (targetEntity.canAttackWithItem()) {
            if (!targetEntity.hitByEntity(this)) {
                float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1;

                if (targetEntity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(),
                            ((EntityLivingBase) targetEntity).getCreatureAttribute());
                } else {
                    f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
                }

                i = i + EnchantmentHelper.getKnockbackModifier(this);

                if (this.isSprinting()) {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater()
                            && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null
                            && targetEntity instanceof EntityLivingBase;

                    if (flag && f > 0.0F) {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(this);

                    if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning()) {
                        flag1 = true;
                        targetEntity.setFire(1);
                    }

                    double d0 = targetEntity.motionX;
                    double d1 = targetEntity.motionY;
                    double d2 = targetEntity.motionZ;
                    boolean flag2 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);

                    if (flag2) {
                        if (i > 0) {
                            targetEntity.addVelocity(
                                    -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i
                                            * 0.5F,
                                    0.1D, MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F)
                                            * (float) i * 0.5F);

                            if (!ModuleManager.instance.getModule(KeepSprint.class).getState()) {
                                this.motionX *= 0.6D;
                                this.motionZ *= 0.6D;
                                this.setSprinting(false);
                            }
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                            ((EntityPlayerMP) targetEntity).playerNetServerHandler
                                    .sendPacket(new S12PacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = d0;
                            targetEntity.motionY = d1;
                            targetEntity.motionZ = d2;
                        }

                        if (flag) {
                            this.onCriticalHit(targetEntity);
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(targetEntity);
                        }

                        if (f >= 18.0F) {
                            this.triggerAchievement(AchievementList.overkill);
                        }

                        this.setLastAttacker(targetEntity);

                        if (targetEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, this);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
                        ItemStack itemstack = this.getCurrentEquippedItem();
                        Entity entity = targetEntity;

                        if (targetEntity instanceof EntityDragonPart) {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart) targetEntity).entityDragonObj;

                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (itemstack != null && entity instanceof EntityLivingBase) {
                            itemstack.hitEntity((EntityLivingBase) entity, this);

                            if (itemstack.stackSize <= 0) {
                                this.destroyCurrentEquippedItem();
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                            this.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                            if (j > 0) {
                                targetEntity.setFire(j * 4);
                            }
                        }

                        this.addExhaustion(0.3F);
                    } else if (flag1) {
                        targetEntity.extinguish();
                    }
                }
            }
        }
    }

}
