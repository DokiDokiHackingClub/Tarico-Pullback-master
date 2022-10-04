package dev.tarico.api.pluginapi;

import dev.tarico.api.pluginapi.PluginEntityClientPlayer;
import dev.tarico.api.pluginapi.PluginGameSetting;
import dev.tarico.api.pluginapi.PluginRenderUtils;
import dev.tarico.api.pluginapi.PluginWorldClient;
import dev.tarico.module.command.Command;

/**
 * 插件命令
 */
public abstract class PluginCommand {
    protected final String name, description;
    public final Command nativeCommand;
    public static PluginEntityClientPlayer player = PluginEntityClientPlayer.getClientPlayer();
    public static PluginRenderUtils renderUtils = PluginRenderUtils.getInstance();
    public static PluginGameSetting gameSetting = PluginGameSetting.getGameSettings();
    public static PluginWorldClient world = PluginWorldClient.getWorldClient();


    /**
     * 构造命令
     * @param name 命令名称，小写
     * @param description 命令描述，即输入.help时的描述
     */
    public PluginCommand(String name, String description) {
        this.name = name;
        this.description = description;

        this.nativeCommand = new Command(name, new String[]{},description) {
            @Override
            public String execute(String[] var1) {
                onExecute(var1);
                return null;
            }
        };
    }


    /**
     * 命令执行
     * @param args 命令参数
     */
    public abstract void onExecute(String[] args);
}
