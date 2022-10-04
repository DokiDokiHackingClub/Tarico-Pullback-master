package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.gui.notification.Notification;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.JsonBridge;
import dev.tarico.utils.client.ServerUtil;
import dev.tarico.utils.timer.TimerUtil;
import net.minecraft.network.play.server.S02PacketChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StaffAnalyser extends Module {

    private final NumberValue<Double> delay = new NumberValue<>("Delay", 60.0, 1.0, 120.0, 1.0);

    private final TimerUtil timer = new TimerUtil();
    private int lastTimeAmountOfBans = -1;
    public static String key = null;

    public StaffAnalyser() {
        super("StaffAnalyser", "Go away bitch staff", ModuleType.Utils);
    }

    @Override
    public void enable() {
        lastTimeAmountOfBans = -1;
        timer.reset();
    }

    @EventTarget
    public void enable(EventPreUpdate e) {
      if (ServerUtil.INSTANCE.isOnHypixel() && timer.delay(120) && key == null) {
          mc.thePlayer.sendChatMessage("/api new");
          timer.reset();
      }
      if (mc.isSingleplayer() && !ServerUtil.INSTANCE.isOnHypixel()) {
          NotificationsManager.addNotification(new Notification(("StaffAnalyser only works on Hypixel!"), Notification.Type.Error));
          super.disable();
      }
    }

    @EventTarget
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packetChat = (S02PacketChat) e.getPacket();
            String chatMessage = packetChat.getChatComponent().getUnformattedText();
            if (chatMessage.matches("Your new API key is ........-....-....-....-............")) {
                e.setCancelled(true);
                key = chatMessage.replace("Your new API key is ", "");
            }
        }
    }

    @EventTarget
    public void onTick(EventTick e){
        if (timer.hasReached(delay.getValue() * 1000) || lastTimeAmountOfBans == -1) {
            new Thread(() -> {
                try {
                    if (key == null) {
                        Thread.sleep(3000);
                    }
                    final HttpURLConnection connection = (HttpURLConnection) new URL("https://api.hypixel.net/punishmentstats?key=" + key).openConnection();
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.47");
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    for (String read; (read = reader.readLine()) != null; ) {
                        if (read.startsWith("{\"success\":true")) {
                            final int staffBans = JsonBridge.parseString(read).getAsJsonObject().get("staff_total").getAsInt();
                            final int diff = staffBans - lastTimeAmountOfBans;
                            final int delay = (int) Math.round(this.delay.getValue());
                            if (lastTimeAmountOfBans != -1 && diff > 0)
                                NotificationsManager.addNotification(new Notification(diff + (diff == 1 ? " player has" : " players have") + " been staff banned in the past " + (delay == 1 ? "second" : delay + "s") + ".", Notification.Type.Alert));
                            lastTimeAmountOfBans = staffBans;
                        }
                    }
                } catch (final Exception ignored) {
                }
            }).start();
            timer.reset();
        }
    }
}
