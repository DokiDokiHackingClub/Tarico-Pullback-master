package dev.tarico.event.events.misc;

import dev.tarico.event.Event;

public class EventChat
        extends Event {
    private final String message;

    public EventChat(String message) {
        this.message = message;
        this.setType((byte) 0);
    }

    public String getMessage() {
        return this.message;
    }

}

