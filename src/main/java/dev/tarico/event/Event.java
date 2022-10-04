package dev.tarico.event;

public abstract class Event {
    public byte type;
    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setType(byte type) {
        this.type = type;
    }
}

