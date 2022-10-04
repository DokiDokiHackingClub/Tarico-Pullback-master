package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.client.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpeedMine extends Module {

    public static NumberValue<Double> speed = new NumberValue<>("Speed", 1.4D, 1.0D, 2.0D, 0.1D);
    public BlockPos blockPos;
    public EnumFacing facing;
    private boolean bzs = false;
    private float bzx = 0.0F;

    public SpeedMine() {
        super("SpeedMine", "Digging Faster", ModuleType.Utils);
    }


    @EventTarget
    public void onDamageBlock(EventPacketSend event) {
        if (event.getPacket() instanceof C07PacketPlayerDigging && !mc.playerController.extendedReach() && mc.playerController != null) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging) event.getPacket();
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                bzs = true;
                blockPos = c07PacketPlayerDigging.getPosition();
                facing = c07PacketPlayerDigging.getFacing();
                bzx = 0;
            } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                bzs = false;
                blockPos = null;
                facing = null;
            }
        }
    }

    @EventTarget
    public void onUpdate(EventPreUpdate event) {// curBlockDamageMP : field_78770_f
        if (((float) ReflectionUtil.getFieldValue(mc.playerController, "curBlockDamageMP", "field_78770_f")) >= speed.getValue().floatValue()) {
            ReflectionUtil.setFieldValue(mc.playerController, 1, "curBlockDamageMP", "field_78770_f");
            boolean item = mc.thePlayer.getCurrentEquippedItem() == null;
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 20, item ? 1 : 0));
        }
        if (mc.playerController.extendedReach()) {// field_78781_i
            ReflectionUtil.setFieldValue(mc.playerController, 0, "blockHitDelay", "field_78781_i");
        } else {
            if (bzs) {
                Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                bzx = (float) bzx + (float) (block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) * speed.getValue().floatValue());
                if (bzx >= 1.0F) {
                    mc.theWorld.setBlockState(blockPos, Blocks.air.getDefaultState(), 11);
                    PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing));
                    bzx = 0.0F;
                    bzs = false;
                }
            }
        }
    }


}