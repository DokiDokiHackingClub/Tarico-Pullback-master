package me.cubk.plugin.utils;

/**
 * Generic exception for when shit hits the fan loading and unloading plugins
 * @author Eric Golde
 *
 */
public class PluginException extends RuntimeException {

	public PluginException(String message) {
		super(message);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
