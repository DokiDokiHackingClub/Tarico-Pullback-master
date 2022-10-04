package dev.tarico.module.value;

public class NumberValue<T extends Number>
        extends Value<T> {
    public T min;
    public T max;
    public T inc;
    private String name;
    private final boolean integer;

    public NumberValue(String name, T value, T min, T max, T inc) {
        super(name);
        this.setValue(value);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.integer = false;
    }

    public boolean isInteger() {
        return integer;
    }

    public T getMinimum() {
        return this.min;
    }

    public T getMaximum() {
        return this.max;
    }

    public T getIncrement() {
        return this.inc;
    }

    public void setIncrement(T inc) {
        this.inc = inc;
    }

    public String getId() {
        return this.name;
    }
}

