package dev.tarico.lua.api.mc;

import dev.tarico.lua.vm.LuaValue;
import dev.tarico.lua.vm.lib.TwoArgFunction;
import dev.tarico.lua.vm.lib.ZeroArgFunction;

public class mc extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaValue lib = tableOf();
        lib.set("",new getMC());
        env.set("client",lib);
        env.get("package").get("loaded").set("client",lib);
        return lib;
    }

    static class getMC extends ZeroArgFunction {

        @Override
        public LuaValue call() {

            return LuaValue.valueOf(0);
        }
    }
}
