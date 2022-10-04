package dev.tarico.api.pluginapi;

import dev.tarico.utils.client.MoveUtils;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * 插件玩家对象
 */
public class PluginEntityClientPlayer extends PluginEntityPlayer {
    public PluginEntityClientPlayer() {
        super(mc.thePlayer);
    }

    /**
     * 获取玩家实体
     *
     * @return 玩家实体对象
     */
    public static PluginEntityClientPlayer getClientPlayer() {
        return new PluginEntityClientPlayer();
    }

    public EntityPlayerSP getEntity() {
        return mc.thePlayer;
    }

    /**
     * 发送聊天信息
     *
     * @param message 聊天信息字符串
     */

    public void sendChatMessage(String message) {
        getEntity().sendChatMessage(message);
    }

    /**
     * 是否能跳跃
     */
    public boolean canJump() {
        if (mc.thePlayer.isOnLadder()) {
            return false;
        }
        if (mc.thePlayer.isInWater()) {
            return false;
        }
        if (mc.thePlayer.isInLava()) {
            return false;
        }
        if (mc.thePlayer.isSneaking()) {
            return false;
        }
        if(!mc.thePlayer.onGround)
            return false;
        return !mc.thePlayer.isRiding();
    }

    /**
     * 玩家跳跃
     */

    public void jump() {
        getEntity().jump();
    }

    /**
     * 设置当前玩家视角速度
     *
     * @param speed 需要设置的速度
     */

    public void setSpeed(double speed) {
        MoveUtils.setMotion(speed);
    }

    /**
     * 设置当前玩家视角速度和角度
     *
     * @param speed 需要设置的速度
     * @param yaw   需要设置的角度
     */

    public void setSpeed(double speed, float yaw) {
        MoveUtils.setMotion(speed, yaw);
    }

    /**
     * 玩家是否在疾跑
     *
     * @return boolean数值
     */
    public boolean isSprinting(){
        return getEntity().isSprinting();
    }

    /**
     * 设置玩家疾跑
     *
     * @param sprint 玩家是否疾跑
     */
    public void setSprint(boolean sprint){
        getEntity().setSprinting(sprint);
    }
}
