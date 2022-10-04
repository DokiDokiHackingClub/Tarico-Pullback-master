package dev.tarico.module.modules.fun;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.timer.TimeHelper;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S45PacketTitle;

public class AutoGG extends Module {
    public BooleanValue<Boolean> gg = new BooleanValue<>("GG", true);
    public BooleanValue<Boolean> hacking = new BooleanValue<>("AD", false);
    public BooleanValue<Boolean> autoplay = new BooleanValue<>("AutoPlay", true);
    public NumberValue<Double> autoPlayDelay = new NumberValue<>("Play Delay", 3000d, 500d, 10000d, 100d);
    public NumberValue<Double> delay = new NumberValue<>("Delay", 100d, 100d, 3000d, 100d);

    public boolean needSpeak = false;
    public boolean speaked = false;

    public TimeHelper timer = new TimeHelper();

    public String playCommand = "";
    public String lastTitle = "";

    public int win = 0;
    public int total = 0;
    public boolean display = false;

    public AutoGG() {
        super("AutoGG", "Auto send gg when game over", ModuleType.Fun);
    }

    @Override
    public void enable() {
        playCommand = "";
        setSuffix("Empty");
    }

    @Override
    public void disable() {
        this.display = false;
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer != null) {
            if (needSpeak) {
                if (!speaked && timer.isDelayComplete(delay.getValue())) {
                    speaked = true;
                    if (this.getState()) {
                        if (gg.getValue() && !mc.thePlayer.capabilities.isFlying) {
                            mc.thePlayer.sendChatMessage("/ac gg" + (hacking.getValue() ? " Get Vape at vape dot gg" : ""));
                        }
                    }
                }

                if (speaked) {
                    if (timer.isDelayComplete(autoPlayDelay.getValue() + delay.getValue())) {
                        speaked = false;
                        needSpeak = false;
                        this.display = false;
                        if (autoplay.getValue() && this.getState()) mc.thePlayer.sendChatMessage(playCommand);
                    } else {
                        this.display = autoplay.getValue();
                    }
                }
            }
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onPacketR(EventPacketReceive e) {
        if (e.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) e.getPacket();
            String title = packet.getMessage().getFormattedText();

            if ((title.startsWith("\2476\247l") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYOU") && title.endsWith("\247r")) || (title.startsWith("\247c\247lGame") && title.endsWith("\247r")) || (title.startsWith("\247c\247lWITH") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYARR") && title.endsWith("\247r"))) {
                total++;
            }

            if (title.startsWith("\2476\247l") && title.endsWith("\247r")) {
                win++;
            }

            if (title.startsWith("\2476\247l") && title.endsWith("\247r") || title.startsWith("\247c\247lY") && title.endsWith("\247r")) {
                timer.reset();
                needSpeak = true;
            }

            lastTitle = title;
        }
    }

    @EventTarget
    public void onPacket(EventPacketSend e) {
        if (playCommand.startsWith("/play ")) {
            String display = playCommand.replace("/play ", "").replace("_", " ");
            boolean nextUp = true;
            StringBuilder result = new StringBuilder();
            for (char c : display.toCharArray()) {
                if (c == ' ') {
                    nextUp = true;
                    result.append(" ");
                    continue;
                }
                if (nextUp) {
                    nextUp = false;
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
            }
            setSuffix(result.toString());
        } else {
            setSuffix("Empty");
        }

        if (e.getPacket() instanceof C0EPacketClickWindow) {
            C0EPacketClickWindow packet = (C0EPacketClickWindow) e.getPacket();
            String itemname = packet.getClickedItem().getDisplayName();
            if (packet.getClickedItem().getDisplayName().startsWith("\247a")) {
                int itemID = Item.getIdFromItem(packet.getClickedItem().getItem());
                if (itemID == 381 || itemID == 368) {
                    if (itemname.contains("空岛战争") || itemname.contains("SkyWars")) {
                        if (itemname.contains("双人") || itemname.contains("Doubles")) {
                            if (itemname.contains("普通") || itemname.contains("Normal")) {
                                playCommand = "/play teams_normal";
                            } else if (itemname.contains("疯狂") || itemname.contains("Insane")) {
                                playCommand = "/play teams_insane";
                            }
                        } else if (itemname.contains("单挑") || itemname.contains("Solo")) {
                            if (itemname.contains("普通") || itemname.contains("Normal")) {
                                playCommand = "/play solo_normal";
                            } else if (itemname.contains("疯狂") || itemname.contains("Insane")) {
                                playCommand = "/play solo_insane";
                            }
                        }
                    }
                } else if (itemID == 355) {
                    if (itemname.contains("起床战争") || itemname.contains("Bed Wars")) {
                        if (itemname.contains("4v4")) {
                            playCommand = "/play bedwars_four_four";
                        } else if (itemname.contains("3v3")) {
                            playCommand = "/play bedwars_four_three";
                        } else if (itemname.contains("双人模式") || itemname.contains("Doubles")) {
                            playCommand = "/play bedwars_eight_two";
                        } else if (itemname.contains("单挑") || itemname.contains("Solo")) {
                            playCommand = "/play bedwars_eight_one";
                        }
                    }
                }
            }
        } else if (e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
            if (packet.getMessage().startsWith("/play")) {
                playCommand = packet.getMessage();
            }
        }
    }

}