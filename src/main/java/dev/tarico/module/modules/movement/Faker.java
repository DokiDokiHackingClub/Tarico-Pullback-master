
package dev.tarico.module.modules.movement;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.client.FakeData;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.SessionHelper;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Faker extends Module {
    public static ArrayList<FakeData> data = new ArrayList<>();
    public TimerUtil timer = new TimerUtil();
    int lag = 0;

    public Faker() {
        super("Faker", "Fake Bypasses", ModuleType.Movement);
    }

    public static FakeData getFakeData(EntityLivingBase target) {
        for (FakeData datas : data) {
            if (datas.entity == target)
                return datas;
        }
        return data.get(0);
    }

    private static double getSwordDamage(ItemStack itemStack) {
        double damage = 0.0;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = ((AttributeModifier) attributeModifier.get()).getAmount();
        }
        return damage + EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }

    @EventTarget
    public void onPacket(EventPacketReceive e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            lag++;
        }
    }

    @EventTarget
    public void onAttack(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            if (lag > 3)
                e.setCancelled(true);
        }
        if (e.getPacket() instanceof C02PacketUseEntity) {
            e.setCancelled(true);
            C02PacketUseEntity p = (C02PacketUseEntity) e.getPacket();
            Entity target = p.getEntityFromWorld(mc.theWorld);
            if (target instanceof EntityLivingBase) {
                data.add(new FakeData((EntityLivingBase) target, ((EntityLivingBase) target).getHealth(), ((EntityLivingBase) target).hurtTime));
                FakeData d = getFakeData((EntityLivingBase) target);

                d.hurtTime += 20;
                if (timer.hasTimeElapsed(new Random().nextInt(300), true)) {
                    d.FakeHealth = (float) (d.FakeHealth - getSwordDamage(mc.thePlayer.getItemInUse()));
                    d.addVelocity(2, 2, 2);
                    if (FakeData.random() && FakeData.random() && FakeData.random())
                        d.entity.swingItem();
                }

                if (d.FakeHealth <= 0) {
                    target.isDead = true;
                    Helper.sendMessageWithoutPrefix(String.format(EnumChatFormatting.GRAY + "%s " + EnumChatFormatting.YELLOW + "was killed by " + EnumChatFormatting.GRAY + "%s", d.entity.getName(), mc.thePlayer.getDisplayNameString()));
                    SessionHelper.SessionState.kills++;
                    for (ItemStack s : d.itemStacks) {
                        EntityItem item = new EntityItem(mc.theWorld, target.posX, target.posY, target.posZ);
                        item.setEntityItemStack(s);
                        mc.theWorld.joinEntityInSurroundings(item);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        for (FakeData d : data) {
            d.entity.hurtTime = d.hurtTime;
            d.entity.motionX += d.x;
            d.entity.motionY += d.y;
            d.entity.motionZ += d.z;
        }
    }
}
