package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BedFucker extends Module {
    private final NumberValue<Double> radius = new NumberValue<>("Radius", 4.5, 1.0, 6.0, 0.1);
    private final ModeValue<Enum<Break>> modeValue = new ModeValue<>("Break Target Mode", Break.values(), Break.Bed);

    public BedFucker() {
        super("BedFucker", "Auto Destroying beds", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onTick(EventTick e) {
        setSuffix(modeValue.getValue());
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onUpdate(EventPreUpdate event) {
        int x = -radius.getValue().intValue();
        while (x < radius.getValue().intValue()) {
            int y = radius.getValue().intValue();
            while (y > -radius.getValue().intValue()) {
                int z = -radius.getValue().intValue();
                while (z < radius.getValue().intValue()) {
                    int xPos = (int) mc.thePlayer.posX + x;
                    int yPos = (int) mc.thePlayer.posY + y;
                    int zPos = (int) mc.thePlayer.posZ + z;
                    BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                    if (block.getBlockState().getBlock() == Block.getBlockById(92) && this.modeValue.getValue() == Break.Cake) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.swingItem();
                    } else if (block.getBlockState().getBlock() == Block.getBlockById(122) && this.modeValue.getValue() == Break.Egg) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.swingItem();
                    } else if (block.getBlockState().getBlock() == Block.getBlockById(26) && this.modeValue.getValue() == Break.Bed) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.swingItem();
                    }
                    ++z;
                }
                --y;
            }
            ++x;
        }
    }

    enum Break {
        Cake,
        Egg,
        Bed
    }
}
