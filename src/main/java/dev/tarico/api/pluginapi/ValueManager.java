package dev.tarico.api.pluginapi;


import dev.tarico.module.value.Value;

/**
 * 数值管理器
 */
public class ValueManager {

    /**
     * 对应模块
     */
    private final PluginModule mod;

    /**
     * 数值管理器构造方法
     * @param mod 对应模块
     */
    public ValueManager(PluginModule mod) {
        this.mod = mod;
    }

    /**
     * 添加新的Numbers数值
     * @param value 数值
     */
    public void addValue(Numbers value){
        mod.nativeModule.getValues().add(value);
    }

    /**
     * 添加新的Option数值
     * @param value 数值
     */
    public void addValue(Option value){
        mod.nativeModule.getValues().add(value);
    }

    /**
     * 添加新的Numbers数值
     * @param name 数值名称
     * @param value 默认数值
     * @param min 最小数值
     * @param max 最大数值
     * @return 生成的数值对象
     */
    @Deprecated
    public Numbers addNumbers(String name, double value, double min, double max) {
        Numbers num = new Numbers(name, value, min, max);
        try {
            mod.nativeModule.getValues().add(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }


    /**
     * 添加Option数值
     * @param name 数值名称
     * @param state 状态
     * @return 生成的数值对象
     */
    @Deprecated
    public Option addOption(String name, boolean state) {
        Option option = new Option(name, state);
        try {
            mod.nativeModule.getValues().add(option);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return option;
    }
}
