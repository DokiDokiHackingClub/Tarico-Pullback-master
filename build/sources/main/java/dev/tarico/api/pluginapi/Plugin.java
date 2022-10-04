package dev.tarico.api.pluginapi;

import me.cubk.plugin.PluginDescription;
import me.cubk.plugin.utils.PluginException;

/**
 * 插件Plugin接口，插件主类
 */
public interface Plugin {

	ThreadLocal<PluginDescription> descriptionFile = new ThreadLocal<PluginDescription>();
	/**
	 * onLoad方法，加载时调用
	 */
	abstract void onLoad();

	/**
	 * unLoad方法，卸载时调用
	 */
	abstract void onUnload();


	default void setDescriptionFile(PluginDescription descriptionFile) {
		if(this.descriptionFile.get() != null) {
			throw new PluginException("Can't set the description file. Its already set!");
		}
		this.descriptionFile.set(descriptionFile);
	}

	default PluginDescription getDescriptionFile() {
		return descriptionFile.get();
	}

}