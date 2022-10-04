package dev.tarico.module.modules.fun;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.misc.EventLoadWorld;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.combat.AntiBot;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MurderMystery extends Module {

    public static boolean a1;
    public static int a2;
    private static List<EntityPlayer> murders;
    private static List<EntityPlayer> bw;

    static {
        MurderMystery.a1 = false;
        MurderMystery.a2 = 0;
        MurderMystery.murders = new ArrayList<>();
        MurderMystery.bw = new ArrayList<>();
    }

    public MurderMystery() {
        super("MurderMystery", "Assistance MurderMystery Game", ModuleType.Fun);
    }

    @Override
    public void enable() {
        mc.displayGuiScreen(null);
    }

    @Override
    public void disable() {
        MurderMystery.a1 = false;
        MurderMystery.a2 = 0;
    }

    @Native
    @SubscribeEvent
    public void o(final RenderWorldLastEvent ev) {
        for (final EntityPlayer en : mc.theWorld.playerEntities) {
            if (en != mc.thePlayer && !en.isInvisible() && !AntiBot.isServerBot(en)) {
                if (en.getHeldItem() != null && en.getHeldItem().hasDisplayName()) {
                    final Item i = en.getHeldItem().getItem();
                    if (i instanceof ItemSword || i instanceof ItemAxe || en.getHeldItem().getDisplayName().replaceAll("§", "").equals("aKnife")) {
                        if (!MurderMystery.murders.contains(en)) {
                            MurderMystery.murders.add(en);
                            mc.thePlayer.playSound("note.pling", 1.0f, 1.0f);
                            Helper.sendMessage(en.getName() + " is the murderer!");
                        }
                    } else if (i instanceof ItemBow && !MurderMystery.bw.contains(en)) {
                        MurderMystery.bw.add(en);
                        Helper.sendMessage("[WARNING]" + en.getName() + " have bow! he maybe will kill you.");
                    }
                }
            }
        }

        for (EntityPlayer entityPlayer : murders) {
            RenderHelper.drawESP(entityPlayer, new Color(255, 0, 0).getRGB(), false, 3);
        }
    }

    private void clear() {
        if (MurderMystery.murders.size() > 0) {
            MurderMystery.murders.clear();
        }
        if (MurderMystery.bw.size() > 0) {
            MurderMystery.bw.clear();
        }
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onWorld(EventLoadWorld e) {
        clear();
    }

}
