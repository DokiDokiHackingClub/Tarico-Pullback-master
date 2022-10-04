package dev.tarico.module.modules;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventBus;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.value.Value;
import dev.tarico.utils.forgeevent.ForgeEventManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

import static dev.tarico.module.modules.utils.ClientSpoof.modeValue;

public class Module {

    public static final Minecraft mc = Minecraft.getMinecraft();
    public boolean state = false;
    public int key = 0;
    public String name;
    public String vapeName = null;
    public String vapeSuffix = null;
    public boolean inVape = false;
    public String dec;
    public ModuleType moduleType;
    public boolean noToggle = false;
    public List<Value<?>> values;
    private String suffix;
    private boolean removed;
    public boolean hidden = false;
    public boolean openValues;

    public boolean arraylistremove = true;
    public double animx = 0;
    public double animy = 0;

    public Module(String name, String dec, ModuleType moduleType) {
        this.name = name;
        this.dec = dec;
        this.vapeName = name;
        this.moduleType = moduleType;
        this.values = new ArrayList<>();
        this.removed = false;
        this.suffix = "";
    }


    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(Object obj) {
        String suffix = obj.toString();
        if (suffix.isEmpty()) {
            this.suffix = suffix;
        } else {
            this.suffix = String.format("§f %s§7", EnumChatFormatting.WHITE + suffix);
        }

    }

    public void setVSuffix(Object obj) {
        String suffix = obj.toString();
        if (suffix.isEmpty()) {
            this.vapeSuffix = suffix;
        } else {
            this.vapeSuffix = String.format("§f %s§7", EnumChatFormatting.GRAY + suffix);
        }

    }


    public String getDec() {
        return dec;
    }

    public List<Value<?>> getValues() {
        return this.values;
    }

    public void toggle() {
        this.setState(!this.state);
    }

    public void enable() {

    }

    public void disable() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
        if (ModuleManager.init) {
            Client.instance.configLoader.saveSetting();
        }
    }

    public boolean getState() {
        return state;
    }

    public boolean wasRemoved() {
        return this.removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean wasArrayRemoved() {
        return this.arraylistremove;
    }

    public void setArrayRemoved(boolean arraylistremove) {
        this.arraylistremove = arraylistremove;
    }

    public void setAnimx(double aanimx) {
        this.animx = aanimx;
    }

    public double getAnimx() {
        return this.animx;
    }

    public void setAnimy(double aanimy) {
        this.animy = aanimy;
    }

    public double getAnimy() {
        return this.animy;
    }

    public void setState(boolean state) {
        if (this.state == state) {
            return;
        }
        if (mc.theWorld != null) {
            mc.thePlayer.playSound("random.click", 0.3f, 0.5f);
        }
        this.state = state;
        if (state) {

            EventBus.getInstance().register(this);
            ForgeEventManager.EVENT_BUS.register(this);
            if (!noToggle && mc.thePlayer != null && mc.theWorld != null)
                NotificationsManager.addNotification(new Notification(getName() + " was Enabled.", Notification.Type.Info));
            enable();
        } else {

            EventBus.getInstance().unregister(this);
            ForgeEventManager.EVENT_BUS.unregister(this);
            if (!noToggle && mc.thePlayer != null && mc.theWorld != null)
                NotificationsManager.addNotification(new Notification(getName() + " was Disabled.", Notification.Type.Alert));
            disable();
        }
    }

    public ModuleType getCategory() {
        return moduleType;
    }

    @Native
    public Packet getSpoofPacket() {
        switch (modeValue.getModeAsString()) {
            case "Forge": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML")));
            }
            case "Lunar": {
                return (new C17PacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer())).writeString("Lunar-Client")));
            }
            case "LabyMod": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("LMC")));
            }
            case "PVP-L": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("PLC18")));
            }
            case "C-B": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("CB")));
            }
            case "Geyser": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Geyser")));
            }
        }

        return new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName()));
    }

}
