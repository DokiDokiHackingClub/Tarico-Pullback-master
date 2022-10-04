package dev.tarico.utils.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FakeData {
    public EntityLivingBase entity;
    public float FakeHealth;
    public int hurtTime;
    public ArrayList<ItemStack> itemStacks = new ArrayList<>();

    public double x = 0D;
    public double y = 0d;
    public double z = 0d;

    public void addVelocity(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public FakeData(EntityLivingBase e, float Health, int hurtTime) {
        entity = e;
        FakeHealth = Health;
        this.hurtTime = hurtTime;
        itemStacks.add(e.getHeldItem());
        if (e instanceof EntityPlayer) {
            itemStacks.addAll(Arrays.asList(((EntityPlayer) e).inventory.armorInventory));
        }
        int i = 0;
        if (random()) itemStacks.add(new ItemStack(Items.diamond_axe));
        if (random()) itemStacks.add(new ItemStack(Items.diamond_pickaxe));
        if (random()) itemStacks.add(new ItemStack(Items.diamond_shovel));
        while (i < 2) {
            if (random()) itemStacks.add(new ItemStack(Items.golden_apple));
            if (random()) itemStacks.add(new ItemStack(Items.ender_pearl));
            if (random()) itemStacks.add(new ItemStack(Items.experience_bottle));
            if (random()) {
                ItemStack itemStack = new ItemStack(Blocks.stone);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random()) {
                ItemStack itemStack = new ItemStack(Items.experience_bottle);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random()) {
                ItemStack itemStack = new ItemStack(Blocks.planks);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random()) {
                ItemStack itemStack = new ItemStack(Blocks.stone);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random()) {
                ItemStack itemStack = new ItemStack(Blocks.planks);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random()) {
                ItemStack itemStack = new ItemStack(Items.cooked_beef);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random() && random()) {
                ItemStack itemStack = new ItemStack(Blocks.glass);
                itemStack.stackSize = 64;
                itemStacks.add(itemStack);
            }
            if (random() && random()) {
                ItemStack itemStack = new ItemStack(Blocks.emerald_block);
                itemStack.stackSize = 20;
                itemStacks.add(itemStack);
            }
            i++;
        }
    }

    public static boolean random() {
        return new Random().nextBoolean();
    }
}
