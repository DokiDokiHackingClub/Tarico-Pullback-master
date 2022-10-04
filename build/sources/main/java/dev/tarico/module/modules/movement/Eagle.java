package dev.tarico.module.modules.movement;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.anim.Animation;
import dev.tarico.utils.anim.animator.Animator;
import dev.tarico.utils.client.Direction;
import dev.tarico.utils.client.EaseBackIn;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class Eagle extends Module {
    public static final NumberValue<Double> delay = new NumberValue<>("Delay", 50.0, 0.0, 200.0, 10.0);
    private final BooleanValue<Boolean> onlyBlocks = new BooleanValue<>("OnlyBlocks", true);
    private final BooleanValue<Boolean> onlyLookingDown = new BooleanValue<>("OnlyLookingDown", false);
    private boolean resetFlag = true;
    private final boolean wasOverBlock = false;
    private final Animator animator = new Animator();
    public static Animation openingAnimation;

    public Eagle() {
        super("Eagle", "Auto Snake when eagle", ModuleType.Movement);
    }

    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return getBlock(new BlockPos(player.posX, player.posY - 1.0d, player.posZ));
    }

    @Native
    @EventTarget
    @SuppressWarnings("unused")
    public void onUpdate(EventPreUpdate event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();
            BlockPos belowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
            if ((!onlyBlocks.getValue() || (heldItem != null && heldItem.getItem() instanceof ItemBlock))
                    && (!onlyLookingDown.getValue() || mc.thePlayer.rotationPitch > 45)
                    && mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                if (mc.theWorld.getBlockState(belowPlayer).getBlock() == Blocks.air) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else {
                if (!resetFlag) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                    resetFlag = true;
                }
            }
        }
    }

    @EventTarget
    public void onR2D(EventRender2D event) {
/*        int slot = this.getItemSlot(false);
        float height = (slot != -1 ? 35 : 17);

        GL11.glPushMatrix();
        this.animator.setMin(0).setMax(1).setSpeed(3.3f);

        float width = 30;
        float x = event.getResolution().getScaledWidth() / 2f - (width / 2);
        float y = event.getResolution().getScaledHeight() / 2f + 10;
        GL11.glTranslatef(x, y, 0);
        RenderUtil.scale(width / 2f, height / 2f, (float) openingAnimation.getOutput() + .6f, () -> {
            RoundedUtil.drawRound(0, 0, width, height, 4,
                    new Color(0, 0, 0, 80));
            if (slot != -1) {
                ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);

                if (itemStack.getItem() != null) {
                    RenderUtil.renderItemOnScreenNoDepth(itemStack, (int) (width / 2) - 8, 4);
                }
            }

            FontLoaders.P20.drawCenteredStringWithShadow(
                    String.valueOf(Math.max(getItemSlot(true), 0)),
                    width / 2, height - 13, -1
            );
        });
        GL11.glPopMatrix();*/
    }

    private int getItemSlot(boolean count) {
        int itemCount = (count ? 0 : -1);

        for (int i = 8; i >= 0; i--) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                if (count) {
                    itemCount += itemStack.stackSize;
                } else {
                    itemCount = i;
                }
            }
        }

        return itemCount;
    }

    @Override
    public void enable() {
        openingAnimation = new EaseBackIn(400, .4f, 2f);
        if(openingAnimation.getDirection() == Direction.BACKWARDS)
            openingAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void disable() {
        openingAnimation = new EaseBackIn(400, .4f, 2f);
        if (openingAnimation.getDirection() == Direction.BACKWARDS)
            openingAnimation.setDirection(Direction.FORWARDS);
    }
}


