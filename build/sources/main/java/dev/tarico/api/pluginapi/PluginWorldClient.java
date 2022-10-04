package dev.tarico.api.pluginapi;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 世界信息获取
 */
public class PluginWorldClient {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static PluginWorldClient getWorldClient() {
        return new PluginWorldClient();
    }

    /**
     * 使用实体id获取实体对象
     *
     * @param id 实体id
     * @return 实体对象
     */

    public PluginEntity getEntityByID(int id) {
        Entity entity = mc.theWorld.getEntityByID(id);
        if (entity instanceof EntityPlayer) {
            return new PluginEntityPlayer((EntityPlayer) entity);
        } else if (entity instanceof EntityLivingBase) {
            return new PluginEntityLivingBase((EntityLivingBase) entity);
        } else if (entity != null) {
            return new PluginEntity(entity);
        }
        return null;
    }

    /**
     * 获取世界中所有已加载实体
     *
     * @return 实体列表
     */

    public List<PluginEntity> getLoadedEntities() {
        ArrayList<PluginEntity> list = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            list.add(new PluginEntity(entity));
        }
        return list;
    }

    /**
     * 获取世界中所有已加载的玩家实体
     *
     * @return 玩家实体列表
     */

    public List<PluginEntityPlayer> getLoadedPlayerEntities() {
        ArrayList<PluginEntityPlayer> list = new ArrayList<>();
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            list.add(new PluginEntityPlayer(entity));
        }
        return list;
    }

    /**
     * 删除实体
     *
     * @param entity 实体对象
     */

    public void removeEntity(PluginEntity entity) {
        mc.theWorld.removeEntity(entity.getEntity());
    }
}
