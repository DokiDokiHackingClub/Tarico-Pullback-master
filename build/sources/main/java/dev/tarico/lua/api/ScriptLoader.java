package dev.tarico.lua.api;

import dev.tarico.lua.api.render.render;
import dev.tarico.lua.vm.Globals;
import dev.tarico.lua.vm.LoadState;
import dev.tarico.lua.vm.LuaError;
import dev.tarico.lua.vm.compiler.LuaC;
import dev.tarico.lua.vm.lib.jse.JsePlatform;
import dev.tarico.management.FileManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.LuaModule;
import dev.tarico.utils.client.FileUtils;
import dev.tarico.utils.client.Helper;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ScriptLoader {
    public static ArrayList<LuaModule> luaModules = new ArrayList<>();
    public static void init()
    {
        if(!luaModules.isEmpty()){
            for(LuaModule m : luaModules){
                ModuleManager.instance.getModules().removeIf(m2 -> Objects.equals(m.getName(), m2.getName()));
            }
            luaModules.clear();
        }
        File[] files = new File(FileManager.dir,"scripts").listFiles();
        if(files == null){
            System.out.println("No Lua Scripts found!");
            return;
        }
        for(File file : files){
            if(file.getName().endsWith(".lua")){
                String script = FileUtils.readFile(file);
                LuaModule m = loadLua(script,file.getName());
                if(m != null)
                    ModuleManager.instance.registerModule(m);
            }
        }
    }

    public static LuaModule loadLua(String lua, String filename) {
        try {
            Globals globals = JsePlatform.standardGlobals();
            globals.load(new LuaAPI());
            globals.load(new render());
            LoadState.install(globals);
            LuaC.install(globals);
            globals.load(lua).call();
            String name = globals.get("getName").invoke().tojstring();
            LuaModule m = new LuaModule(name, globals);
            luaModules.add(m);
            System.out.println("Loaded Lua Module: " + m.name);
            return m;
        }catch (LuaError e){
            if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null){
                Helper.sendMessage("Failed load " + filename);
                Helper.sendMessage(e.getMessage());
            }else {
                e.printStackTrace();
            }
            return null;
        }
    }
}
