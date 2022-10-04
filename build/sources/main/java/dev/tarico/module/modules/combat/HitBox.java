package dev.tarico.module.modules.combat;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.EntitySize;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;

public class HitBox extends Module {
    private final NumberValue<Double> heights = new NumberValue<>("Height", 2.0, 2.0, 5.0, 0.1);
    private final NumberValue<Double> Widths = new NumberValue<>("Width", 1.0, 1.0, 5.0, 0.1);

    public HitBox() {
        super("HitBox", "Modified HitBox", ModuleType.Combat);
        this.inVape = true;
        this.vapeName = "HitBoxes";
    }

    public static void setEntityBoundingBoxSize(Entity entity, float width, float height) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) height, entity.posZ + d0));
    }

    public static void setEntityBoundingBoxSize(Entity entity) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (entity.width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) entity.height, entity.posZ + d0));
    }

    public static EntitySize getEntitySize(Entity entity) {
        return new EntitySize(0.6F, 1.8F);
    }

    public static ArrayList<EntityPlayer> getPlayersList() {
        return (ArrayList<EntityPlayer>) mc.theWorld.playerEntities;
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (entity == mc.thePlayer) {
            return false;
        }
        return !entity.isDead;
    }

    @Native
    @EventTarget
    public void onClientTick(EventTick event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        setSuffix("Size: " + Widths.getValue());
        for (EntityPlayer player : getPlayersList()) {
            if (!check(player)) continue;
            float width = this.Widths.getValue().floatValue();
            float height = this.heights.getValue().floatValue();
            setEntityBoundingBoxSize(player, width, height);
        }
    }

    @Override
    public void disable() {
        for (EntityPlayer player : getPlayersList())
            setEntityBoundingBoxSize(player);
    }

}


