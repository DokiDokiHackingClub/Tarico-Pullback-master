package dev.tarico.module.modules;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.lua.vm.Globals;

import java.util.ArrayList;
import java.util.List;

public class LuaModule extends Module{

    public Globals globals;
    private final List<String> blacklist;

    public LuaModule(final String name, final Globals globals){
        super(name,"Lua Module",ModuleType.Plugin);
        this.globals = globals;
        this.blacklist = new ArrayList<>();
        this.name = name;
    }

    public void enable(){
        invoke("onEnable");
    }
    public void disable(){
        invoke("onDisable");
    }

    @EventTarget
    public void onTick(EventTick e){
        invoke("onTick");
    }

    @EventTarget
    public void onPrePlayerUpdate(EventPreUpdate e){
        invoke("onPreUpdate");
    }
    @EventTarget
    public void onPostPlayerUpdate(EventPostUpdate e){
        invoke("onPostUpdate");
    }
    @EventTarget
    public void onRender2D(EventRender2D e){
        invoke("onRender2D");
    }


    protected void invoke(String method) {
        try {
            if (!globals.get(method).isnil()
                    && !blacklist.contains(method))
                globals.get(method).invoke();
            else
                blacklist.add(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
