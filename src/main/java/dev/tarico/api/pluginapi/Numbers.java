package dev.tarico.api.pluginapi;

import dev.tarico.module.value.NumberValue;

/**
 * Double类型Value
 */
public class Numbers extends NumberValue<Double> {
    public Numbers(String name, Double value, Double min, Double max) {
        super(name, value, min, max, 0.1);
    }

    /**
     * 设置数值
     */
    public void set(Double value) {
        super.setValue(value);
    }

    /**
     * 获取Double数值
     */
    public Double get() {
        return super.getValue();
    }

    /**
     * 获取Value名称
     */
    public String name() {
        return super.getName();
    }
}
