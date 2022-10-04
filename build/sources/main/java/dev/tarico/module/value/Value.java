package dev.tarico.module.value;

public abstract class Value<V> {
    private final String name;
    public float animX1;
    public float animX;
    public boolean drag;
    private V value;

    public Value(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}

