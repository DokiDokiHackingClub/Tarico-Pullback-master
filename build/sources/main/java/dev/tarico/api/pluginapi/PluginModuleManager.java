package dev.tarico.api.pluginapi;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;

import java.util.ArrayList;


/**
 * 插件模块管理器
 */
public class PluginModuleManager {

    /**
     * 模块列表
     */
    static ArrayList<PluginModule> list = new ArrayList<>();


    /**
     * 获取所有插件模块
     */
    public ArrayList<PluginModule> getModules() {
        return list;
    }

    public PluginModuleManager() {

    }


    /**
     * 获取指定插件模块
     *
     * @param name 模块名称
     * @return 模块
     */
    public PluginModule getModule(String name) {
        for (PluginModule m : list) {
            if (m.getName().equalsIgnoreCase(name))
                return m;
        }
        return null;
    }


    /**
     * 取消加载所有插件模块
     */
    public void unLoad(){
        for(Module m : ModuleManager.instance.pluginModules){
            for(Module m2 : ModuleManager.instance.getModules()){
                if(m == m2){
                    ModuleManager.instance.getModules().remove(m2);
                }
            }
        }
        ModuleManager.instance.pluginModules.clear();
    }


    /**
     * 注册新的插件模块
     *
     * @param m 要进行注册的模块实例
     */
    public void registerModule(PluginModule m){
        list.add(m);
        ModuleManager.instance.registerModule(m.nativeModule);
        ModuleManager.instance.pluginModules.add(m.nativeModule);
    }
}
