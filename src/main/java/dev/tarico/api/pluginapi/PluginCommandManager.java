package dev.tarico.api.pluginapi;

import dev.tarico.Client;
import dev.tarico.module.command.Command;

/**
 * 插件命令管理器
 */
public class PluginCommandManager {

    /**
     * 注册新的插件命令
     * @param command 要进行注册的命令
     */
    public void registerCommand(PluginCommand command){
        Client.instance.commandManager.getCommands().add(command.nativeCommand);
        Client.instance.commandManager.pluginCommand.add(command.nativeCommand);
    }


    /**
     * 清除所有插件命令
     */
    public void clear(){
        for(Command m : Client.instance.commandManager.pluginCommand){
            for(Command m2 : Client.instance.commandManager.getCommands()){
                if(m == m2){
                    Client.instance.commandManager.getCommands().remove(m2);
                }
            }
        }
        Client.instance.commandManager.pluginCommand.clear();
    }
}
