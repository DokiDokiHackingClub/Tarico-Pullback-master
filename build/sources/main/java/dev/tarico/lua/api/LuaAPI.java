package dev.tarico.lua.api;

import dev.tarico.Client;
import dev.tarico.lua.vm.LuaValue;
import dev.tarico.lua.vm.lib.TwoArgFunction;
import dev.tarico.lua.vm.lib.ZeroArgFunction;

public class LuaAPI extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaValue lib = tableOf();
        lib.set("printVersion",new printVer());
        env.set("client",lib);
        env.get("package").get("loaded").set("client",lib);
        return lib;
    }

    static class printVer extends ZeroArgFunction{

        @Override
        public LuaValue call() {
            System.out.println("Client version: " + Client.instance.version);
            return LuaValue.valueOf(0);
        }
    }

}
