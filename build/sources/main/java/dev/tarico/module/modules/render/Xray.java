package dev.tarico.module.modules.render;

import by.radioegor146.nativeobfuscator.Native;
import com.google.common.collect.Lists;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import dev.tarico.utils.render.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Xray extends Module {
    public static boolean isEnabled;

    public static List<Integer> blockIdList = Lists.newArrayList(10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154);
    public static List<BlockPos> blockPosList = new CopyOnWriteArrayList<>();
    public static NumberValue<Double> distance = new NumberValue<>("Distance", 42.0D, 12.0D, 64.0D, 4.0D);
    public static NumberValue<Double> delay = new NumberValue<>("Delay", 10.0D, 1.0D, 30.0D, 0.5D);
    public static BooleanValue<Boolean> update = new BooleanValue<>("Update", true);
    public static BooleanValue<Boolean> CoalOre = new BooleanValue<>("Coal Ore", true);
    public static BooleanValue<Boolean> RedStoneOre = new BooleanValue<>("RedStone Ore", true);
    public static BooleanValue<Boolean> IronOre = new BooleanValue<>("Iron Ore", true);
    public static BooleanValue<Boolean> GoldOre = new BooleanValue<>("Gold Ore", true);
    public static BooleanValue<Boolean> DiamondOre = new BooleanValue<>("Diamond Ore", true);
    public static BooleanValue<Boolean> EmeraldOre = new BooleanValue<>("Emerald Ore", true);
    public static BooleanValue<Boolean> LapisOre = new BooleanValue<>("Lapis Ore", true);
    private final TimerUtil timer = new TimerUtil();

    public Xray() {
        super("Xray", "Ore Chams", ModuleType.Utils);
    }

    public static HashSet<BlockPos> getBlocks() {
        return (HashSet<BlockPos>) Xray.blockPosList;
    }

    public static boolean containsID(final int id) {
        return Xray.blockPosList.contains(id);
    }

    public static Double getDistance() {
        return Xray.distance.getValue();
    }

    @Override
    public void enable() {
        onToggle(true);
    }

    @Override
    public void disable() {
        onToggle(false);
        timer.reset();
    }

    private void onToggle(boolean enabled) {
        blockPosList.clear();
        mc.renderGlobal.loadRenderers();
        isEnabled = enabled;
    }

    @Native
    @EventTarget
    public void update(final EventTick var1) {
        if (Xray.update.getValue() && this.timer.hasTimeElapsed((long) (1000.0 * Xray.delay.getValue()))) {
            mc.renderGlobal.loadRenderers();
            this.timer.reset();
        }
    }

    @Native
    @SubscribeEvent
    public void onRender3D(final RenderWorldLastEvent var1) {
        for (BlockPos pos : blockPosList) {
            if (mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= Xray.distance.getValue().intValue()) {
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block == Blocks.diamond_ore && Xray.DiamondOre.getValue()) {
                    this.render3D(pos, 0, 255, 255);
                }
                if (block == Blocks.iron_ore && Xray.IronOre.getValue()) {
                    this.render3D(pos, 225, 225, 225);
                }
                if (block == Blocks.lapis_ore && Xray.LapisOre.getValue()) {
                    this.render3D(pos, 0, 0, 255);
                }
                if (block == Blocks.redstone_ore && Xray.RedStoneOre.getValue()) {
                    this.render3D(pos, 255, 0, 0);
                }
                if (block == Blocks.coal_ore && Xray.CoalOre.getValue()) {
                    this.render3D(pos, 0, 30, 30);
                }
                if (block == Blocks.emerald_ore && Xray.EmeraldOre.getValue()) {
                    this.render3D(pos, 0, 255, 0);
                }
                if (block == Blocks.gold_ore && Xray.GoldOre.getValue()) {
                    this.render3D(pos, 255, 255, 0);
                }
            }
        }
    }

    private void render3D(final BlockPos var1, final int var2, final int pos, final int block) {
        RenderUtil.drawSolidBlockESP(var1, ColorUtils.getColor(var2, pos, block));
    }

}
