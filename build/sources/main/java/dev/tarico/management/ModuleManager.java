package dev.tarico.management;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.combat.*;
import dev.tarico.module.modules.fun.*;
import dev.tarico.module.modules.hudimplement.Arrow;
import dev.tarico.module.modules.hudimplement.Compass;
import dev.tarico.module.modules.hudimplement.HotBar;
import dev.tarico.module.modules.movement.*;
import dev.tarico.module.modules.plugin.ReloadLUA;
import dev.tarico.module.modules.render.*;
import dev.tarico.module.modules.utils.*;
import dev.tarico.module.value.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum ModuleManager {
    instance;

    static final ArrayList<Module> moduleArrayList = new ArrayList<>();
    public final ArrayList<Module> pluginModules = new ArrayList<>();
    public static boolean init = false;

    public ArrayList<Module> getModules() {
        return moduleArrayList;
    }

    public ArrayList<Module> getopenValues() {
        ArrayList<Module> arrayList = new ArrayList<>();
        for (Module class88 : moduleArrayList) {
            if (!class88.openValues) continue;
            arrayList.add(class88);
        }
        return arrayList;
    }

    public void registerModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value) module.getValues().add((Value<?>) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        moduleArrayList.add(module);
    }

    public List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<>();
        for (Module m : moduleArrayList) {
            if (m.getCategory() != t)
                continue;
            output.add(m);
        }
        return output;
    }

    public Module getModule(String name) {
        for (Module m : moduleArrayList) {
            if (m.getName().equalsIgnoreCase(name))
                return m;
        }
        return null;
    }

    public Module getModule(Class<?> c) {
        for (Module m : moduleArrayList) {
            if (m.getClass() == c)
                return m;
        }
        return null;
    }

    @Native
    public void init() {
        registerModule(new Sprint());
        registerModule(new ClickGui());
        registerModule(new AntiFireball());
        registerModule(new Brightness());
        registerModule(new Capes());
        registerModule(new HotBar());
        registerModule(new Disabler());
        registerModule(new Compass());
        registerModule(new Xray());
        registerModule(new Health());
        registerModule(new InvMove());
        registerModule(new Faker());
        registerModule(new MCF());
        registerModule(new ZombieTrigger());
        registerModule(new HitBox());
        registerModule(new BedESP());
        //registerModule(new UHCHelper());
        registerModule(new Freecam());
        registerModule(new BlockOverlay());
        registerModule(new LagBackCheck());
        registerModule(new AntiFire());
        registerModule(new ESP());
        registerModule(new AutoArmor());
        registerModule(new TargetStrafe());
        //registerModule(new SpeedMine());
        registerModule(new MoreParticle());
        registerModule(new MCP());
        registerModule(new AutoGG());
        registerModule(new Velocity());
        registerModule(new Panic());
        registerModule(new SpeedMine());
        registerModule(new ReloadLUA());
        registerModule(new ArmorBreaker());
        registerModule(new LegitAura());
        registerModule(new Scaffold());
        registerModule(new Loli());
        registerModule(new Strafe());
        registerModule(new AutoL());
        registerModule(new NoHurtCam());
        registerModule(new NoRotate());
        registerModule(new NameTags());
        registerModule(new Chams());
        registerModule(new Timer());
        registerModule(new AntiBot());
        registerModule(new AntiDebuff());
        registerModule(new NoSlow());
        registerModule(new AutoTool());
        registerModule(new FakeFPS());
        registerModule(new Teams());
        registerModule(new Regen());
        registerModule(new Blink());
        registerModule(new NoFall());
        registerModule(new FarmHunt());
        registerModule(new Fly());
        registerModule(new AutoPot());
        registerModule(new AutoClicker());
        registerModule(new BedFucker());
        registerModule(new ChestStealer());
        registerModule(new MurderMystery());
        registerModule(new Eagle());
        registerModule(new Bobbing());
        registerModule(new LegitSpeed());
        registerModule(new FastPlace());
        registerModule(new InvCleaner());
        registerModule(new LightningCheck());
        //registerModule(new Debug());
        registerModule(new Criticals());
        registerModule(new DMGParticle());
        registerModule(new Reach());
        registerModule(new NoWeather());
        registerModule(new WTap());
        registerModule(new MLG());
        registerModule(new ItemPhysics());
        registerModule(new AutoDoor());
        registerModule(new Tracers());
        registerModule(new ItemESP());
        registerModule(new PickupFilter());
        registerModule(new StorageESP());
        registerModule(new NoJumpDelay());
        registerModule(new ClientSpoof());
        registerModule(new SafeWalk());
        registerModule(new BowAimBot());
        registerModule(new AimAssist());
        registerModule(new AntiVoid());
        registerModule(new TNTCountdown());
        registerModule(new Trajectories());
        registerModule(new Crasher());
        registerModule(new StaffAnalyser());
        registerModule(new KeepSprint());
        registerModule(new TPAura());
        registerModule(new FastDrop());
        registerModule(new Arrow());
        registerModule(new HUD());
//        if (Objects.equals(Client.instance.user, "cubk"))
//            registerModule(new AntiBan());
        sortModules();
        Client.instance.configLoader.readConfig();
        init = true;
    }

    @SuppressWarnings("all")
    public void sortModules() {
        moduleArrayList.sort((m1, m2) -> {
            if (m1.getName().toCharArray()[0] > m2.getName().toCharArray()[0]) {
                return 1;
            }
            return -1;
        });

    }
}
