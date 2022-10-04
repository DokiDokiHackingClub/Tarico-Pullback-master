package dev.tarico.api.pluginapi;
/**
 * Plugin核心API
 */
public class PluginAPI {
    /**
     * 插件ModuleManager，用于管理模块
     */
    public static PluginModuleManager moduleManager = new PluginModuleManager();

    /**
     * 插件CommandManager，用于管理命令
     */
    public static PluginCommandManager commandManager = new PluginCommandManager();
}
