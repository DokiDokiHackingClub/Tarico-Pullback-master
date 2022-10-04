package dev.tarico.event.events.misc;

import dev.tarico.event.Event;

public class EventMouse extends Event {
    private int mouseX, mouseY, mouseButton;
    private Type type;

    public EventMouse(int mouseX, int mouseY, int mouseButton, Type type) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
        this.type = type;
    }

    public int getMouseX(){
        return mouseX;
    }

    public int getMouseY(){
        return mouseX;
    }

    public int getMouseButton(){
        return mouseX;
    }

    public Type getType(){
        return type;
    }

    public enum Type {
        CLICK,
        CLICK_MOVE,
        RELEASED,
        NO_SCREEN
    }
}
