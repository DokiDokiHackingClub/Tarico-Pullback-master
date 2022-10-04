package dev.tarico.lua.api.mc;

import dev.tarico.lua.vm.LuaValue;
import dev.tarico.lua.vm.lib.TwoArgFunction;
import dev.tarico.lua.vm.lib.ZeroArgFunction;
import net.minecraft.client.Minecraft;

public class player extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaValue lib = tableOf();
        lib.set("jump",new jump());
        env.set("player",lib);
        env.get("package").get("loaded").set("player",lib);
        return lib;
    }

    static class jump extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            Minecraft.getMinecraft().thePlayer.jump();
            return LuaValue.valueOf(0);
        }
    }

}
