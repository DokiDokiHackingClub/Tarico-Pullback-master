package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPostUpdate;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.MoveUtils;
import dev.tarico.utils.client.PacketUtils;
import dev.tarico.utils.client.PlayerUtil;
import dev.tarico.utils.client.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Scaffold extends Module {
    public final static List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace,
            Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air,
            Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer,
            Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever,
            Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
            Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab,
            Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower,
            Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder,
            Blocks.web);
    public static final BooleanValue<Boolean> safe = new BooleanValue<>("Safewalk", true);
    static final BooleanValue<Boolean> sprint = new BooleanValue<>("Sprint", false);
    static final BooleanValue<Boolean> swing = new BooleanValue<>("Swing", false);
    static final BooleanValue<Boolean> speedcheck = new BooleanValue<>("Speed Check", true);
    public static final RotationUtil.Rotation rotation = new RotationUtil.Rotation(999.0f, 999.0f);

    static {
        GLAllocation.createDirectFloatBuffer(16);
        new Vec3(0.20000000298023224, 1.0, -0.699999988079071).normalize();
        new Vec3(-0.20000000298023224, 1.0, 0.699999988079071).normalize();
    }
    private final NumberValue<Double> sprintOffset = new NumberValue<>("Offset", 0.75, 0.1, 1.0, 0.01);
    private final NumberValue<Double> ticks = new NumberValue<>("TowerTick", 10.0, 2.0, 50.0, 1.0);
    private final BooleanValue<Boolean> tower = new BooleanValue<>("Tower", false);
    private final BooleanValue<Boolean> towerboost = new BooleanValue<>("Boost", false);

    private final BooleanValue<Boolean> telly = new BooleanValue<>("Telly", false);
    // private final BooleanValue<Boolean> swing = new BooleanValue<>("Swing", true);
    int yValue = 0;
    private BlockData data;
    private int slot;
    private int towerTick;

    public Scaffold() {
        super("Scaffold", "Auto Place block", ModuleType.Movement);
        this.inVape = true;
    }

    @Native
    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3, -0.3);
            z += randomNumber(0.3, -0.3);
        } else {
            y += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    public static int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack()
                    || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack()
                    .getItem() instanceof ItemBlock))
                continue;
            return i;
        }
        return -1;
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    @Native
    @EventTarget
    public void onPre(EventPreUpdate event) {
        if (getBlockCount() < 1) {
            setState(false);
            return;
        }

        // 你要是会飞就没必要KeepY Scaffold了
        if (telly.getValue() && (MathHelper.floor_double(mc.thePlayer.posY) < yValue || MathHelper.floor_double(mc.thePlayer.posY) > (yValue + 3)))
            setState(false);

        if (telly.getValue() && yValue != MathHelper.floor_double(mc.thePlayer.posY))
            return;

        if ((speedcheck.getValue() && mc.thePlayer.isPotionActive(Potion.moveSpeed)) || telly.getValue()) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * 0.8F;
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * 0.8F;
        }

        if ((speedcheck.getValue() && mc.thePlayer.isSprinting())) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * sprintOffset.getValue().floatValue();
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * sprintOffset.getValue().floatValue();
        }

        if (!sprint.getValue()) {
            mc.thePlayer.setSprinting(false);
        }
        if (this.getBlockCount() <= 0 && this.getallBlockCount() <= 0) {
            return;
        }
        if (this.getBlockCount() <= 0) {
            int spoofSlot = this.getBestSpoofSlot();
            this.getBlock(spoofSlot);
        }
        this.data = this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0,
                mc.thePlayer.posZ)) == null
                ? this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0,
                mc.thePlayer.posZ).down(1))
                : this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0,
                mc.thePlayer.posZ));
        this.slot = getBlockSlot();
        mc.thePlayer.inventoryContainer.getSlot(slot + 36).getStack();
        if (this.data == null || this.slot == -1 || this.getBlockCount() <= 0
                || !(MoveUtils.isMoving() || mc.gameSettings.keyBindJump.isKeyDown())) {
            return;
        }
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX,
                mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
            float rot = 0.0f;
            if (mc.thePlayer.movementInput.moveForward > 0.0f) {
                rot = 180.0f;
                if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
                    rot = -120.0f;
                } else if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
                    rot = 120.0f;
                }
            } else if (mc.thePlayer.movementInput.moveForward == 0.0f) {
                rot = 180.0f;
                if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
                    rot = -90.0f;
                } else if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
                    rot = 90.0f;
                }
            } else if (mc.thePlayer.movementInput.moveForward < 0.0f) {
                if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
                    rot = -45.0f;
                } else if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
                    rot = 45.0f;
                }
            }
            if (PlayerUtil.isAirUnder(mc.thePlayer) && mc.gameSettings.keyBindJump.isKeyDown()
                    && !PlayerUtil.MovementInput() && this.tower.getValue()) {
                rot = 180.0f;
            }
            rotation.setYaw(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - rot);
            rotation.setPitch(87.5f);
        }
        if (rotation.getYaw() != 999.0f) {
            mc.thePlayer.rotationYawHead = rotation.getYaw();
            mc.thePlayer.renderYawOffset = rotation.getYaw();
            event.setYaw(rotation.getYaw());
        }
        if (rotation.getPitch() != 999.0f) {
            event.setPitch(rotation.getPitch());
        }
        if (PlayerUtil.isAirUnder(mc.thePlayer) && MoveUtils.isOnGround(1.15)
                && mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtil.MovementInput()
                && this.tower.getValue()) {
            if (this.towerboost.getValue())
                Client.getTimer().timerSpeed = 2.1078f;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.movementInput.moveForward = 0.0f;
            mc.thePlayer.movementInput.moveStrafe = 0.0f;
            if (++this.towerTick < ticks.getValue()) {
                mc.thePlayer.jump();
            } else {
                this.towerTick = 0;
            }
        }
        if (Client.getTimer().timerSpeed == 2.1078f) {
            Client.getTimer().timerSpeed = 1.0f;
        }
        if (getBlockCount() < 1) {
            setState(false);
        }
    }

    @Native
    @EventTarget
    public void onPost(EventPostUpdate e) {
        if (getBlockCount() < 1) {
            setState(false);
            return;
        }
        if (telly.getValue() && yValue != MathHelper.floor_double(mc.thePlayer.posY))
            return;

        int last = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.inventory.currentItem = this.slot;

        if (data != null) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getEnumFacing(),
                    getVec3(this.data.getBlockPos(), this.data.getEnumFacing()))) {
                if (mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem() != null
                        && (mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)
                        .getItem() instanceof ItemBlock)
                        && !mc.isSingleplayer()) {
                    mc.thePlayer.inventory
                            .getStackInSlot(mc.thePlayer.inventory.currentItem).getItem();
                }
                if(swing.getValue()){
                    mc.thePlayer.swingItem();
                }else{
                    PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
                }
            }
            if (getBlockCount() < 1) {
                setState(false);
                return;
            }
            mc.thePlayer.inventory.currentItem = last;
        }
    }

    @Override
    public void enable() {
        if (getBlockCount() < 1) {
            setState(false);
        }
        yValue = MathHelper.floor_double(mc.thePlayer.posY);
        this.data = null;
        this.slot = -1;
        rotation.setYaw(999.0f);
        rotation.setPitch(999.0f);
        this.towerTick = 0;
    }

    @Override
    public void disable() {

        rotation.setYaw(999.0f);
        rotation.setPitch(999.0f);
        Client.getTimer().timerSpeed = 1.0f;
    }

    @Native
    private BlockData getBlockData(BlockPos pos) {
        if (this.isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(-2, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(2, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, 2);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, -2);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (this.isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (this.isPosSolid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (this.isPosSolid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (this.isPosSolid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (this.isPosSolid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque()
                || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow
                || block instanceof BlockSkull) && !block.getMaterial().isLiquid()
                && !(block instanceof BlockContainer);
    }

    public int getBlockCount() {
        int n = 0;
        int i = 36;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }

    public int getallBlockCount() {
        int n = 0;
        int i = 0;
        while (i < 36) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }

    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) item).getBlock());
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    void getBlock(int hotbarSlot) {
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) is.getItem();
                    if (isValid(block)) {
                        if (36 + hotbarSlot != i) {
                            this.swap(i, hotbarSlot);
                        }
                        break;
                    }
                }
            }
        }

    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;

        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }
        }

        return spoofSlot;
    }

    private static class BlockData {
        private final BlockPos pos;
        private final EnumFacing facing;

        public BlockData(final BlockPos pos, final EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public BlockPos getBlockPos() {
            return this.pos;
        }

        public EnumFacing getEnumFacing() {
            return this.facing;
        }
    }
}

