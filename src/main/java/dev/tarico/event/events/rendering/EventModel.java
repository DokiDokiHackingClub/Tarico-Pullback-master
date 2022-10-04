package dev.tarico.event.events.rendering;

import dev.tarico.event.Event;
import dev.tarico.event.Type;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class EventModel extends Event {
    public ModelBiped biped;
    public Type type;
    public Entity entity;

    public EventModel(ModelBiped biped, Entity entity, Type types) {
        this.biped = biped;
        this.type = types;
        this.entity = entity;
    }

    public ModelBiped getBiped() {
        return this.biped;
    }

}
