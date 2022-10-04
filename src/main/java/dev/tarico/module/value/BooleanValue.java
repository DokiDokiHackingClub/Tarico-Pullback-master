package dev.tarico.module.value;

public class BooleanValue<V>
        extends Value<V> {
    public BooleanValue(String name, V enabled) {
        super(name);
        this.setValue(enabled);
    }
}

