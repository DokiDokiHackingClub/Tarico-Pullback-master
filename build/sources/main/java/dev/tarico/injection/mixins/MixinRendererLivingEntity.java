package dev.tarico.injection.mixins;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.combat.LegitAura;
import dev.tarico.module.modules.movement.Scaffold;
import dev.tarico.module.modules.render.NameTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender {


    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    public void onChat(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (ModuleManager.instance.getModule(NameTags.class).getState() && entity instanceof EntityPlayer)
            ci.cancel();
    }


    @Shadow
    protected boolean renderOutlines = false;
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    @Final
    private static Logger logger;

    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow
    protected abstract float getSwingProgress(T livingBase, float partialTickTime);

    @Shadow
    protected abstract void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    @Shadow
    protected abstract void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks);

    @Shadow
    protected abstract float handleRotationFloat(T livingBase, float partialTicks);

    @Shadow
    protected abstract void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    @Shadow
    protected abstract boolean setScoreTeamColor(EntityLivingBase entityLivingBaseIn);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor);

    @Shadow
    protected abstract void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_);

    @Shadow
    protected abstract boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    @Shadow
    protected abstract void unsetBrightness();


    public void doRenderModel(Object entitylivingbaseIn, float a, float b, float c, float d, float e, float scaleFactor) {
        this.renderModel((T) entitylivingbaseIn, a, b, c, d, e, scaleFactor);
    }

    public void doRenderLayers(Object entitylivingbaseIn, float a, float b, float partialTicks, float d, float e, float f, float g) {
        this.renderLayers((T) entitylivingbaseIn, a, b, partialTicks, d, e, f, g);
    }


    /**
     * @author Czf_233
     * @reason Fix LegitAura and Scaffold
     */
    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isRiding();

        this.mainModel.isChild = entity.isChild();

        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapAngleTo180_float(f2);

                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }
            }

            //Created by Thread on 2020年12月11日21:25:25;

            float f8 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

            if(entity == Minecraft.getMinecraft().thePlayer && ModuleManager.instance.getModule(Scaffold.class).getState()) {
                f8= Scaffold.rotation.getPitch();
            }else if(entity == Minecraft.getMinecraft().thePlayer && ModuleManager.instance.getModule(LegitAura.class).getState() && !ModuleManager.instance.getModule(Scaffold.class).getState()) {
                f8= LegitAura.facing[1];
            }else {
                f8=entity.prevRotationPitch
                        + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            }

            this.renderLivingAt(entity, x, y, z);
            float f7 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f7, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, partialTicks);
            float f4 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

            if (entity.isChild()) {
                f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f7, f2, f8, 0.0625F, entity);

            if (this.renderOutlines) {
                boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

                if (flag1) {
                    this.unsetScoreTeamColor();
                }
            } else {
                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

                if (flag) {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                    this.renderLayers(entity, f6, f5, partialTicks, f7, f2, f8, 0.0625F);
                }
            }

            GlStateManager.disableRescaleNormal();
        } catch (Exception exception) {
            logger.error("Couldn't render entity", exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        if (!this.renderOutlines) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
}
