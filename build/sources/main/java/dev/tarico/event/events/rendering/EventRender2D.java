package dev.tarico.event.events.rendering;

import dev.tarico.event.Event;
import dev.tarico.utils.math.vector.Vec2i;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D
        extends Event {
    private float partialTicks;

    private final ScaledResolution resolution;

    private final Vec2i mousePosition;

    public EventRender2D(float partialTicks, ScaledResolution resolution, Vec2i mousePosition) {
        this.partialTicks = partialTicks;
        this.resolution = resolution;
        this.mousePosition = mousePosition;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

    public Vec2i getMousePosition() {
        return this.mousePosition;
    }
}

