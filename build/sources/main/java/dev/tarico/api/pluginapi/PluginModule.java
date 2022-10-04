package dev.tarico.api.pluginapi;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;

/**
 * 插件模块
 */
public class PluginModule {
    public Module nativeModule;

    /**
     * 模块名称
     */
    String name;


    /**
     * 模块是否启用
     */
    boolean enable = false;


    /**
     * 模块Value管理器
     */
    public ValueManager values;


    /**
     * 玩家对象
     */
    public static PluginEntityClientPlayer player = PluginEntityClientPlayer.getClientPlayer();


    /**
     * 绘制类实用
     */
    public static PluginRenderUtils renderUtils = PluginRenderUtils.getInstance();


    /**
     * 游戏设置实用
     */
    public static PluginGameSetting gameSetting = PluginGameSetting.getGameSettings();


    /**
     * 世界对象
     */
    public static PluginWorldClient world = PluginWorldClient.getWorldClient();

    public PluginModule(String name){
        nativeModule = new Module(name,"plugin module", ModuleType.Plugin){
            @Override
            public void enable() {
                enable = true;
            }

            @Override
            public void disable() {
                enable = false;
            }
        };
        values = new ValueManager(this);
        this.name = name;
    }

    /**
     * 获取模块名称
     */
    public String getName() {
        return name;
    }


    /**
     * 获取模块是否启用
     */
    public boolean getState() {
        return enable;
    }


    /**
     * 设置模块是否启用
     */
    public void setState(boolean state) {
        if (this.enable == state) {
            return;
        }
        this.enable = state;
        nativeModule.setState(state);
    }


    /**
     * Tick事件
     */
    public void onTick(){}


    /**
     * 2D渲染事件
     */
    public void onRender2D(float partialTicks){}


    /**
     * 3D渲染事件
     */
    public void onRender3D(float partialTicks){}


    /**
     * 玩家更新事件
     */
    public void onUpdate(float yaw, float pitch, double y, boolean onGround){};


    /**
     * 键盘输入事件
     */
    public void onKey(){}
}
