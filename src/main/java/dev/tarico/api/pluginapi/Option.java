package dev.tarico.api.pluginapi;

import dev.tarico.module.value.BooleanValue;

/**
 * Boolean类型Value
 */
public class Option extends BooleanValue<Boolean> {
    public Option(String name, Boolean enabled) {
        super(name, enabled);
    }

    /**
     * 获取是否启用
     */
    public Boolean get() {
        return super.getValue();
    }

    /**
     * 获取数值名称
     */
    public String name() {
        return super.getName();
    }

    /**
     * 设置是否启用
     */
    public void set(Boolean value) {
        super.setValue(value);
    }

}
