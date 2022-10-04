package dev.tarico.module.modules.combat;

import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.path.MainPathFinder;
import dev.tarico.utils.path.Vec3;
import dev.tarico.utils.timer.CPSDelay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TPAura extends Module {

    public static EntityLivingBase target;
    static ArrayList<EntityLivingBase> targets = new ArrayList<>();
    private final ModeValue<Enum<TPMode>> mode = new ModeValue<>("Mode", TPMode.values(), TPMode.Single);
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 5D, 1D, 20D, 1D);
    private final NumberValue<Double> range = new NumberValue<>("Range", 20D, 4D, 120D, 1D);
    private final NumberValue<Double> timerSpeed = new NumberValue<>("Timer", 1D, 0.1D, 3D, 0.1);
    private final NumberValue<Double> maxTargets = new NumberValue<>("Max Targets", 25D, 2D, 50D, 1D);
    private final BooleanValue<Boolean> groundCheck = new BooleanValue<>("GroundCheck", true);
    private final BooleanValue<Boolean> players = new BooleanValue<>("Players", true);
    private final BooleanValue<Boolean> animals = new BooleanValue<>("Animals", true);
    private final BooleanValue<Boolean> mobs = new BooleanValue<>("Mobs", true);
    private final BooleanValue<Boolean> invis = new BooleanValue<>("Invisibles", false);
    private final CPSDelay timer = new CPSDelay();
    private ArrayList<Vec3> path;

    public TPAura() {
        super("TPAura", "KillAura but inf range", ModuleType.Combat);
    }

    public void sortTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entLiving = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entLiving) < range.getValue() && entLiving != mc.thePlayer && !entLiving.isDead && isValid(entLiving)) {
                    targets.add(entLiving);
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onPreMotion(EventPreUpdate event) {
        setSuffix(mode.getValue().name());
        if (!timer.shouldAttack(cps.getValue().intValue()))
            return;

        sortTargets();

        final int maxTargets = (int) Math.round(this.maxTargets.getValue());

        if (mode.getValue() != TPMode.Multi && targets.size() > maxTargets)
            targets.subList(maxTargets, targets.size()).clear();


        if (targets.isEmpty()) {
            target = null;
            return;
        }

        target = targets.get(0);

        final EntityPlayer player = mc.thePlayer;

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        final double targetX = target.posX;
        final double targetY = target.posY;
        final double targetZ = target.posZ;

        Client.getTimer().timerSpeed = timerSpeed.getValue().floatValue();

        final double finalZ = z;
        final double finalY = y;
        final double finalX = x;
        new Thread(() -> {
            if (mode.getValue() == TPMode.Single) {
                path = MainPathFinder.computePath(new Vec3(finalX, finalY, finalZ), new Vec3(targetX, targetY, targetZ));

                for (final Vec3 vec : path)
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec.getX(), vec.getY(), vec.getZ(), false));

                mc.thePlayer.swingItem();

                PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

                Collections.reverse(path);

                for (final Vec3 vec : path)
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec.getX(), vec.getY(), vec.getZ(), false));
            } else if (mode.getValue() == TPMode.Multi) {
                for (final Entity entity : targets) {
                    path = MainPathFinder.computePath(new Vec3(finalX, finalY, finalZ), new Vec3(entity.posX, entity.posY, entity.posZ));

                    for (final Vec3 vec : path)
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec.getX(), vec.getY(), vec.getZ(), true));

                    mc.thePlayer.swingItem();

                    PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

                    Collections.reverse(path);

                    for (final Vec3 vec : path)
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec.getX(), vec.getY(), vec.getZ(), true));
                }
            }
        }).start();
        targets.clear();
    }

    public boolean isValid(EntityLivingBase ent) {
        if (AntiBot.isServerBot(ent))
            return false;
        if (Teams.isOnSameTeam(ent))
            return false;
        if (ent instanceof EntityPlayer && !players.getValue())
            return false;
        if (ent instanceof EntityMob && !mobs.getValue())
            return false;
        if (ent instanceof EntityAnimal && !animals.getValue())
            return false;
        if (ent.isInvisible() && !invis.getValue())
            return false;
        if (!ent.onGround && groundCheck.getValue())
            return false;
        if (ent.getHealth() <= 0)
            return false;
        if (ent.isDead) {
            target = null;
            return false;
        }
        return true;
    }

    @Override
    public void disable() {
        Client.getTimer().timerSpeed = 1;
        target = null;
    }

    enum TPMode {
        Single,
        Multi
    }
}