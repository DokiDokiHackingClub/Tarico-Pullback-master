package dev.tarico.api.pluginapi;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 插件玩家实体对象
 */
public class PluginEntityPlayer extends PluginEntityLivingBase {
    public PluginEntityPlayer(EntityPlayer entity) {
        super(entity);
    }


    public EntityPlayer getEntity() {
        return ((EntityPlayer) entity);
    }

    /**
     * 获取实体是否在飞行
     *
     * @return 是否在飞行
     */

    public boolean getFlying() {
        return getEntity().capabilities.isFlying;
    }

    /**
     * 获取实体是否可以飞行
     *
     * @return 是否可以飞行
     */

    public boolean canFlying() {
        return getEntity().capabilities.allowFlying;
    }

    /**
     * 获取实体手中物品使用计数
     *
     * @return 物品使用计数
     */

    public int getItemInUseCount() {
        return getEntity().getItemInUseCount();
    }


    /**
     * 获取实体是否在格挡
     *
     * @return 实体是否在格挡
     */

    public boolean isBlocking() {
        return getEntity().isBlocking();
    }

    /**
     * 获取实体是否在使用物品
     *
     * @return 实体是否在使用物品
     */

    public boolean isUsingItem() {
        return getEntity().isUsingItem();
    }

    /**
     * 获取实体饥饿值
     *
     * @return 实体饥饿值
     */

    public int getFoodLevel() {
        return getEntity().getFoodStats().getFoodLevel();
    }
}
