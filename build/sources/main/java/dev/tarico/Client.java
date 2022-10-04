package dev.tarico;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventBus;
import dev.tarico.event.EventHandler;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.misc.EventKey;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.lua.api.ScriptLoader;
import dev.tarico.management.*;
import dev.tarico.module.auth.GuiLogin;
import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.auth.client.ServerPacketHandler;
import dev.tarico.module.auth.packet.client.CDisconnectPacket;
import dev.tarico.module.config.ConfigLoader;
import dev.tarico.module.config.ConfigManager;
import dev.tarico.module.gui.hud.HUDManager;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.fun.AutoL;
import dev.tarico.module.modules.movement.Faker;
import dev.tarico.utils.Unsafe;
import dev.tarico.utils.client.AbuseUtil;
import dev.tarico.utils.client.SessionHelper;
import dev.tarico.utils.client.VMCheck;
import dev.tarico.utils.forgeevent.ForgeEventManager;
import dev.tarico.utils.render.Colors;
import me.cubk.plugin.PluginAPI;
import me.cubk.plugin.PluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.awt.*;
import java.io.File;

import static dev.tarico.utils.client.Helper.mc;

public class Client {

    public static Client instance;
    public static String server = "45.133.119.163";
    public final String name = "Tarico";
    public static boolean noNative = true;
    public final float version = 220825;
    public static boolean call = false;
    public static boolean scare = false;
    public ConfigLoader configLoader = new ConfigLoader("Client");
    public final CommandManager commandManager = new CommandManager();

    public final FriendManager friendManager = new FriendManager();
    public Colors mainColor = Colors.INDIGO;
    public static String user = "hi";

    public final PluginAPI api = PluginAPI.getApi(pluginLoader -> new PluginLoader(), "PluginAPI");

    private float renderDeltaTime;


    public Client(String user) {
        if(!call)
            exit();
        if (ServerPacketHandler.isPacketed) {
            instance = this;
            this.user = user;
            if(user == "Fadouse"){
                this.user = "Developer";
            }
            if (!GuiLogin.isLogin())
                exit();
            startDetection();
            start();
        }
    }

    protected void startDetection() {
        new Thread(() -> {
            try {
                while (true) {
                    if (VMCheck.getInstance().runChecks()) {
                    }
                    Thread.sleep(5000L);
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    @Native
    public static double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * getTimer().timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }


    @Native
    public static void exit() {
        Unsafe.theUnsafe.putAddress(0,0);
    }

    public static void attack() {
        Object[] o = null;
        while (true)
            o = new Object[]{o};

    }

    @Native
    public static Timer getTimer() {
        return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
    }

    public static int rainbowDraw(long speed, long delay) {
        long time = System.currentTimeMillis() + delay;
        return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed), 1.0F, 1.0F).getRGB();
    }

    public void setRenderDeltaTime(float renderDeltaTime) {
        this.renderDeltaTime = renderDeltaTime;
    }

    @Native
    public float getRenderDeltaTime() {
        return renderDeltaTime;
    }

    public static void displayGuiScreen(GuiScreen guiScreenIn) {
        mc.currentScreen = guiScreenIn;
        if (guiScreenIn != null) {
            try {
                mc.setIngameNotInFocus();
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                mc.currentScreen.mc = Minecraft.getMinecraft();
                mc.currentScreen.height = j;
                mc.currentScreen.width = i;
                mc.currentScreen.initGui();
                mc.skipRenderWorld = false;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Native
    public void start() {
        commandManager.init();
        api.loadAll(new File(mc.mcDataDir,"Tarico/Plugins"));
        new EventHandler();
        ForgeEventManager.EVENT_BUS.register(this);
        HUDManager.init();
        ModuleManager.instance.init();
        friendManager.init();
        EventBus.getInstance().register(this);
        CapeManager.capeLocation = new ResourceLocation("cape/cape.png");
        FileManager.loadPickupFilter();
        ConfigManager.reload();
        ModuleManager.instance.getModule(Faker.class).setState(false);
        ScriptLoader.init();
    }

    public void relaodPlugin(){
        dev.tarico.api.pluginapi.PluginAPI.moduleManager.unLoad();
        dev.tarico.api.pluginapi.PluginAPI.commandManager.clear();
        api.unloadAll(new File(mc.mcDataDir,"Tarico/Plugins"));
        api.loadAll(new File(mc.mcDataDir,"Tarico/Plugins"));
        NotificationsManager.addNotification(new Notification("Reloaded!", Notification.Type.Info));
    }

    @Native
    public void stop() {
        ClientImpl.sendPacket(new CDisconnectPacket());
        Client.instance.configLoader.saveSetting();
    }

    @EventTarget
    @Native
    public void onPacket(EventPacketReceive packetReceive) {
        if (packetReceive.getPacket() instanceof S02PacketChat) {
            String msg = ((S02PacketChat) packetReceive.getPacket()).getChatComponent().getUnformattedText();
            if (msg.contains("by " + Minecraft.getMinecraft().thePlayer.getName()) || msg.contains("被" + Minecraft.getMinecraft().thePlayer.getName()) || msg.contains(Minecraft.getMinecraft().thePlayer.getName() + "的")) {
                SessionHelper.SessionState.kills++;
                if (ModuleManager.instance.getModule(AutoL.class).getState()) {
                    if (AutoL.shout.getValue()) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/shout " + AbuseUtil.getL());
                    } else Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac " + AbuseUtil.getL());
                }
            } else if (msg.contains("You won!") || msg.contains("coins! (Win)") || msg.contains("你胜利了") || msg.contains("你赢了") || msg.contains("再来一场"))
                SessionHelper.SessionState.wins++;
        }
    }

    @EventTarget
    @Native
    private void onKeyPress(EventKey e) {
        for (Module m : ModuleManager.instance.getModules()) {
            if (m.getKey() != e.getKey()) continue;
            m.toggle();
        }
    }
}
