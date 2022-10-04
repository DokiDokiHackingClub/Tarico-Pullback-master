package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.PlayerUtil;
import dev.tarico.utils.timer.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;

public class UHCHelper extends Module {

    private final BooleanValue<Boolean> noSandDamage = new BooleanValue<>("NoSandDamage", false);
    private final BooleanValue<Boolean> autoSneak = new BooleanValue<>("AutoSneak", false);
    private final BooleanValue<Boolean> noWaterDamage = new BooleanValue<>("NoWaterDamage", false);
    private final BooleanValue<Boolean> lightningCheck = new BooleanValue<>("LightningCheck", false);
    private final BooleanValue<Boolean> lessPacket = new BooleanValue<>("LessPacket", false);
    private final BooleanValue<Boolean> sandBreaker = new BooleanValue<>("SandBreaker", false);

    TimeHelper sneakTimer = new TimeHelper();
    boolean sneak;
    public static int x;
    public static int y;
    public static int z;

    public UHCHelper() {
        super("UHCHelper", "help your UHC game better", ModuleType.Utils);
    }

    @EventTarget
    public void onUpdate(final EventPreUpdate e) {
        final BlockPos sand = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 3.0, this.mc.thePlayer.posZ));
        final Block sandblock = this.mc.theWorld.getBlockState(sand).getBlock();
        final BlockPos forge = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 2.0, this.mc.thePlayer.posZ));
        final Block forgeblock = this.mc.theWorld.getBlockState(forge).getBlock();
        final BlockPos obsidianpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0, this.mc.thePlayer.posZ));
        final Block obsidianblock = this.mc.theWorld.getBlockState(obsidianpos).getBlock();
        if (obsidianblock == Block.getBlockById(49)) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (forgeblock == Block.getBlockById(61)) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (this.sandBreaker.getValue() && (sandblock == Block.getBlockById(12) || sandblock == Block.getBlockById(13))) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 3.0, this.mc.thePlayer.posZ));
            Helper.sendMessage(EnumChatFormatting.DARK_PURPLE + "Sand On your Head. Care for it.");
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (this.autoSneak.getValue() && !this.sneak && this.sneakTimer.isDelayComplete(2000L)) {
            this.sneak = true;
        }
        if (this.sneak) {}
        if (this.lessPacket.getValue()) {
            this.mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            this.mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
            if (this.mc.thePlayer.onGround) {
                final EntityPlayerSP thePlayer = this.mc.thePlayer;
                thePlayer.motionX *= 1.0049999952316284;
                final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                thePlayer2.motionZ *= 1.0049999952316284;
            }
        }
    }

    @EventTarget
    public void onPacketReceive(final EventPacketSend packetEvent) {
        if (this.noSandDamage.getValue() && packetEvent.packet instanceof C03PacketPlayer && this.isInsideBlock()) {
            packetEvent.setCancelled(true);
        }
        if (this.noWaterDamage.getValue() && packetEvent.packet instanceof C03PacketPlayer && PlayerUtil.isInWater() && Keyboard.isKeyDown(42)) {
            packetEvent.setCancelled(true);
        }
    }

    @EventTarget
    public void onPacketReceive(final EventPacketReceive packetEvent) {
        if (this.lightningCheck.getValue() && packetEvent.packet instanceof S2CPacketSpawnGlobalEntity) {
            final S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity)packetEvent.packet;
            if (packetIn.func_149053_g() == 1) {
                UHCHelper.x = (int)(packetIn.func_149051_d() / 32.0);
                UHCHelper.y = (int)(packetIn.func_149050_e() / 32.0);
                UHCHelper.z = (int)(packetIn.func_149049_f() / 32.0);
                Helper.sendMessage(EnumChatFormatting.DARK_PURPLE + "Found Lightning X:" + UHCHelper.x + "-Y:" + UHCHelper.y + "-Z:" + UHCHelper.z);
            }
        }
    }

    @Override
    public void enable() {
        if (this.noWaterDamage.getValue()) {
           Helper.sendMessage(EnumChatFormatting.DARK_PURPLE + "Lshift In Water To Enable Water GodMode");
        }
        sneakTimer.reset();
        super.enable();
    }

    @Override
    public void disable() {
        if (this.lessPacket.getValue()) {
            if (this.mc.thePlayer.capabilities.isCreativeMode) {
                this.mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
            }
            else {
                this.mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            }
        }
        sneakTimer.reset();
        super.disable();
    }

    private boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir) && block.isFullBlock()) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox((World)this.mc.theWorld, new BlockPos(x, y, z), this.mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                        }
                        if (boundingBox != null && this.mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void bestTool(final int x, final int y, final int z) {
        final int blockId = Block.getIdFromBlock(this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1.0f;
        for (int i1 = 36; i1 < 45; ++i1) {
            try {
                final ItemStack curSlot = this.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if ((curSlot.getItem() instanceof ItemTool || curSlot.getItem() instanceof ItemSword || curSlot.getItem() instanceof ItemShears) && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f) {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            }
            catch (Exception ex) {}
        }
        if (f != -1.0f) {
            this.mc.thePlayer.inventory.currentItem = bestSlot;
            this.mc.playerController.updateController();
        }
    }
}

