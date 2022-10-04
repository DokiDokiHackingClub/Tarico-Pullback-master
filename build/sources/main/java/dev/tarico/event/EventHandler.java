package dev.tarico.event;

import dev.tarico.api.pluginapi.PluginAPI;
import dev.tarico.api.pluginapi.PluginModule;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.utils.forgeevent.ForgeEventManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class EventHandler {
    public EventHandler(){
        EventBus.getInstance().register(this);
        ForgeEventManager.EVENT_BUS.register(this);
    }

    @EventTarget
    public void onTick(EventTick e){
        for(PluginModule m : PluginAPI.moduleManager.getModules()){
            if(m.getState())
                m.onTick();
        }
    }
    @EventTarget
    public void onUpdate(EventPreUpdate e){
        for(PluginModule m : PluginAPI.moduleManager.getModules()){
            if(m.getState())
                m.onUpdate(e.getYaw(),e.getPitch(),e.y,e.isOnground());
        }
    }
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e){
        for(PluginModule m : PluginAPI.moduleManager.getModules()){
            if(m.getState())
                m.onKey();
        }
    }
    @EventTarget
    public void onRender2d(EventRender2D e){
        for(PluginModule m : PluginAPI.moduleManager.getModules()){
            if(m.getState())
                m.onRender2D(e.getPartialTicks());
        }
    }
    @SubscribeEvent
    public void onR3D(RenderWorldLastEvent e){
        for(PluginModule m : PluginAPI.moduleManager.getModules()){
            if(m.getState())
                m.onRender3D(e.partialTicks);
        }
    }
}
