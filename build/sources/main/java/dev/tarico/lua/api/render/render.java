package dev.tarico.lua.api.render;

import dev.tarico.lua.vm.LuaValue;
import dev.tarico.lua.vm.lib.FiveArgFunction;
import dev.tarico.lua.vm.lib.TwoArgFunction;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.utils.client.RenderUtil;

public class render extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaValue lib = tableOf();
        lib.set("drawRect",new drawRect());
        lib.set("drawString",new drawString());
        env.set("render",lib);
        env.get("package").get("loaded").set("render",lib);
        return lib;
    }

    static class drawRect extends FiveArgFunction {
        @Override
        public LuaValue call(LuaValue x, LuaValue y, LuaValue w, LuaValue h, LuaValue col) {
            RenderUtil.drawRect(x.tofloat(), y.tofloat(), x.tofloat() + w.tofloat(), y.tofloat() + h.tofloat(),col.toint());
            return LuaValue.valueOf(0);
        }
    }
    static class drawString extends FiveArgFunction {
        @Override
        public LuaValue call(LuaValue str, LuaValue x, LuaValue y, LuaValue col, LuaValue shadow) {
            if(shadow.toboolean()){
                FontLoaders.F18.drawStringWithShadow(str.tojstring(), x.tofloat(), y.tofloat(), col.toint());
            }else{
                FontLoaders.F18.drawString(str.tojstring(), x.tofloat(), y.tofloat(), col.toint());
            }

            return LuaValue.valueOf(0);
        }
    }
}
